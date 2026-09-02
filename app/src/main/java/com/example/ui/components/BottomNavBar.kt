package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DeveloperBoard
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.FindInPage
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface1
import com.example.ui.theme.DarkSurface2
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

data class NavItem(
    val tab: AppTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun BottomNavBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(AppTab.CHAT, "Chat", Icons.Filled.Chat, Icons.Outlined.Chat, "nav_tab_chat"),
        NavItem(AppTab.HARDWARE_RUNNER, "Hardware", Icons.Filled.DeveloperBoard, Icons.Outlined.DeveloperBoard, "nav_tab_hardware"),
        NavItem(AppTab.STUDIO_MULTIMODAL, "Studio", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_tab_studio"),
        NavItem(AppTab.MODELS_HUB, "Models", Icons.Filled.Hub, Icons.Outlined.Hub, "nav_tab_models"),
        NavItem(AppTab.AAS_SERVER, "AAS", Icons.Filled.Dns, Icons.Outlined.Dns, "nav_tab_server"),
        NavItem(AppTab.VECTOR_RAG, "RAG", Icons.Filled.FindInPage, Icons.Outlined.FindInPage, "nav_tab_rag"),
        NavItem(AppTab.CODE_IDE, "IDE", Icons.Filled.Code, Icons.Outlined.Code, "nav_tab_code"),
        NavItem(AppTab.SETTINGS_PRIVACY, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_tab_settings")
    )

    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .testTag("bottom_nav_bar"),
        containerColor = DarkSurface1,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanNeon,
                    selectedTextColor = CyanNeon,
                    indicatorColor = DarkSurface2,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
