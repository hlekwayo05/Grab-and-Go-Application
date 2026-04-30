// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grabngo2.data.model.Cafeteria
import com.example.grabngo2.data.model.MenuItem
import com.example.grabngo2.data.model.Order
import com.example.grabngo2.data.model.User
import com.example.grabngo2.data.repository.MenuRepository
import com.example.grabngo2.data.repository.OrderRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for the staff portal. Manages live order stream for the assigned cafeteria,
 * order status updates, and menu item management. Scoped to the staff nav graph.
 */
class StaffViewModel(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val menuRepository: MenuRepository = MenuRepository()
) : ViewModel() {

    private val _staffUser = MutableStateFlow<User?>(null)
    /** Staff user details. */
    val staffUser: StateFlow<User?> = _staffUser.asStateFlow()

    private val _liveOrders = MutableStateFlow<List<Order>>(emptyList())
    /** Live orders for the staff member's cafeteria, sorted oldest-first. */
    val liveOrders: StateFlow<List<Order>> = _liveOrders.asStateFlow()

    /** Summary counts derived from live orders. */
    val pendingCount = _liveOrders.map { orders -> orders.count { it.status == "pending" || it.status == "confirmed" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    val preparingCount = _liveOrders.map { orders -> orders.count { it.status == "preparing" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    val collectedCount = _liveOrders.map { orders -> orders.count { it.status == "collected" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    /** Menu items for the cafeteria. */
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _cafeteria = MutableStateFlow<Cafeteria?>(null)
    /** Cafeteria document. */
    val cafeteria: StateFlow<Cafeteria?> = _cafeteria.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    /** Indicates if Firestore is currently offline. */
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    init {
        monitorConnectivity()
    }

    /**
     * Initializes data fetching for the given staff user.
     */
    fun loadStaffData(user: User) {
        _staffUser.value = user
        val cafeteriaId = user.cafeteriaId
        if (cafeteriaId.isEmpty()) return

        viewModelScope.launch {
            launch {
                orderRepository.getLiveOrdersForCafeteria(cafeteriaId).collect {
                    _liveOrders.value = it
                }
            }
            launch {
                menuRepository.getAllMenuItemsByCafeteria(cafeteriaId).collect {
                    _menuItems.value = it
                }
            }
            launch {
                menuRepository.getCafeteria(cafeteriaId).collect {
                    _cafeteria.value = it
                }
            }
        }
    }

    /**
     * Marks an order as "preparing".
     */
    fun markPreparing(orderId: String) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, "preparing")
        }
    }

    /**
     * Marks an order as "ready" and generates a collection token.
     */
    fun markReady(orderId: String) {
        viewModelScope.launch {
            val token = generateCollectionToken()
            orderRepository.updateOrderStatus(orderId, "ready", token)
        }
    }

    /**
     * Marks an order as "collected".
     */
    fun markCollected(orderId: String) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, "collected")
        }
    }

    /**
     * Cancels an order with a reason.
     */
    fun cancelOrder(orderId: String, reason: String) {
        viewModelScope.launch {
            orderRepository.cancelOrder(orderId, reason)
        }
    }

    /**
     * Extends the ETA of an order by 5 minutes.
     */
    fun extendETA(orderId: String) {
        viewModelScope.launch {
            orderRepository.extendOrderETA(orderId, 5)
        }
    }

    /**
     * Toggles availability of a menu item.
     */
    fun toggleItemAvailability(itemId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            menuRepository.toggleItemAvailability(itemId, isAvailable)
        }
    }

    /**
     * Toggles the open/closed status of the cafeteria.
     */
    fun toggleCafeteriaOpen(isOpen: Boolean) {
        val cafeteriaId = _staffUser.value?.cafeteriaId ?: return
        viewModelScope.launch {
            menuRepository.updateCafeteriaStatus(cafeteriaId, isOpen)
        }
    }

    /**
     * Adds a new menu item.
     */
    fun addMenuItem(item: MenuItem) {
        viewModelScope.launch {
            menuRepository.addMenuItem(item)
        }
    }

    /**
     * Updates an existing menu item.
     */
    fun updateMenuItem(item: MenuItem) {
        viewModelScope.launch {
            menuRepository.updateMenuItem(item)
        }
    }

    /**
     * Deletes a menu item.
     */
    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch {
            menuRepository.deleteMenuItem(itemId)
        }
    }

    private fun generateCollectionToken(): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun monitorConnectivity() {
        FirebaseFirestore.getInstance().addSnapshotsInSyncListener {
            // This is a simplified way to detect connectivity changes via Firestore
            // In a real app, one might use a more robust ConnectivityManager approach
            // but for Firestore status, we can check if it's using cache only.
            // However, the task mentions detecting Firestore connectivity.
        }
        // More direct approach for Firestore connectivity:
        FirebaseFirestore.getInstance().collection(".info/connected").addSnapshotListener { snapshot, _ ->
            val connected = snapshot?.getBoolean("connected") ?: false
            _isOffline.value = !connected
        }
    }
}
