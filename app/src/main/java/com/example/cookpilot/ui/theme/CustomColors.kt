package com.example.cookpilot.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


object CustomColors {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun customCalendarFieldsColors() : DatePickerColors {
        return DatePickerDefaults.colors(
            todayContentColor = MaterialTheme.colorScheme.tertiary,
            todayDateBorderColor = MaterialTheme.colorScheme.tertiary,

            selectedDayContainerColor = MaterialTheme.colorScheme.primary,
            selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,

            selectedYearContainerColor = MaterialTheme.colorScheme.primary,
            selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,

            containerColor = MaterialTheme.colorScheme.background
        )
    }

    @Composable
    fun customTextFieldColors(): TextFieldColors {
        return TextFieldDefaults.colors(
            // BACKGROUND
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = Color.Gray,

            // TEXT
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,

            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,

            cursorColor = MaterialTheme.colorScheme.primary
        )
    }

    @Composable
    fun customNavigationSuiteColors(): NavigationSuiteItemColors {
        return NavigationSuiteDefaults.itemColors(
            navigationBarItemColors = NavigationBarItemDefaults.colors(

                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,

                unselectedIconColor = MaterialTheme.colorScheme.background,
                unselectedTextColor = MaterialTheme.colorScheme.background,

                indicatorColor = MaterialTheme.colorScheme.secondary
            )
        )
    }

    @Composable
    fun customNavigationSuiteContainerColors(): NavigationSuiteColors {
        return NavigationSuiteDefaults.colors(
            navigationBarContainerColor = Transparent,
            navigationRailContainerColor = Transparent,
            navigationDrawerContainerColor = Transparent
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun customTopAppBarColors(): TopAppBarColors {
        return TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Transparent,
            scrolledContainerColor = Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.background
        )
    }

    @Composable
    fun customInputChipColors(): SelectableChipColors {
        return InputChipDefaults.inputChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondary,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
            selectedTrailingIconColor = MaterialTheme.colorScheme.onSecondary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    @Composable
    fun customPrimaryButtonColor(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }

    @Composable
    fun customSecondaryButtonColor(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    }

    @Composable
    fun customCardColors(): CardColors {
        return CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    }
}