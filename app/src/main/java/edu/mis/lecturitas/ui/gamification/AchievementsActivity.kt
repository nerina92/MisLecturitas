package edu.mis.lecturitas.ui.gamification

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import edu.mis.lecturitas.model.Achievement
import edu.mis.lecturitas.ui.theme.MisLecturitasTheme

/**
 * Pantalla de todos los logros/medallas
 */
class AchievementsActivity : ComponentActivity() {

    private val viewModel = AchievementsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MisLecturitasTheme {
                AchievementsScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Logros") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is AchievementsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is AchievementsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState as AchievementsUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is AchievementsUiState.Success -> {
                val successState = uiState as AchievementsUiState.Success
                AchievementsContent(
                    achievements = achievements,
                    unlockedCount = successState.unlockedCount,
                    totalCount = successState.totalCount,
                    completionPercentage = successState.completionPercentage,
                    selectedFilter = selectedFilter,
                    onFilterChange = { filter -> viewModel.setFilter(filter) },
                    onAchievementClick = { achievement -> viewModel.onAchievementClick(achievement) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun AchievementsContent(
    achievements: List<AchievementWithProgress>,
    unlockedCount: Int,
    totalCount: Int,
    completionPercentage: Int,
    selectedFilter: AchievementFilter,
    onFilterChange: (AchievementFilter) -> Unit,
    onAchievementClick: (Achievement) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Card
        AchievementsHeaderCard(
            unlockedCount = unlockedCount,
            totalCount = totalCount,
            completionPercentage = completionPercentage
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filters
        AchievementFilters(
            selectedFilter = selectedFilter,
            onFilterChange = onFilterChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Achievements Grid
        if (achievements.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay logros en esta categoría",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achievements) { achievementWithProgress ->
                    AchievementCard(
                        achievement = achievementWithProgress.achievement,
                        progress = achievementWithProgress.progress,
                        progressText = achievementWithProgress.progressText,
                        onClick = { onAchievementClick(achievementWithProgress.achievement) }
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementsHeaderCard(
    unlockedCount: Int,
    totalCount: Int,
    completionPercentage: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$unlockedCount / $totalCount",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Logros desbloqueados",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = completionPercentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$completionPercentage% completado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AchievementFilters(
    selectedFilter: AchievementFilter,
    onFilterChange: (AchievementFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == AchievementFilter.ALL,
            onClick = { onFilterChange(AchievementFilter.ALL) },
            label = { Text("Todos") }
        )
        FilterChip(
            selected = selectedFilter == AchievementFilter.UNLOCKED,
            onClick = { onFilterChange(AchievementFilter.UNLOCKED) },
            label = { Text("Desbloqueados") }
        )
        FilterChip(
            selected = selectedFilter == AchievementFilter.LOCKED,
            onClick = { onFilterChange(AchievementFilter.LOCKED) },
            label = { Text("Bloqueados") }
        )
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    progress: Float,
    progressText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.unlocked)
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFD700),
                                    Color(0xFFFFA500)
                                )
                            )
                        else
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Gray.copy(alpha = 0.3f),
                                    Color.Gray.copy(alpha = 0.2f)
                                )
                            )
                    )
                    .border(
                        width = 3.dp,
                        color = if (achievement.unlocked)
                            Color(0xFFFFD700)
                        else
                            Color.Gray.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.icon,
                    fontSize = 40.sp,
                    modifier = Modifier.alpha(if (achievement.unlocked) 1f else 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Name
            Text(
                text = achievement.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (achievement.unlocked)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = if (achievement.unlocked)
                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

            if (!achievement.unlocked) {
                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Progress Text
                Text(
                    text = progressText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Desbloqueado",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
