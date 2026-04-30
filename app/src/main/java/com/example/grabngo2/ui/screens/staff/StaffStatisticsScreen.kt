// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.screens.staff

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.StaffViewModel
import com.example.grabngo2.ui.viewmodel.StatsPeriod

/**
 * Screen displaying operational statistics for the cafeteria.
 * Includes revenue metrics, top items, and hourly order trends.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffStatisticsScreen(
    themeChoice: String = "dark",
    viewModel: StaffViewModel
) {
    val stats by viewModel.statsData.collectAsStateWithLifecycle()
    val period by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = { 
            StaffBottomBar(
                themeChoice = themeChoice, 
                currentRoute = "staff_stats",
                onNavigate = { /* Navigation handled in NavGraph */ } 
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
            // Date Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsPeriod.values().forEach { p ->
                    FilterChip(
                        selected = period == p,
                        onClick = { viewModel.setStatsPeriod(p) },
                        label = { Text(p.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Metric Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(stats.totalOrders.toString(), "Orders", PrimaryOrange, Modifier.weight(1f))
                StatCard("R${"%.2f".format(stats.totalRevenue)}", "Revenue", Color(0xFF4CAF50), Modifier.weight(1.2f))
                StatCard("R${"%.2f".format(stats.avgValue)}", "Avg Order", Color(0xFF42A5F5), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Orders by Hour Chart
            Text("Orders by Hour", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            HourlyBarChart(stats.ordersByHour, themeChoice)

            Spacer(modifier = Modifier.height(32.dp))

            // Top Items
            Text("Top 5 Items", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            stats.topItems.forEachIndexed { index, item ->
                TopItemRow(index + 1, item, themeChoice)
                if (index < stats.topItems.size - 1) {
                    Divider(color = (if (themeChoice == "dark") Color.White else Color.Black).copy(alpha = 0.05f))
                }
            }

            if (stats.topItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No data for this period", color = TextGray)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Export Button
            OutlinedButton(
                onClick = {
                    val csvData = viewModel.getCsvExportData()
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_SUBJECT, "GrabNGo Stats Export - ${period.name}")
                        putExtra(Intent.EXTRA_TEXT, csvData)
                    }
                    context.startActivity(Intent.createChooser(intent, "Export Stats CSV"))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryOrange),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(PrimaryOrange)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Data (CSV)")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Custom bar chart for hourly order distribution.
 */
@Composable
fun HourlyBarChart(data: Map<Int, Int>, themeChoice: String) {
    val maxOrders = (data.values.maxOrNull() ?: 1).toFloat()
    val barColor = if (themeChoice == "dark") Color(0xFF3D2A1D) else LightIconBg
    val peakColor = PrimaryOrange

    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val barWidth = width / 12f // 7 to 17 is 11 bars, plus spacing
                val spacing = barWidth * 0.2f
                val actualBarWidth = barWidth - spacing

                data.entries.sortedBy { it.key }.forEachIndexed { index, entry ->
                    val barHeight = (entry.value / maxOrders) * (height - 40f)
                    val isPeak = entry.value == data.values.maxOrNull() && entry.value > 0
                    
                    drawRoundRect(
                        color = if (isPeak) peakColor else barColor,
                        topLeft = Offset(index * barWidth + spacing / 2, height - barHeight - 20f),
                        size = Size(actualBarWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                    
                    // Simple text for hours would require a NativeCanvas or DrawContext.drawText
                    // For now we'll just draw the bars.
                }
            }
        }
    }
    
    // Labels row
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("07:00", fontSize = 10.sp, color = TextGray)
        Text("12:00", fontSize = 10.sp, color = TextGray)
        Text("17:00", fontSize = 10.sp, color = TextGray)
    }
}

/**
 * Ranked row for top items.
 */
@Composable
fun TopItemRow(rank: Int, item: TopItem, themeChoice: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$rank",
            fontWeight = FontWeight.Bold,
            color = if (rank == 1) PrimaryOrange else TextGray,
            modifier = Modifier.width(32.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold)
            Text("${item.count} orders", fontSize = 12.sp, color = TextGray)
        }
        Text(
            "R${"%.2f".format(item.revenue)}",
            fontWeight = FontWeight.Bold,
            color = PrimaryOrange
        )
    }
}
