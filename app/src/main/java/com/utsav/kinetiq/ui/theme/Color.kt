package com.utsav.kinetiq.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// KINETIQ Cosmic Premium Palette
val KinetiqCosmicBlack = Color(0xFF07070D) // Deep Space Onyx
val KinetiqBlack = Color(0xFF07070D)
val KinetiqCharcoal = Color(0xFF0F0E17) // Cosmic Deep Glass Surface
val KinetiqDarkGray = Color(0xFF181528) // Deep Nebula Purple Graphite
val KinetiqWhite = Color(0xFFFFFFFF)
val KinetiqGray = Color(0xFF8E8E98) // Sleek Metallic Gray
val KinetiqLightGray = Color(0xFFD1D1D6)

// Cosmic Glowing Primary & Accent Colors
val KinetiqCyan = Color(0xFF00F0FF) // Neural Cyan Glow (Primary Accent)
val KinetiqCyanGlow = Color(0xFF00E5FF)
val KinetiqPurple = Color(0xFFA855F7) // Neon Violet / Cosmic Purple
val KinetiqViolet = Color(0xFF7000FF) // Electric Cosmic Violet
val KinetiqMagenta = Color(0xFFE024C3) // Cosmic Magenta Accent
val KinetiqLime = KinetiqCyan // Mapped for seamless transition to Cosmic Cyan highlight

val KinetiqGold = Color(0xFFFFD700)
val KinetiqEmerald = Color(0xFF00FF9D)
val KinetiqOrange = Color(0xFFFF6B00)

// Glassmorphism Surface Colors
val SurfaceDark = Color(0xFF121020)
val SurfaceGlass = Color(0x1F181528)
val BorderGlass = Color(0x3300F0FF)
val OnSurfaceDark = Color(0xFFFFFFFF)
val OnSurfaceVariantDark = Color(0xFFA0A0AB)

// Cosmic Gradient Brushes
val PremiumLimeGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF00F0FF), Color(0xFFA855F7))
)

val PremiumCosmicGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF00F0FF), Color(0xFFA855F7))
)

val GlassBorderGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x6600F0FF),
        Color(0x33A855F7),
        Color(0x0500F0FF)
    )
)

val DarkGlassBackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF14102B),
        Color(0xFF07070D)
    )
)

val PremiumAuraGradient = Brush.radialGradient(
    colors = listOf(
        Color(0x3300F0FF),
        Color(0x1AA855F7),
        Color.Transparent
    )
)
