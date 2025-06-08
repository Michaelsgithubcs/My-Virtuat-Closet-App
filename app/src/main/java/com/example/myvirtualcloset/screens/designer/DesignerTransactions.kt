package com.example.mvc.screens.designer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mvc.DatabaseHelper
import com.example.mvc.Designer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Data classes for transactions
data class Transaction(
    val id: Int,
    val designId: Int,
    val designTitle: String,
    val amount: Double,
    val buyerName: String,
    val date: Date,
    val status: TransactionStatus,
    val category: String
)

enum class TransactionStatus {
    COMPLETED, PENDING, CANCELLED
}

data class EarningsData(
    val totalEarnings: Double,
    val thisMonthEarnings: Double,
    val totalSales: Int,
    val popularCategory: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignerTransactions(
    databaseHelper: DatabaseHelper,
    navController: NavController,
    currentDesigner: Designer
) {
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var earningsData by remember { mutableStateOf<EarningsData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("All") }

    val scope = rememberCoroutineScope()

    // Load transactions data
    LaunchedEffect(currentDesigner.id) {
        scope.launch {
            withContext(Dispatchers.IO) {
                // Simulate loading transactions data
                // In a real app, you'd fetch this from your database
                val mockTransactions = generateMockTransactions(currentDesigner.id)
                val mockEarnings = calculateEarningsData(mockTransactions)

                withContext(Dispatchers.Main) {
                    transactions = mockTransactions
                    earningsData = mockEarnings
                    isLoading = false
                }
            }
        }
    }

    val filterOptions = listOf("All", "Completed", "Pending", "This Month")
    val filteredTransactions = filterTransactions(transactions, selectedFilter)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export functionality */ }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Earnings Overview
                item {
                    earningsData?.let { earnings ->
                        EarningsOverviewCard(earnings)
                    }
                }

                // Filter Chips
                item {
                    FilterChips(
                        options = filterOptions,
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it }
                    )
                }

                // Transactions List
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent Transactions",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${filteredTransactions.size} transactions",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (filteredTransactions.isEmpty()) {
                    item {
                        EmptyTransactionsCard()
                    }
                } else {
                    items(filteredTransactions) { transaction ->
                        TransactionItem(transaction = transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun EarningsOverviewCard(earningsData: EarningsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Earnings Overview",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EarningsMetric(
                    title = "Total Earnings",
                    value = NumberFormat.getCurrencyInstance().format(earningsData.totalEarnings),
                    icon = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f)
                )

                EarningsMetric(
                    title = "This Month",
                    value = NumberFormat.getCurrencyInstance().format(earningsData.thisMonthEarnings),
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EarningsMetric(
                    title = "Total Sales",
                    value = "${earningsData.totalSales}",
                    icon = Icons.Default.ShoppingCart,
                    modifier = Modifier.weight(1f)
                )

                EarningsMetric(
                    title = "Top Category",
                    value = earningsData.popularCategory,
                    icon = Icons.Default.Category,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun EarningsMetric(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    options: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                onClick = { onFilterSelected(option) },
                label = { Text(option) },
                selected = selectedFilter == option,
                leadingIcon = if (selectedFilter == option) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        when (transaction.status) {
                            TransactionStatus.COMPLETED -> Color.Green
                            TransactionStatus.PENDING -> Color.Yellow
                            TransactionStatus.CANCELLED -> Color.Red
                        }
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.designTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Sold to ${transaction.buyerName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = " • ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(transaction.date)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = NumberFormat.getCurrencyInstance().format(transaction.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = transaction.status.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = when (transaction.status) {
                        TransactionStatus.COMPLETED -> Color.Green
                        TransactionStatus.PENDING -> Color.Yellow
                        TransactionStatus.CANCELLED -> Color.Red
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyTransactionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No transactions found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Your sales and earnings will appear here once customers start purchasing your designs.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Helper functions for mock data
private fun generateMockTransactions(designerId: Long): List<Transaction> {
    val random = Random()
    val buyers = listOf("Sarah Johnson", "Mike Chen", "Emma Wilson", "David Brown", "Lisa Garcia")
    val designs = listOf("Summer Dress Collection", "Urban Streetwear Set", "Business Casual Look", "Evening Gown", "Casual Weekend Outfit")
    val categories = listOf("Evening Wear", "Streetwear", "Business Wear", "Casual Wear", "Dresses")

    return (1..15).map { i ->
        Transaction(
            id = i,
            designId = random.nextInt(100),
            designTitle = designs[random.nextInt(designs.size)],
            amount = 50.0 + random.nextDouble() * 200.0,
            buyerName = buyers[random.nextInt(buyers.size)],
            date = Date(System.currentTimeMillis() - random.nextLong() % (30L * 24 * 60 * 60 * 1000)),
            status = TransactionStatus.values()[random.nextInt(TransactionStatus.values().size)],
            category = categories[random.nextInt(categories.size)]
        )
    }.sortedByDescending { it.date }
}

private fun calculateEarningsData(transactions: List<Transaction>): EarningsData {
    val completedTransactions = transactions.filter { it.status == TransactionStatus.COMPLETED }
    val totalEarnings = completedTransactions.sumOf { it.amount }

    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val thisMonthEarnings = completedTransactions.filter {
        val transactionCalendar = Calendar.getInstance().apply { time = it.date }
        transactionCalendar.get(Calendar.MONTH) == currentMonth &&
                transactionCalendar.get(Calendar.YEAR) == currentYear
    }.sumOf { it.amount }

    val categoryCount = completedTransactions.groupingBy { it.category }.eachCount()
    val popularCategory = categoryCount.maxByOrNull { it.value }?.key ?: "None"

    return EarningsData(
        totalEarnings = totalEarnings,
        thisMonthEarnings = thisMonthEarnings,
        totalSales = completedTransactions.size,
        popularCategory = popularCategory
    )
}

private fun filterTransactions(transactions: List<Transaction>, filter: String): List<Transaction> {
    return when (filter) {
        "Completed" -> transactions.filter { it.status == TransactionStatus.COMPLETED }
        "Pending" -> transactions.filter { it.status == TransactionStatus.PENDING }
        "This Month" -> {
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)

            transactions.filter {
                val transactionCalendar = Calendar.getInstance().apply { time = it.date }
                transactionCalendar.get(Calendar.MONTH) == currentMonth &&
                        transactionCalendar.get(Calendar.YEAR) == currentYear
            }
        }
        else -> transactions
    }
}