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

class CartViewModel(
    private val repository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

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

    fun addItem(
        restaurantId: Int,
        restaurantName: String,
        restaurantImageResName: String,
        item: CartItem
    ) {
        viewModelScope.launch {
            val result = repository.addItem(
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                restaurantImageResName = restaurantImageResName,
                item = item
            )

            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message
                )
            }

            loadCart()
        }
    }


    fun increaseQuantity(restaurantId: Int, item: CartItem) {
        viewModelScope.launch {
            repository.increaseQuantity(restaurantId, item)
            loadCart()
        }
    }

    fun decreaseQuantity(restaurantId: Int, item: CartItem) {
        viewModelScope.launch {
            repository.decreaseQuantity(restaurantId, item)
            loadCart()
        }
    }

    fun removeItem(restaurantId: Int, item: CartItem) {
        viewModelScope.launch {
            repository.removeItem(restaurantId, item)
            loadCart()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
            loadCart()
        }
    }
}

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
