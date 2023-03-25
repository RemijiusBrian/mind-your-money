package dev.ridill.mym.core.navigation.screenSpecs

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface BottomBarSpec : ScreenSpec {

    val icon: ImageVector
}