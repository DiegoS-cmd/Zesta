package com.zesta.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zesta.app.data.model.CartItem
import com.zesta.app.data.repository.CartRepository
import com.zesta.app.data.repository.RestaurantCartWithItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de la UI del carrito.
 *
 * @property carts Lista de carritos agrupados por restaurante.
 * @property isLoading true mientras se está cargando el carrito desde el repositorio.
 * @property errorMessage Mensaje de error de la última operación fallida, o null.
 * @property totalPrice Precio total calculado sumando precio × cantidad de todos los items.
 */
data class CartUiState(
    val carts: List<RestaurantCartWithItems> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val totalPrice: Double
        get() = carts.sumOf { cart ->
            cart.items.sumOf { it.precio * it.cantidad }
        }
}

/**
 * ViewModel del carrito de compra.
 *
 * Gestiona todas las operaciones sobre el carrito delegando en [CartRepository]
 * y exponiendo el estado a través de [uiState].
 * Tras cada operación exitosa recarga el carrito para mantener la UI sincronizada.
 *
 * @param repository Repositorio que gestiona la persistencia del carrito.
 */
class CartViewModel(
    private val repository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    /**
     * Carga el carrito completo desde el repositorio.
     * Se llama automáticamente al inicializar el ViewModel y tras cada operación exitosa.
     */
    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.getRestaurantCarts()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    carts = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    /**
     * Añade un producto al carrito del restaurante indicado.
     * Si el restaurante no tiene carrito todavía, el repositorio lo crea.
     *
     * @param restaurantId ID del restaurante al que pertenece el producto.
     * @param restaurantName Nombre del restaurante (se guarda junto al carrito).
     * @param restaurantImageResName Nombre del recurso de imagen del restaurante.
     * @param item Producto a añadir.
     * @param onSuccess Callback opcional al completar la operación con éxito.
     * @param onError Callback opcional con el mensaje de error si falla.
     */
    fun addItem(
        restaurantId: Int,
        restaurantName: String,
        restaurantImageResName: String,
        item: CartItem,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val result = repository.addItem(
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                restaurantImageResName = restaurantImageResName,
                item = item
            )

            if (result.isSuccess) {
                loadCart()
                onSuccess?.invoke()
            } else {
                val message = result.exceptionOrNull()?.message ?: "Error al añadir producto"
                _uiState.value = _uiState.value.copy(errorMessage = message)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Incrementa en 1 la cantidad de un producto en el carrito.
     *
     * @param restaurantId ID del restaurante al que pertenece el producto.
     * @param item Producto cuya cantidad se incrementa.
     * @param onSuccess Callback opcional al completar la operación con éxito.
     * @param onError Callback opcional con el mensaje de error si falla.
     */
    fun increaseQuantity(
        restaurantId: Int,
        item: CartItem,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val result = repository.increaseQuantity(restaurantId, item)

            if (result.isSuccess) {
                loadCart()
                onSuccess?.invoke()
            } else {
                val message = result.exceptionOrNull()?.message ?: "Error al aumentar cantidad"
                _uiState.value = _uiState.value.copy(errorMessage = message)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Decrementa en 1 la cantidad de un producto en el carrito.
     * Si la cantidad llega a 0, el repositorio elimina el producto.
     *
     * @param restaurantId ID del restaurante al que pertenece el producto.
     * @param item Producto cuya cantidad se decrementa.
     * @param onSuccess Callback opcional al completar la operación con éxito.
     * @param onError Callback opcional con el mensaje de error si falla.
     */
    fun decreaseQuantity(
        restaurantId: Int,
        item: CartItem,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val result = repository.decreaseQuantity(restaurantId, item)

            if (result.isSuccess) {
                loadCart()
                onSuccess?.invoke()
            } else {
                val message = result.exceptionOrNull()?.message ?: "Error al disminuir cantidad"
                _uiState.value = _uiState.value.copy(errorMessage = message)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Elimina un producto del carrito independientemente de su cantidad.
     *
     * @param restaurantId ID del restaurante al que pertenece el producto.
     * @param item Producto a eliminar.
     * @param onSuccess Callback opcional al completar la operación con éxito.
     * @param onError Callback opcional con el mensaje de error si falla.
     */
    fun removeItem(
        restaurantId: Int,
        item: CartItem,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val result = repository.removeItem(restaurantId, item)

            if (result.isSuccess) {
                loadCart()
                onSuccess?.invoke()
            } else {
                val message = result.exceptionOrNull()?.message ?: "Error al eliminar producto"
                _uiState.value = _uiState.value.copy(errorMessage = message)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Vacía el carrito completo eliminando todos los restaurantes y sus productos.
     *
     * @param onSuccess Callback opcional al completar la operación con éxito.
     * @param onError Callback opcional con el mensaje de error si falla.
     */
    fun clearCart(
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val result = repository.clearCart()

            if (result.isSuccess) {
                loadCart()
                onSuccess?.invoke()
            } else {
                val message = result.exceptionOrNull()?.message ?: "Error al vaciar carrito"
                _uiState.value = _uiState.value.copy(errorMessage = message)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Vacía únicamente el carrito del restaurante indicado, dejando intactos
     * los carritos de otros restaurantes.
     *
     * @param restaurantId ID del restaurante cuyo carrito se quiere vaciar.
     * @param onSuccess Callback opcional al completar la operación con éxito.
     * @param onError Callback opcional con el mensaje de error si falla.
     */
    fun clearCartByRestaurant(
        restaurantId: Int,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val result = repository.clearCartByRestaurant(restaurantId)

            if (result.isSuccess) {
                loadCart()
                onSuccess?.invoke()
            } else {
                val message = result.exceptionOrNull()?.message ?: "Error al vaciar carrito del restaurante"
                _uiState.value = _uiState.value.copy(errorMessage = message)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Limpia el mensaje de error del estado, por ejemplo tras mostrarlo en la UI.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * Factory para crear [CartViewModel] con su [CartRepository] inyectado.
 * Necesario porque el ViewModel tiene un constructor con parámetros.
 *
 * @param repository Repositorio que se inyecta en el ViewModel.
 */
class CartViewModelFactory(
    private val repository: CartRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}