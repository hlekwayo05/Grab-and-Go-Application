// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grabngo2.data.model.Cafeteria
import com.example.grabngo2.data.model.MenuItem
import com.example.grabngo2.data.model.Order
import com.example.grabngo2.data.model.OrderItem
import com.example.grabngo2.data.repository.MenuRepository
import com.example.grabngo2.data.repository.OrderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the student portal. Manages cafeteria selection, menu data,
 * cart state, and order tracking. Uses StateFlow for all UI state.
 */
class StudentViewModel(
    private val menuRepository: MenuRepository = MenuRepository(),
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    private val _selectedCafeteriaId = MutableStateFlow("main-caf")
    /** Selected cafeteria: default to main-caf */
    val selectedCafeteriaId: StateFlow<String> = _selectedCafeteriaId.asStateFlow()

    /** All cafeteria documents */
    val cafeterias: StateFlow<List<Cafeteria>> = menuRepository.getAllCafeterias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    /** Menu items for selected cafeteria */
    val menuItems: StateFlow<List<MenuItem>> = _selectedCafeteriaId
        .flatMapLatest { id -> menuRepository.getMenuItemsByCafeteria(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Featured item for Today's Special banner */
    val featuredItem: StateFlow<MenuItem?> = menuItems.map { items ->
        items.find { it.isFeatured }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _cart = MutableStateFlow<Map<String, Int>>(emptyMap())
    /** Cart: map of itemId to quantity */
    val cart: StateFlow<Map<String, Int>> = _cart.asStateFlow()

    /** Cart total item count */
    val cartItemCount: StateFlow<Int> = _cart.map { it.values.sum() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _activeOrders = MutableStateFlow<List<Order>>(emptyList())
    /** Active orders for current student */
    val activeOrders: StateFlow<List<Order>> = _activeOrders.asStateFlow()

    private val _orderState = MutableStateFlow<OrderPlacementState>(OrderPlacementState.Idle)
    /** State of the current order placement operation */
    val orderState: StateFlow<OrderPlacementState> = _orderState.asStateFlow()

    /**
     * Updates the selected cafeteria.
     */
    fun selectCafeteria(cafeteriaId: String) {
        _selectedCafeteriaId.value = cafeteriaId
    }

    /**
     * Adds an item to the cart or increments its quantity.
     */
    fun addToCart(item: MenuItem) {
        val currentCart = _cart.value.toMutableMap()
        val count = currentCart.getOrDefault(item.itemId, 0)
        currentCart[item.itemId] = count + 1
        _cart.value = currentCart
    }

    /**
     * Removes an item from the cart.
     */
    fun removeFromCart(itemId: String) {
        val currentCart = _cart.value.toMutableMap()
        currentCart.remove(itemId)
        _cart.value = currentCart
    }

    /**
     * Updates the quantity of an item in the cart.
     */
    fun updateCartQuantity(itemId: String, quantity: Int) {
        val currentCart = _cart.value.toMutableMap()
        if (quantity > 0) {
            currentCart[itemId] = quantity
        } else {
            currentCart.remove(itemId)
        }
        _cart.value = currentCart
    }

    /**
     * Clears all items from the cart.
     */
    fun clearCart() {
        _cart.value = emptyMap()
    }

    /**
     * Starts listening for data relevant to a specific student.
     */
    fun loadStudentData(studentId: String) {
        viewModelScope.launch {
            orderRepository.getActiveOrders(studentId).collect { orders ->
                _activeOrders.value = orders
            }
        }
    }

    /**
     * Returns a real-time flow of a single order.
     */
    fun getOrderById(orderId: String): Flow<Order> {
        return orderRepository.getOrderById(orderId)
    }

    /**
     * Places an order for the current items in the cart.
     */
    fun placeOrder(studentId: String, specialInstructions: String) {
        val currentCart = _cart.value
        if (currentCart.isEmpty()) return

        val cafeteriaId = _selectedCafeteriaId.value
        val items = menuItems.value

        val orderItems = currentCart.mapNotNull { (itemId, quantity) ->
            val menuItem = items.find { it.itemId == itemId }
            menuItem?.let {
                OrderItem(
                    itemId = it.itemId,
                    name = it.name,
                    quantity = quantity,
                    unitPrice = it.price,
                    subtotal = it.price * quantity
                )
            }
        }

        viewModelScope.launch {
            _orderState.value = OrderPlacementState.Loading
            val result = orderRepository.placeOrder(
                studentId = studentId,
                cafeteriaId = cafeteriaId,
                items = orderItems,
                specialInstructions = specialInstructions
            )
            result.fold(
                onSuccess = { orderId ->
                    _cart.value = emptyMap()
                    _orderState.value = OrderPlacementState.Success(orderId)
                },
                onFailure = {
                    _orderState.value = OrderPlacementState.Error(it.message ?: "Failed to place order")
                }
            )
        }
    }

    /**
     * Resets the order placement state to Idle.
     */
    fun clearOrderState() {
        _orderState.value = OrderPlacementState.Idle
    }

    /**
     * Cancels an order.
     */
    fun cancelOrder(orderId: String, reason: String = "Cancelled by student") {
        viewModelScope.launch {
            _orderState.value = OrderPlacementState.Loading
            val result = orderRepository.cancelOrder(orderId, reason)
            result.fold(
                onSuccess = { _orderState.value = OrderPlacementState.Idle },
                onFailure = { _orderState.value = OrderPlacementState.Error(it.message ?: "Failed to cancel order") }
            )
        }
    }
}

/**
 * Sealed class representing the various states of placing an order.
 */
sealed class OrderPlacementState {
    object Idle : OrderPlacementState()
    object Loading : OrderPlacementState()
    data class Success(val orderId: String) : OrderPlacementState()
    data class Error(val message: String) : OrderPlacementState()
}
