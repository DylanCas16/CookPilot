package com.example.cookpilot.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(

    // --- TÍTULOS GRANDES (H1, H2 - para nombres de recetas o secciones) ---
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif, // Usamos Serif para un toque clásico y establecido
        fontWeight = FontWeight.SemiBold, // Seminegrita para autoridad
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),

    // --- TÍTULOS DE SECCIÓN (H3, H4 - ingredientes, pasos) ---
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold, // Negrita para que resalte
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),

    // --- CUERPO PRINCIPAL (bodyLarge - descripciones, contenido) ---
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif, // Usamos SansSerif para la legibilidad en bloques de texto
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp, // Ligeramente más grande para lectura cómoda
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp
    ),

    // --- PIES DE PÁGINA O ETIQUETAS (labelSmall) ---
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)