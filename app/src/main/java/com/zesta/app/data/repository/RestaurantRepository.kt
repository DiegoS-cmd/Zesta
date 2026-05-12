package com.zesta.app.data.repository

import com.zesta.app.R
import com.zesta.app.data.model.Product
import com.zesta.app.data.model.PromoType
import com.zesta.app.ui.screens.restaurant.Restaurant

object  RestaurantRepository {

    private val allRestaurants = listOf(
        Restaurant(
            id = 1, nameRes = R.string.restaurante_nombre_burger_king,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 20,
            ratingValue = 4.4, ratingCount = 170, imageRes = R.drawable.bk,
            categories = listOf("Hamburguesas"),
            addressRes = R.string.direccion_burger,
            products = listOf(
                Product(id = 1, nameRes = R.string.producto_whopper, price = 8.25, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.whopper, imageKey = "whopper"),
                Product(id = 2, nameRes = R.string.producto_chili_cheese, price = 10.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.chilicheese, imageKey = "chilicheese"),
                Product(id = 3, nameRes = R.string.producto_crazy_bacon, price = 7.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.crazybacon, imageKey = "crazybacon"),
                Product(id = 4, nameRes = R.string.producto_combo_wings, price = 9.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.doblecheeseburger, imageKey = "burgerkingicono")
            )
        ),
        Restaurant(
            id = 2, nameRes = R.string.restaurante_nombre_dominos,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 25,
            ratingValue = 4.2, ratingCount = 320, imageRes = R.drawable.dominos,
            categories = listOf("Pizzas"),
            addressRes = R.string.direccion_dominos,
            products = listOf(
                Product(id = 5, nameRes = R.string.producto_pizzaDominos_carbonara, price = 12.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.carbonaradominos, imageKey = "carbonaradominos"),
                Product(id = 6, nameRes = R.string.producto_pizzaDominos_BBQ, price = 13.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.bbqdominos, imageKey = "bbqdominos"),
                Product(id = 7, nameRes = R.string.producto_pizzaDominos_pepperoni, price = 11.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.pepperonidominos, imageKey = "pepperonidominos"),
                Product(id = 8, nameRes = R.string.producto_pizzaDominos_4quesos, price = 8.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.cuatroquesosdominos, imageKey = "cuatroquesosdominos")
            )
        ),
        Restaurant(
            id = 3, nameRes = R.string.restaurante_nombre_wingstop,
            hasFreeDelivery = false, deliveryFee = 2.50, deliveryTimeMinutes = 20,
            ratingValue = 4.8, ratingCount = 70, imageRes = R.drawable.wingstop,
            promoTextRes = R.string.promocion_compra_una_llevate_otra,
            categories = listOf("Hamburguesas", "Americana"),
            addressRes = R.string.direccion_wingstop,
            products = listOf(
                Product(id = 9, nameRes = R.string.producto_alitasBBQ_wingstop, price = 9.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstopalitas, imageKey = "wingstopalitas", promoType = PromoType.DOS_POR_UNO),
                Product(id = 10, nameRes = R.string.producto_combo_wings, price = 12.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstopcombo, imageKey = "wingstopcombo", promoType = PromoType.DOS_POR_UNO),
                Product(id = 11, nameRes = R.string.producto_alitas_wingstop, price = 7.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstopalitasnormal, imageKey = "wingstopalitasnormal"),
                Product(id = 12, nameRes = R.string.producto_patatas_cajun, price = 4.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstoppatatas, imageKey = "wingstoppatatas")
            )
        ),
        Restaurant(
            id = 4, nameRes = R.string.restaurante_nombre_mcdonalds,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 15,
            ratingValue = 4.1, ratingCount = 512, imageRes = R.drawable.mcdonalds,
            promoTextRes = R.string.promocion_2x1,
            addressRes = R.string.direccion_mcdonalds,
            products = listOf(
                Product(id = 13, nameRes = R.string.producto_bigMac_mc, price = 7.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcbigmac, imageKey = "mcbigmac", promoType = PromoType.DOS_POR_UNO),
                Product(id = 14, nameRes = R.string.producto_mcnuggets_mc, price = 6.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcnuggets, imageKey = "mcnuggets", promoType = PromoType.DOS_POR_UNO),
                Product(id = 15, nameRes = R.string.producto_mcflurry_mc, price = 3.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcmcflurry, imageKey = "mcmcflurry"),
                Product(id = 16, nameRes = R.string.producto_macExtrem_mc, price = 8.25, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcmacextreme, imageKey = "mcmacextreme")
            )
        ),
        Restaurant(
            id = 5, nameRes = R.string.restaurante_nombre_kfc,
            hasFreeDelivery = false, deliveryFee = 1.99, deliveryTimeMinutes = 25,
            ratingValue = 4.3, ratingCount = 248, imageRes = R.drawable.kfc,
            categories = listOf("Hamburguesas", "Americana"),
            addressRes = R.string.direccion_kfc,
            products = listOf(
                Product(id = 17, nameRes = R.string.producto_bucket_kfc, price = 14.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfcbucket, imageKey = "kfcbucket"),
                Product(id = 18, nameRes = R.string.producto_comboTiras_kfc, price = 6.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfccubotiras, imageKey = "kfccubotiras"),
                Product(id = 19, nameRes = R.string.producto_comboAlitas_kfc, price = 8.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfccuboalitas, imageKey = "kfccuboalitas"),
                Product(id = 20, nameRes = R.string.producto_comboAlitasPic_kfc, price = 11.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfccuboalitaspicantes, imageKey = "kfccuboalitaspicantes")
            )
        ),
        Restaurant(
            id = 6, nameRes = R.string.restaurante_nombre_subway,
            hasFreeDelivery = true, deliveryFee = 2.99, deliveryTimeMinutes = 20,
            ratingValue = 4.0, ratingCount = 189, imageRes = R.drawable.subway,
            promoTextRes = R.string.promocion_envio_gratis,
            categories = listOf("Panadería"),
            addressRes = R.string.direccion_subway,
            products = listOf(
                Product(id = 21, nameRes = R.string.producto_italiano_sub, price = 7.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subwayitaliano, imageKey = "subwayitaliano"),
                Product(id = 22, nameRes = R.string.producto_pollo_sub, price = 8.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subwaypollo, imageKey = "subwaypollo"),
                Product(id = 23, nameRes = R.string.producto_veggie_sub, price = 6.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subwayveggie, imageKey = "subwayveggie"),
                Product(id = 24, nameRes = R.string.producto_clubSandwich_sub, price = 9.25, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subwaysandwich, imageKey = "subwaysandwich")
            )
        ),
        Restaurant(
            id = 7, nameRes = R.string.restaurante_nombre_telepizza,
            hasFreeDelivery = false, deliveryFee = 2.99, deliveryTimeMinutes = 30,
            ratingValue = 3.9, ratingCount = 410, imageRes = R.drawable.telepizza,
            categories = listOf("Pizzas"),
            addressRes = R.string.direccion_telepizza,
            products = listOf(
                Product(id = 25, nameRes = R.string.producto_pizzaTelepizza_BBQ, price = 11.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizzabbq, imageKey = "telepizzabbq"),
                Product(id = 26, nameRes = R.string.producto_pizzaTelepizza_4quesos, price = 12.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizzacuatroquesos, imageKey = "telepizzacuatroquesos"),
                Product(id = 27, nameRes = R.string.producto_pizzaTelepizza_carbonara, price = 13.25, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizzacarbonara, imageKey = "telepizzacarbonara"),
                Product(id = 28, nameRes = R.string.producto_pizzaTelepizza_carnivora, price = 7.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizzacarnivora, imageKey = "telepizzacarnivora")
            )
        ),
        Restaurant(
            id = 8, nameRes = R.string.restaurante_nombre_papajohns,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 28,
            ratingValue = 4.2, ratingCount = 155, imageRes = R.drawable.papajohns,
            promoTextRes = R.string.promocion_descuento_20,
            categories = listOf("Pizzas"),
            addressRes = R.string.direccion_papajohns,
            products = listOf(
                Product(id = 29, nameRes = R.string.producto_pizzaGarden_Papa, price = 12.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papagarden, imageKey = "papagarden", promoType = PromoType.DESCUENTO_20),
                Product(id = 30, nameRes = R.string.producto_pizzaHawaiana_Papa, price = 11.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papahawaina, imageKey = "papahawaina", promoType = PromoType.DESCUENTO_20),
                Product(id = 31, nameRes = R.string.producto_patatas_Papa, price = 4.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papapatatas, imageKey = "papapatatas"),
                Product(id = 32, nameRes = R.string.producto_pizzaPeperonni_Papa, price = 13.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papapeperoni, imageKey = "papapeperoni")
            )
        ),
        Restaurant(
            id = 9, nameRes = R.string.restaurante_nombre_tacobell,
            hasFreeDelivery = false, deliveryFee = 1.50, deliveryTimeMinutes = 22,
            ratingValue = 4.5, ratingCount = 93, imageRes = R.drawable.tacobell,
            categories = listOf("Mexicana"),
            addressRes = R.string.direccion_tacobell,
            products = listOf(
                Product(id = 33, nameRes = R.string.producto_crunchWrap_taco, price = 5.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.tacosupreme, imageKey = "tacosupreme"),
                Product(id = 34, nameRes = R.string.producto_nachosBell_taco, price = 6.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.taconachos, imageKey = "taconachos"),
                Product(id = 35, nameRes = R.string.producto_quesadilla_taco, price = 4.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.tacoquesadilla, imageKey = "tacoquesadilla"),
                Product(id = 36, nameRes = R.string.producto_crunchWrapDelux_taco, price = 8.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.tacowrapdeluxe, imageKey = "tacowrapdeluxe")
            )
        ),
        Restaurant(
            id = 10, nameRes = R.string.restaurante_nombre_dunkin,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 18,
            ratingValue = 4.0, ratingCount = 207, imageRes = R.drawable.dunkin,
            promoTextRes = R.string.promocion_envio_gratis,
            categories = listOf("Desayuno", "Panadería"),
            addressRes = R.string.direccion_dunkin,
            products = listOf(
                Product(id = 37, nameRes = R.string.producto_donutGlass_dunkin, price = 2.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkinglaseado, imageKey = "dunkinglaseado"),
                Product(id = 38, nameRes = R.string.producto_cafeAmericano_dunkin, price = 3.20, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkinamericano, imageKey = "dunkinamericano"),
                Product(id = 39, nameRes = R.string.producto_tostadaJamon_dunkin, price = 4.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkintostada, imageKey = "dunkintostada"),
                Product(id = 40, nameRes = R.string.producto_donutLotus_dunkin, price = 3.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkinlotus, imageKey = "dunkinlotus")
            )
        ),
        Restaurant(
            id = 11, nameRes = R.string.restaurante_nombre_starbucks,
            hasFreeDelivery = false, deliveryFee = 2.50, deliveryTimeMinutes = 20,
            ratingValue = 4.6, ratingCount = 341, imageRes = R.drawable.starbucks,
            categories = listOf("Desayuno", "Panadería"),
            addressRes = R.string.direccion_starbucks,
            products = listOf(
                Product(id = 41, nameRes = R.string.producto_frappuccinoCar_star, price = 6.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbucksfrapuccionocaramelo, imageKey = "starbucksfrapuccionocaramelo"),
                Product(id = 42, nameRes = R.string.producto_latte_star, price = 4.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbuckscaffelatte, imageKey = "starbuckscaffelatte"),
                Product(id = 43, nameRes = R.string.producto_croissant_star, price = 3.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbuckscroissant, imageKey = "starbuckscroissant"),
                Product(id = 44, nameRes = R.string.producto_frappuccinoCookies_star, price = 3.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbucksfrapuccionocookies, imageKey = "starbucksfrapuccionocookies")
            )
        ),
        Restaurant(
            id = 12, nameRes = R.string.restaurante_nombre_fiveguys,
            hasFreeDelivery = false, deliveryFee = 3.50, deliveryTimeMinutes = 30,
            ratingValue = 4.7, ratingCount = 118, imageRes = R.drawable.fiveguys,
            promoTextRes = R.string.promocion_2x1,
            categories = listOf("Hamburguesas"),
            addressRes = R.string.direccion_fiveguys,
            products = listOf(
                Product(id = 45, nameRes = R.string.producto_burgerBac_fiveguys, price = 12.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguysbacon, imageKey = "fiveguysbacon", promoType = PromoType.DOS_POR_UNO),
                Product(id = 46, nameRes = R.string.producto_hotdogQueso_fiveguys, price = 8.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguysperrito, imageKey = "fiveguysperrito", promoType = PromoType.DOS_POR_UNO),
                Product(id = 47, nameRes = R.string.producto_sandwichVeg_fiveguys, price = 5.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguyssandwichvegetal, imageKey = "fiveguyssandwichvegetal"),
                Product(id = 48, nameRes = R.string.producto_sandwichQueso_fiveguys, price = 13.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguyssandwichqueso, imageKey = "fiveguyssandwichqueso")
            )
        ),
        Restaurant(
            id = 13, nameRes = R.string.restaurante_nombre_fosters,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 35,
            ratingValue = 4.3, ratingCount = 86, imageRes = R.drawable.fosters,
            categories = listOf("Hamburguesas", "Americana"),
            addressRes = R.string.direccion_foster,
            products = listOf(
                Product(id = 49, nameRes = R.string.producto_costillas_fosters, price = 16.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fostercostillas, imageKey = "fostercostillas"),
                Product(id = 50, nameRes = R.string.producto_burger_fosters, price = 11.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fosterpinkburguer, imageKey = "fosterpinkburguer"),
                Product(id = 51, nameRes = R.string.producto_combo_fosters, price = 7.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fostercombo, imageKey = "fostercombo"),
                Product(id = 52, nameRes = R.string.producto_sandwich_fosters, price = 9.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fostersandwich, imageKey = "fostersandwich")
            )
        ),
        Restaurant(
            id = 14, nameRes = R.string.restaurante_nombre_ginos,
            hasFreeDelivery = false, deliveryFee = 1.99, deliveryTimeMinutes = 28,
            ratingValue = 4.1, ratingCount = 143, imageRes = R.drawable.ginos,
            promoTextRes = R.string.promocion_descuento_20,
            categories = listOf("Pizzas"),
            addressRes = R.string.direccion_ginos,
            products = listOf(
                Product(id = 53, nameRes = R.string.producto_pizza_ginos, price = 13.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginosproscuitto, imageKey = "ginosproscuitto", promoType = PromoType.DESCUENTO_20),
                Product(id = 54, nameRes = R.string.producto_rigattoni_ginos, price = 10.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginosrigatonni, imageKey = "ginosrigatonni", promoType = PromoType.DESCUENTO_20),
                Product(id = 55, nameRes = R.string.producto_calzzone_ginos, price = 5.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginoscalzzone, imageKey = "ginoscalzzone"),
                Product(id = 56, nameRes = R.string.producto_ensalada_ginos, price = 12.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginosensalada, imageKey = "ginosensalada")
            )
        ),
        Restaurant(
            id = 15, nameRes = R.string.restaurante_nombre_vips,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 25,
            ratingValue = 4.2, ratingCount = 201, imageRes = R.drawable.vips,
            categories = listOf("Desayuno", "Americana"),
            addressRes = R.string.direccion_vips,
            products = listOf(
                Product(id = 57, nameRes = R.string.producto_sandwichclub_vips, price = 10.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vipsclub, imageKey = "vipsclub"),
                Product(id = 58, nameRes = R.string.producto_pancakes_vips, price = 8.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vipstortitas, imageKey = "vipstortitas"),
                Product(id = 59, nameRes = R.string.producto_nachos_vips, price = 5.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vipsnachos, imageKey = "vipsnachos"),
                Product(id = 60, nameRes = R.string.producto_ensalada_vips, price = 3.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vipsensalada, imageKey = "vipsensalada")
            )
        ),
        Restaurant(
            id = 16, nameRes = R.string.restaurante_nombre_montaditos,
            hasFreeDelivery = false, deliveryFee = 0.99, deliveryTimeMinutes = 20,
            ratingValue = 4.4, ratingCount = 377, imageRes = R.drawable.montaditos,
            promoTextRes = R.string.promocion_2x1,
            categories = listOf("Panadería"),
            addressRes = R.string.direccion_100mon,
            products = listOf(
                Product(id = 61, nameRes = R.string.producto_montaditoLomo_100Mon, price = 1.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.cienmonlomo, imageKey = "cienmonlomo", promoType = PromoType.DOS_POR_UNO),
                Product(id = 62, nameRes = R.string.producto_montaditoTortilla_100Mon, price = 1.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.cienmontortilla, imageKey = "cienmontortilla", promoType = PromoType.DOS_POR_UNO),
                Product(id = 63, nameRes = R.string.producto_comboMontaditos_100Mon, price = 9.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.cienmoncombo, imageKey = "cienmoncombo"),
                Product(id = 64, nameRes = R.string.producto_cheeseFries_100Mon, price = 3.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.cienmoncheese, imageKey = "cienmoncheese")
            )
        ),
        Restaurant(
            id = 17, nameRes = R.string.restaurante_nombre_goiko,
            hasFreeDelivery = false, deliveryFee = 2.99, deliveryTimeMinutes = 30,
            ratingValue = 4.7, ratingCount = 132, imageRes = R.drawable.goiko,
            categories = listOf("Hamburguesas"),
            addressRes = R.string.direccion_goiko,
            products = listOf(
                Product(id = 65, nameRes = R.string.producto_burger_goiko, price = 13.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goikoangus, imageKey = "goikoangus"),
                Product(id = 66, nameRes = R.string.producto_smash_goiko, price = 12.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goikosmash, imageKey = "goikosmash"),
                Product(id = 67, nameRes = R.string.producto_patatas_goiko, price = 5.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goikopatatas, imageKey = "goikopatatas"),
                Product(id = 68, nameRes = R.string.producto_kevin_goiko, price = 7.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goikokevin, imageKey = "goikokevin")
            )
        ),
        Restaurant(
            id = 18, nameRes = R.string.restaurante_nombre_sushishop,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 35,
            ratingValue = 4.6, ratingCount = 89, imageRes = R.drawable.sushishop,
            promoTextRes = R.string.promocion_envio_gratis,
            categories = listOf("Asiática"),
            addressRes = R.string.direccion_sushi,
            products = listOf(
                Product(id = 69, nameRes = R.string.producto_california_sushi, price = 9.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushicalifornia, imageKey = "sushicalifornia"),
                Product(id = 70, nameRes = R.string.producto_salmon_sushi, price = 7.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushinigiri, imageKey = "sushinigiri"),
                Product(id = 71, nameRes = R.string.producto_combo_sushi, price = 18.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushicombo, imageKey = "sushicombo"),
                Product(id = 72, nameRes = R.string.producto_maki_sushi, price = 6.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushimaki, imageKey = "sushimaki4")
            )
        )
    )

    fun getByCategory(category: String): List<Restaurant> =
        allRestaurants.filter { it.categories.any { cat -> cat.equals(category, ignoreCase = true) } }

    // Explorar muestra todos menos los destacados para evitar duplicados en pantalla
    fun getExploreRestaurants(): List<Restaurant> {
        val excludedIds = getFeaturedRestaurants().map { it.id }
        return allRestaurants.filter { it.id !in excludedIds }
    }
    fun getAllRestaurants(): List<Restaurant> = allRestaurants

    fun getRestaurantById(id: Int): Restaurant? = allRestaurants.find { it.id == id }

    // Los 6 primeros de la lista son los que aparecen en "Destacado en Zesta"
    fun getFeaturedRestaurants(): List<Restaurant> = allRestaurants.take(6)

    fun getPromoRestaurants(): List<Restaurant> = allRestaurants.filter { it.promoTextRes != null }

    // resolveName es necesario porque los nombres están en R.string, no como texto directo
    fun searchRestaurants(query: String, resolveName: (Int) -> String): List<Restaurant> {
        if (query.isBlank()) return allRestaurants
        return allRestaurants.filter { resolveName(it.nameRes).contains(query, ignoreCase = true) }
    }
}
