// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.screens.staff

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grabngo2.data.model.MenuItem
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.StaffViewModel

/**
 * Screen for cafeteria staff to manage menu items.
 * Allows adding, editing, deleting, and toggling availability/featured status.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MenuManagementScreen(
    themeChoice: String = "dark",
    viewModel: StaffViewModel
) {
    val menuItems by viewModel.menuItems.collectAsStateWithLifecycle()
    var showAddEditSheet by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<MenuItem?>(null) }
    var itemToDelete by remember { mutableStateOf<MenuItem?>(null) }

    val categories = listOf("Meals", "Snacks", "Drinks", "Combos", "Desserts")
    val groupedItems = menuItems.groupBy { it.category.ifEmpty { "Other" } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu Management", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(
                        onClick = {
                            itemToEdit = null
                            showAddEditSheet = true
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = PrimaryOrange.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item", tint = PrimaryOrange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    itemToEdit = null
                    showAddEditSheet = true
                },
                containerColor = PrimaryOrange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        bottomBar = { StaffBottomBar(themeChoice = themeChoice, currentRoute = "staff_menu") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            categories.forEach { category ->
                val itemsInCategory = groupedItems[category.lowercase()] ?: emptyList()
                if (itemsInCategory.isNotEmpty()) {
                    stickyHeader {
                        CategoryHeader(category)
                    }
                    items(itemsInCategory, key = { it.itemId }) { item ->
                        MenuItemRow(
                            item = item,
                            themeChoice = themeChoice,
                            onToggleAvailability = { available ->
                                viewModel.toggleItemAvailability(item.itemId, available)
                            },
                            onClick = {
                                itemToEdit = item
                                showAddEditSheet = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddEditSheet) {
        AddEditItemSheet(
            item = itemToEdit,
            onDismiss = { showAddEditSheet = false },
            onSave = { newItem ->
                if (itemToEdit == null) {
                    viewModel.addMenuItem(newItem.copy(cafeteriaId = viewModel.staffUser.value?.cafeteriaId ?: ""))
                } else {
                    viewModel.updateMenuItem(newItem)
                }
                showAddEditSheet = false
            },
            onDelete = {
                itemToDelete = itemToEdit
                showAddEditSheet = false
            }
        )
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Item?") },
            text = { Text("Are you sure? This will remove \"${itemToDelete?.name}\" for all students.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDelete?.let { viewModel.deleteMenuItem(it.itemId) }
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CategoryHeader(title: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Text(
            text = title,
            color = PrimaryOrange,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

@Composable
fun MenuItemRow(
    item: MenuItem,
    themeChoice: String,
    onToggleAvailability: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji Placeholder
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (themeChoice == "dark") Color(0xFF3D2A1D) else LightIconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = when(item.category.lowercase()) {
                            "meals" -> "🍲"
                            "snacks" -> "🍟"
                            "drinks" -> "🥤"
                            "combos" -> "🍱"
                            "desserts" -> "🍰"
                            else -> "🍴"
                        },
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (item.isFeatured) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "SPECIAL",
                                color = Color(0xFFFFD700),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = item.category.uppercase(),
                    color = PrimaryOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("R${item.price}", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "${item.stockCount} remaining",
                        color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Switch(
                checked = item.isAvailable,
                onCheckedChange = onToggleAvailability,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50),
                    uncheckedThumbColor = TextGray,
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemSheet(
    item: MenuItem?,
    onDismiss: () -> Unit,
    onSave: (MenuItem) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var category by remember { mutableStateOf(item?.category ?: "meals") }
    var price by remember { mutableStateOf(item?.price?.toString() ?: "") }
    var stockCount by remember { mutableStateOf(item?.stockCount?.toString() ?: "") }
    var defaultStockCount by remember { mutableStateOf(item?.defaultStockCount?.toString() ?: "") }
    var selectedAllergens by remember { mutableStateOf(item?.allergens ?: emptyList()) }
    var isFeatured by remember { mutableStateOf(item?.isFeatured ?: false) }
    var isAvailable by remember { mutableStateOf(item?.isAvailable ?: true) }

    val categories = listOf("meals", "snacks", "drinks", "combos", "desserts")
    val allergenOptions = listOf("Nuts", "Dairy", "Gluten", "Eggs", "Soy", "Seafood")
    var categoryExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (item == null) "Add New Item" else "Edit Menu Item",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category.replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (R)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = stockCount,
                    onValueChange = { stockCount = it },
                    label = { Text("Today's Stock") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = defaultStockCount,
                onValueChange = { defaultStockCount = it },
                label = { Text("Default Daily Stock") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text("Allergens", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allergenOptions.forEach { allergen ->
                    FilterChip(
                        selected = selectedAllergens.contains(allergen),
                        onClick = {
                            selectedAllergens = if (selectedAllergens.contains(allergen)) {
                                selectedAllergens - allergen
                            } else {
                                selectedAllergens + allergen
                            }
                        },
                        label = { Text(allergen) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Today's Special", fontWeight = FontWeight.Bold)
                    Text("Featured at the top of student menu", fontSize = 12.sp, color = TextGray)
                }
                Switch(checked = isFeatured, onCheckedChange = { isFeatured = it })
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Mark as Available", fontWeight = FontWeight.Bold)
                    Text("Uncheck to hide from students", fontSize = 12.sp, color = TextGray)
                }
                Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            MenuItem(
                                itemId = item?.itemId ?: "",
                                cafeteriaId = item?.cafeteriaId ?: "",
                                name = name,
                                description = description,
                                category = category,
                                price = price.toDoubleOrNull() ?: 0.0,
                                stockCount = stockCount.toIntOrNull() ?: 0,
                                defaultStockCount = defaultStockCount.toIntOrNull() ?: 0,
                                isAvailable = isAvailable,
                                isFeatured = isFeatured,
                                allergens = selectedAllergens
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Changes", modifier = Modifier.padding(4.dp))
            }

            if (item != null) {
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Item")
                }
            }
        }
    }
}
