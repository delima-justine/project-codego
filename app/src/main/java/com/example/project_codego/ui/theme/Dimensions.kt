package com.example.project_codego.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Dimensions(
    val smallPadding: Dp,
    val mediumPadding: Dp,
    val largePadding: Dp,
    val extraLargePadding: Dp,
    
    val smallIconSize: Dp,
    val mediumIconSize: Dp,
    val largeIconSize: Dp,
    val extraLargeIconSize: Dp,
    
    val buttonHeight: Dp,
    val bottomNavHeight: Dp,
    val topBarHeight: Dp,
    
    val smallTextSize: TextUnit,
    val normalTextSize: TextUnit,
    val mediumTextSize: TextUnit,
    val largeTextSize: TextUnit,
    val extraLargeTextSize: TextUnit,
    
    val cardRadius: Dp,
    val buttonRadius: Dp,
    
    val profileAvatarSize: Dp,
    val postAvatarSize: Dp,
    
    val screenWidthDp: Dp,
    val isTablet: Boolean
)

@Composable
fun rememberDimensions(): Dimensions {
    val configuration = LocalConfiguration.current
    
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    // Determine if device is a tablet (width >= 600dp)
    val isTablet = screenWidth >= 600.dp
    
    // Scale factor based on screen width
    val scale = when {
        screenWidth < 360.dp -> 0.9f  // Small phones
        screenWidth < 400.dp -> 1.0f  // Normal phones
        screenWidth < 600.dp -> 1.1f  // Large phones
        screenWidth < 840.dp -> 1.3f  // Small tablets
        else -> 1.5f                   // Large tablets
    }
    
    return Dimensions(
        // Padding
        smallPadding = (4.dp * scale),
        mediumPadding = (8.dp * scale),
        largePadding = (16.dp * scale),
        extraLargePadding = (32.dp * scale),
        
        // Icon sizes
        smallIconSize = (16.dp * scale),
        mediumIconSize = (24.dp * scale),
        largeIconSize = (48.dp * scale),
        extraLargeIconSize = (64.dp * scale),
        
        // Component heights
        buttonHeight = (50.dp * scale),
        bottomNavHeight = if (isTablet) (90.dp * scale) else (80.dp * scale),
        topBarHeight = (56.dp * scale),
        
        // Text sizes
        smallTextSize = (12.sp * scale),
        normalTextSize = (14.sp * scale),
        mediumTextSize = (16.sp * scale),
        largeTextSize = (20.sp * scale),
        extraLargeTextSize = (24.sp * scale),
        
        // Radii
        cardRadius = (12.dp * scale),
        buttonRadius = (8.dp * scale),
        
        // Avatars
        profileAvatarSize = (100.dp * scale),
        postAvatarSize = (48.dp * scale),
        
        screenWidthDp = screenWidth,
        isTablet = isTablet
    )
}

// Extension for responsive width
@Composable
fun responsiveWidth(fraction: Float): Dp {
    val configuration = LocalConfiguration.current
    return (configuration.screenWidthDp * fraction).dp
}

// Extension for responsive height
@Composable
fun responsiveHeight(fraction: Float): Dp {
    val configuration = LocalConfiguration.current
    return (configuration.screenHeightDp * fraction).dp
}
