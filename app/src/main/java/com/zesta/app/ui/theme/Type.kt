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
        color = ZestaTextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = ZestaTitleFont,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        color = ZestaTextPrimary
    ),
    bodyLarge = TextStyle(
        fontFamily = ZestaBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        color = ZestaTextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = ZestaBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = ZestaTextPrimary
    ),
    labelLarge = TextStyle(
        fontFamily = ZestaBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        color = ZestaWhite
    )
)

val LinkTextStyle = TextStyle(
    fontFamily = ZestaLinkFont,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = ZestaTextPrimary,
    textDecoration = TextDecoration.Underline
)

val DescriptionTextStyle = TextStyle(
    fontFamily = ZestaDescriptionFont,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    color = ZestaTextPrimary
)
