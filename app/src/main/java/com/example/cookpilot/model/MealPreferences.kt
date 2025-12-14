package com.example.cookpilot.model

import com.example.cookpilot.utils.DEFAULT_BREAKFAST_HOUR
import com.example.cookpilot.utils.DEFAULT_DINNER_HOUR
import com.example.cookpilot.utils.DEFAULT_LUNCH_HOUR
import java.time.LocalTime

data class MealPreferences(
    val breakfastTime: LocalTime = LocalTime.of(DEFAULT_BREAKFAST_HOUR, 0),
    val lunchTime: LocalTime = LocalTime.of(DEFAULT_LUNCH_HOUR, 0),
    val dinnerTime: LocalTime = LocalTime.of(DEFAULT_DINNER_HOUR, 0),
    val notificationsEnabled: Boolean = false
)
