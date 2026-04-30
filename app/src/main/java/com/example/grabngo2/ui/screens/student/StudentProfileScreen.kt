// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grabngo2.data.model.User
import com.example.grabngo2.ui.components.AuthTextField
import com.example.grabngo2.ui.components.StudentBottomBar
import com.example.grabngo2.ui.screens.auth.PasswordStrengthMeter
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.AuthViewModel
import com.example.grabngo2.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Student Profile Screen: Manages user account, allergens, notifications, and password.
 */
@Composable
fun StudentProfileScreen(
    themeChoice: String = "dark",
    currentUser: User,
    authViewModel: AuthViewModel,
    studentViewModel: StudentViewModel,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    val cartItemCount by studentViewModel.cartItemCount.collectAsStateWithLifecycle()
    val orderHistory by studentViewModel.orderHistory.collectAsStateWithLifecycle()
    
    var firstName by remember { mutableStateOf(currentUser.firstName) }
    var lastName by remember { mutableStateOf(currentUser.lastName) }
    var isEditingName by remember { mutableStateOf(false) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?") },
            text = { Text("Are you sure you want to sign out? Your session will be cleared.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            StudentBottomBar(
                currentScreen = "profile",
                themeChoice = themeChoice,
                cartCount = cartItemCount,
                onHomeClick = { onNavigate("home") },
                onSearchClick = { onNavigate("search") },
                onCartClick = { onNavigate("cart") },
                onOrdersClick = { onNavigate("orders") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Profile Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = PrimaryOrange
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val initials = "${currentUser.firstName.take(1)}${currentUser.lastName.take(1)}".uppercase()
                        Text(initials, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isEditingName) {
                            Column {
                                OutlinedTextField(
                                    value = firstName,
                                    onValueChange = { firstName = it },
                                    label = { Text("First Name") },
                                    modifier = Modifier.fillMaxWidth(0.7f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = lastName,
                                    onValueChange = { lastName = it },
                                    label = { Text("Last Name") },
                                    modifier = Modifier.fillMaxWidth(0.7f),
                                    singleLine = true
                                )
                                TextButton(onClick = {
                                    authViewModel.updateProfile(mapOf("firstName" to firstName, "lastName" to lastName))
                                    isEditingName = false
                                }) {
                                    Text("Save", color = PrimaryOrange)
                                }
                            }
                        } else {
                            Text(
                                "${currentUser.firstName} ${currentUser.lastName}",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { isEditingName = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = PrimaryOrange, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    Text(
                        "Member Since ${currentUser.createdAt?.toDate()?.let { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(it) } ?: "Jan 2026"}",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Account Info (Read-only)
            SectionHeader("Account Details")
            InfoRow(Icons.Default.Style, "Student Number", currentUser.studentNumber)
            InfoRow(Icons.Default.Email, "University Email", currentUser.email)

            Spacer(modifier = Modifier.height(32.dp))

            // Password Change
            SectionHeader("Security")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Change Password", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AuthTextField(
                        label = "CURRENT PASSWORD",
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        themeChoice = themeChoice
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AuthTextField(
                        label = "NEW PASSWORD",
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        themeChoice = themeChoice
                    )
                    if (newPassword.isNotEmpty()) {
                        PasswordStrengthMeter(newPassword)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    AuthTextField(
                        label = "CONFIRM NEW PASSWORD",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        themeChoice = themeChoice
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = {
                            authViewModel.updatePassword(currentPassword, newPassword) { result ->
                                result.onSuccess { 
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                }
                                // Snackbar handled by AuthViewModel or UI side
                            }
                        },
                        enabled = currentPassword.isNotBlank() && newPassword.length >= 6 && newPassword == confirmPassword,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Update Password")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Allergen Profile
            SectionHeader("Dietary Preferences")
            Text("Select allergens to receive warnings in the menu.", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
            
            val allergenOptions = listOf("Nuts", "Dairy", "Gluten", "Eggs", "Soy", "Seafood", "Other")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allergenOptions.forEach { allergen ->
                    FilterChip(
                        selected = currentUser.allergens.contains(allergen),
                        onClick = {
                            val newList = if (currentUser.allergens.contains(allergen)) {
                                currentUser.allergens - allergen
                            } else {
                                currentUser.allergens + allergen
                            }
                            authViewModel.updateProfile(mapOf("allergens" to newList))
                        },
                        label = { Text(allergen) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryOrange,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Notification Preferences
            SectionHeader("Notifications")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val prefs = listOf(
                        "order_confirmed" to "Order Confirmed",
                        "order_preparing" to "Order Preparing",
                        "order_ready" to "Order Ready",
                        "order_cancelled" to "Order Cancelled"
                    )
                    
                    prefs.forEach { (key, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, color = MaterialTheme.colorScheme.onBackground)
                            Switch(
                                checked = currentUser.notifPrefs[key] ?: true,
                                onCheckedChange = { isEnabled ->
                                    val newMap = currentUser.notifPrefs.toMutableMap().apply {
                                        put(key, isEnabled)
                                    }
                                    authViewModel.updateProfile(mapOf("notifPrefs" to newMap))
                                },
                                colors = SwitchDefaults.colors(checkedTrackColor = PrimaryOrange)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Account Stats
            SectionHeader("Statistics")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatTile("Orders", orderHistory.size.toString(), Modifier.weight(1f))
                StatTile("Verifications", "2026", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Logout
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out of Account")
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        title,
        color = PrimaryOrange,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, color = TextGray, fontSize = 12.sp)
            Text(value, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = TextGray.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
    }
}

@Composable
fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = PrimaryOrange, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextGray, fontSize = 12.sp)
        }
    }
}
