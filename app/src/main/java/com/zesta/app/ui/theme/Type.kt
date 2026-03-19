package com.zesta.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

val ZestaTitleFont = FontFamily.Serif
val ZestaLinkFont = FontFamily.Serif
val ZestaBodyFont = FontFamily.SansSerif
val ZestaDescriptionFont = FontFamily.SansSerif

val Typography = Typography(
    headlineMedium = TextStyle(
        fontFamily = ZestaTitleFont,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        color = TextoPrincipalZesta
    ),
    titleLarge = TextStyle(
        fontFamily = ZestaTitleFont,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        color = TextoPrincipalZesta
    ),
    bodyLarge = TextStyle(
        fontFamily = ZestaBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        color = TextoPrincipalZesta
    ),
    bodyMedium = TextStyle(
        fontFamily = ZestaBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = TextoPrincipalZesta
    ),
    labelLarge = TextStyle(
        fontFamily = ZestaBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        color = TextoPrincipalZesta
    )
)

val LinkTextStyle = TextStyle(
    fontFamily = ZestaLinkFont,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = TextoPrincipalZesta,
    textDecoration = TextDecoration.Underline
)

val DescriptionTextStyle = TextStyle(
    fontFamily = ZestaDescriptionFont,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    color = TextoPrincipalZesta
)
