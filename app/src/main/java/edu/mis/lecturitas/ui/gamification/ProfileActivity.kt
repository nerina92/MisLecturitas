package edu.mis.lecturitas.ui.gamification

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
import edu.mis.lecturitas.ui.theme.MisLecturitasTheme

/**
 * Pantalla de perfil del usuario con gamificación
 */
class ProfileActivity : ComponentActivity() {

    private val viewModel = ProfileViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MisLecturitasTheme {
                ProfileScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() },
                    onAvatarCustomizeClick = {
                        startActivity(Intent(this, AvatarCustomizerActivity::class.java))
                    },
                    onViewAllAchievementsClick = {
                        startActivity(Intent(this, AchievementsActivity::class.java))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onAvatarCustomizeClick: () -> Unit,
    onViewAllAchievementsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
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
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState as ProfileUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is ProfileUiState.Success -> {
                val successState = uiState as ProfileUiState.Success
                ProfileContent(
                    userName = successState.userName,
                    userProgress = successState.userProgress,
                    streakMessage = successState.streakMessage,
                    streakEmoji = successState.streakEmoji,
                    achievements = achievements,
                    onAvatarCustomizeClick = onAvatarCustomizeClick,
                    onViewAllAchievementsClick = onViewAllAchievementsClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun ProfileContent(
    userName: String,
    userProgress: edu.mis.lecturitas.model.UserProgress,
    streakMessage: String,
    streakEmoji: String,
    achievements: List<AchievementWithProgress>,
    onAvatarCustomizeClick: () -> Unit,
    onViewAllAchievementsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Card
        AvatarCard(
            userName = userName,
            avatarEmoji = getAvatarEmoji(userProgress.avatar.baseCharacter),
            onCustomizeClick = onAvatarCustomizeClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Points and Level Card
        PointsAndLevelCard(
            points = userProgress.totalPoints,
            level = userProgress.level,
            levelProgress = userProgress.levelProgress(),
            pointsToNextLevel = userProgress.pointsToNextLevel()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Card
        StatsCard(
            booksRead = userProgress.booksRead,
            audiobooksListened = userProgress.audiobooksListened,
            gamesCompleted = userProgress.gamesCompleted
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Streak Card
        StreakCard(
            currentStreak = userProgress.currentStreak,
            longestStreak = userProgress.longestStreak,
            streakMessage = streakMessage,
            streakEmoji = streakEmoji
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Achievements Preview
        AchievementsPreview(
            achievements = achievements.take(4),
            unlockedCount = userProgress.unlockedAchievementsCount(),
            totalCount = achievements.size,
            onViewAllClick = onViewAllAchievementsClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Build Version Info
        Text(
            text = "Versión: ${edu.mis.lecturitas.BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.alpha(0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun AvatarCard(
    userName: String,
    avatarEmoji: String,
    onCustomizeClick: () -> Unit
) {
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
            // Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarEmoji,
                    fontSize = 60.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onCustomizeClick,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Personalizar Avatar")
            }
        }
    }
}

@Composable
fun PointsAndLevelCard(
    points: Int,
    level: Int,
    levelProgress: Float,
    pointsToNextLevel: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Points
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "⭐",
                        fontSize = 32.sp
                    )
                    Text(
                        text = "$points",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Puntos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Divider
                Divider(
                    modifier = Modifier
                        .height(60.dp)
                        .width(1.dp)
                )

                // Level
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🏅",
                        fontSize = 32.sp
                    )
                    Text(
                        text = "$level",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nivel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Level Progress Bar
            Column {
                LinearProgressIndicator(
                    progress = levelProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$pointsToNextLevel puntos para nivel ${level + 1}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StatsCard(
    booksRead: Int,
    audiobooksListened: Int,
    gamesCompleted: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            StatRow(icon = "📚", label = "Libros leídos", value = booksRead)
            Spacer(modifier = Modifier.height(8.dp))
            StatRow(icon = "🎧", label = "Audiolibros", value = audiobooksListened)
            Spacer(modifier = Modifier.height(8.dp))
            StatRow(icon = "🎮", label = "Juegos completados", value = gamesCompleted)
        }
    }
}

@Composable
fun StatRow(icon: String, label: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
    streakMessage: String,
    streakEmoji: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = streakEmoji,
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = streakMessage,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Récord: $longestStreak días",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AchievementsPreview(
    achievements: List<AchievementWithProgress>,
    unlockedCount: Int,
    totalCount: Int,
    onViewAllClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 Medallas ($unlockedCount/$totalCount)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAllClick) {
                    Text("Ver todas")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achievements) { achievementWithProgress ->
                    AchievementBadge(
                        icon = achievementWithProgress.achievement.icon,
                        name = achievementWithProgress.achievement.name,
                        unlocked = achievementWithProgress.achievement.unlocked
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementBadge(
    icon: String,
    name: String,
    unlocked: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    if (unlocked)
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
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 32.sp,
                modifier = Modifier.let {
                    if (!unlocked) it.then(Modifier.alpha(0.3f)) else it
                }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            color = if (unlocked)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

// Helper function to get avatar emoji
fun getAvatarEmoji(characterId: String): String {
    return when (characterId) {
        "character_1" -> "👦"
        "character_2" -> "👧"
        "character_3" -> "🤖"
        "character_4" -> "🐱"
        "character_5" -> "🐶"
        "character_6" -> "🐰"
        else -> "👤"
    }
}
