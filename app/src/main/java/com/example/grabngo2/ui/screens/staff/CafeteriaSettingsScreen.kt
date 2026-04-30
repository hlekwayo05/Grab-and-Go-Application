// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.screens.staff

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grabngo2.data.model.User
import com.example.grabngo2.ui.screens.student.ThemeDialog
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.StaffViewModel

/**
 * Settings screen for cafeteria staff.
 * Manages cafeteria status, account view, theme selection, and logout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeteriaSettingsScreen(
    themeChoice: String = "dark",
    onThemeChange: (String) -> Unit = {},
    viewModel: StaffViewModel,
    staffUser: User,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    val cafeteria by viewModel.cafeteria.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showCloseConfirmDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeDialog(
            currentTheme = themeChoice,
            onDismiss = { showThemeDialog = false },
            onSelect = {
                onThemeChange(it)
                showThemeDialog = false
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?") },
            text = { Text("Are you sure you want to log out of the staff portal?") },
            confirmButton = {
                TextButton(onClick = onLogout, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
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

    if (showCloseConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCloseConfirmDialog = false },
            title = { Text("Close Cafeteria?") },
            text = { Text("Students will see \"Cafeteria Closed\" until you re-open it. Confirm close?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleCafeteriaOpen(false)
                        showCloseConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirm Close")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            StaffBottomBar(
                themeChoice = themeChoice,
                currentRoute = "staff_settings",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Cafeteria Status Section
            SettingsSectionHeader("Cafeteria Status")
            SettingsCard {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cafeteria?.name ?: "Loading...", fontWeight = FontWeight.Bold)
                        Text(
                            text = if (cafeteria?.isOpen == true) "Open for Orders" else "Closed to Students",
                            color = if (cafeteria?.isOpen == true) Color(0xFF4CAF50) else TextGray,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = cafeteria?.isOpen ?: false,
                        onCheckedChange = { open ->
                            if (!open) {
                                showCloseConfirmDialog = true
                            } else {
                                viewModel.toggleCafeteriaOpen(true)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Operating Hours Section
            SettingsSectionHeader("Operating Hours")
            SettingsCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Opening Time", color = TextGray)
                        Text(cafeteria?.openingTime ?: "07:30", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Closing Time", color = TextGray)
                        Text(cafeteria?.closingTime ?: "17:00", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Contact admin to change operating hours",
                        color = TextGray.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // My Account Section
            SettingsSectionHeader("My Account")
            SettingsCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    AccountDetailRow("Name", "${staffUser.firstName} ${staffUser.lastName}")
                    AccountDetailRow("Email", staffUser.email)
                    AccountDetailRow("Assigned", cafeteria?.name ?: "...")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Appearance Section
            SettingsSectionHeader("Appearance")
            SettingsCard {
                Row(
                    modifier = Modifier
                        .clickable { showThemeDialog = true }
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = PrimaryOrange)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Theme", fontWeight = FontWeight.Bold)
                        val themeLabel = when(themeChoice) {
                            "dark" -> "Dark Ember"
                            "light" -> "Campus Light"
                            else -> "System Default"
                        }
                        Text(themeLabel, color = TextGray, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Support Section
            SettingsSectionHeader("Support")
            SettingsCard {
                Row(
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:admin@testump.ac.za")
                                putExtra(Intent.EXTRA_SUBJECT, "Staff Support Request - ${cafeteria?.name}")
                            }
                            context.startActivity(intent)
                        }
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF42A5F5))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Contact Admin", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Logout Button
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = PrimaryOrange,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        content = content
    )
}

@Composable
fun AccountDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextGray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
