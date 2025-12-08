package com.example.cookpilot.model

import java.time.LocalTime

data class MealPreferences(
    val breakfastTime: LocalTime = LocalTime.of(8, 0),  // 08:00
    val lunchTime: LocalTime = LocalTime.of(14, 0),      // 14:00
    val dinnerTime: LocalTime = LocalTime.of(21, 0),     // 21:00
    val notificationsEnabled: Boolean = false
)
