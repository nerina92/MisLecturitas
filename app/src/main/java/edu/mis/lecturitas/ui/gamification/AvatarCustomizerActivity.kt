package edu.mis.lecturitas.ui.gamification

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.mis.lecturitas.model.AvatarCategory
import edu.mis.lecturitas.model.AvatarItem
import edu.mis.lecturitas.ui.theme.MisLecturitasTheme

/**
 * Pantalla de personalización de avatar
 */
class AvatarCustomizerActivity : ComponentActivity() {

    private val viewModel = AvatarCustomizerViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MisLecturitasTheme {
                AvatarCustomizerScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        if (viewModel.hasUnsavedChanges()) {
                            // TODO: Mostrar diálogo de confirmación
                        }
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarCustomizerScreen(
    viewModel: AvatarCustomizerViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentAvatar by viewModel.currentAvatar.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val availableItems by viewModel.availableItems.collectAsState()
    val userPoints by viewModel.userPoints.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personalizar Avatar") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Points display
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⭐", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$userPoints",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.cancelChanges() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { viewModel.saveAvatar() },
                        modifier = Modifier.weight(1f),
                        enabled = uiState !is AvatarCustomizerUiState.Saving
                    ) {
                        if (uiState is AvatarCustomizerUiState.Saving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is AvatarCustomizerUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is AvatarCustomizerUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (uiState as AvatarCustomizerUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadAvatarData() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            else -> {
                AvatarCustomizerContent(
                    currentAvatar = currentAvatar,
                    selectedCategory = selectedCategory,
                    availableItems = availableItems,
                    userPoints = userPoints,
                    onCategoryChange = { category -> viewModel.selectCategory(category) },
                    onItemSelect = { item -> viewModel.selectItem(item) },
                    onItemPurchase = { item -> viewModel.purchaseItem(item) },
                    modifier = Modifier.padding(paddingValues)
                )

                // Snackbar messages
                when (uiState) {
                    is AvatarCustomizerUiState.SaveSuccess -> {
                        LaunchedEffect(Unit) {
                            // TODO: Mostrar snackbar de éxito
                        }
                    }
                    is AvatarCustomizerUiState.PurchaseSuccess -> {
                        LaunchedEffect(Unit) {
                            // TODO: Mostrar snackbar de compra exitosa
                        }
                    }
                    is AvatarCustomizerUiState.PurchaseError -> {
                        LaunchedEffect(Unit) {
                            // TODO: Mostrar snackbar de error
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun AvatarCustomizerContent(
    currentAvatar: edu.mis.lecturitas.model.AvatarCustomization,
    selectedCategory: AvatarCategory,
    availableItems: List<AvatarItem>,
    userPoints: Int,
    onCategoryChange: (AvatarCategory) -> Unit,
    onItemSelect: (AvatarItem) -> Unit,
    onItemPurchase: (AvatarItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Avatar Preview
        AvatarPreviewCard(currentAvatar = currentAvatar)

        Spacer(modifier = Modifier.height(16.dp))

        // Category Tabs
        CategoryTabs(
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Items Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(availableItems) { item ->
                AvatarItemCard(
                    item = item,
                    isSelected = isItemSelected(currentAvatar, item),
                    canAfford = userPoints >= item.pointsCost,
                    onClick = {
                        if (item.unlocked || item.pointsCost == 0) {
                            onItemSelect(item)
                        } else if (userPoints >= item.pointsCost) {
                            onItemPurchase(item)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AvatarPreviewCard(currentAvatar: edu.mis.lecturitas.model.AvatarCustomization) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vista Previa",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar Display (simplified with emojis)
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Character
                    Text(
                        text = getAvatarEmoji(currentAvatar.baseCharacter),
                        fontSize = 80.sp
                    )
                    // Hat (if any)
                    currentAvatar.hat?.let { hat ->
                        if (hat != "hat_none") {
                            Text(
                                text = getHatEmoji(hat),
                                fontSize = 40.sp,
                                modifier = Modifier.offset(y = (-60).dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabs(
    selectedCategory: AvatarCategory,
    onCategoryChange: (AvatarCategory) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedCategory.ordinal,
        edgePadding = 0.dp
    ) {
        AvatarCategory.values().forEach { category ->
            Tab(
                selected = selectedCategory == category,
                onClick = { onCategoryChange(category) },
                text = {
                    Text(
                        text = when (category) {
                            AvatarCategory.CHARACTER -> "Personaje"
                            AvatarCategory.HAT -> "Sombreros"
                            AvatarCategory.ACCESSORY -> "Accesorios"
                            AvatarCategory.BACKGROUND -> "Fondos"
                            AvatarCategory.FRAME -> "Marcos"
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun AvatarItemCard(
    item: AvatarItem,
    isSelected: Boolean,
    canAfford: Boolean,
    onClick: () -> Unit
) {
    val isLocked = !item.unlocked && item.pointsCost > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(enabled = canAfford || item.unlocked) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                isLocked && !canAfford -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isSelected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                // Item Icon/Emoji
                Text(
                    text = item.emoji,
                    fontSize = 40.sp,
                    modifier = Modifier.alpha(if (isLocked && !canAfford) 0.3f else 1f)
                )

                if (isLocked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (canAfford)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Gray
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⭐", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${item.pointsCost}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Seleccionado",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Lock icon overlay
            if (isLocked && !canAfford) {
                Icon(
                    imageVector = Icons.Default.Check, // TODO: Use Lock icon
                    contentDescription = "Bloqueado",
                    tint = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(16.dp)
                )
            }
        }
    }
}

// Helper functions
fun isItemSelected(avatar: edu.mis.lecturitas.model.AvatarCustomization, item: AvatarItem): Boolean {
    return when (item.category) {
        AvatarCategory.CHARACTER -> avatar.baseCharacter == item.id
        AvatarCategory.HAT -> avatar.hat == item.id
        AvatarCategory.ACCESSORY -> avatar.accessory == item.id
        AvatarCategory.BACKGROUND -> avatar.background == item.id
        AvatarCategory.FRAME -> avatar.frame == item.id
    }
}

fun getHatEmoji(hatId: String): String {
    return when (hatId) {
        "hat_cap" -> "🧢"
        "hat_wizard" -> "🎩"
        "hat_crown" -> "👑"
        "hat_astronaut" -> "🚀"
        "hat_pirate" -> "🏴‍☠️"
        "hat_chef" -> "👨‍🍳"
        "hat_cowboy" -> "🤠"
        "hat_detective" -> "🕵️"
        "hat_graduation" -> "🎓"
        else -> ""
    }
}
