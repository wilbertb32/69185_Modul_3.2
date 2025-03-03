/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.rally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.compose.rally.ui.components.RallyTabRow
import com.example.compose.rally.ui.theme.RallyTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.compose.rally.ui.overview.OverviewScreen
import com.example.compose.rally.ui.accounts.AccountsScreen
import com.example.compose.rally.ui.bills.BillsScreen
import com.example.compose.rally.ui.accounts.SingleAccountScreen
import com.example.compose.rally.data.UserData
import androidx.navigation.navDeepLink

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RallyApp()
        }
    }
    interface RallyDestination {
        val icon: ImageVector
        val route: String
    }

    /**
     * Rally app navigation destinations
     */
    object Overview : RallyDestination {
        override val icon = Icons.Filled.PieChart
        override val route = "overview"
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

@Composable
fun RallyApp() {
    RallyTheme {
        val navController = rememberNavController()

        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        // Ubah variable ini agar default-nya adalah Overview
        val currentScreen = rallyTabRowScreens.find { it.route == currentDestination?.route } ?: RallyActivity.Overview

        Scaffold(
            topBar = {
                RallyTabRow(
                    allScreens = rallyTabRowScreens,
                    onTabSelected = { newScreen ->
                        navController
                            .navigateSingleTopTo(newScreen.route)
                    },
                    currentScreen = currentScreen,
                )
            }
        ) { innerPadding ->
            RallyNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun RallyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = RallyActivity.Overview.route, // Pastikan Overview menjadi startDestination
        modifier = Modifier
    ) {
        // Tambahkan rute Overview di sini
        composable(route = RallyActivity.Overview.route) {
            OverviewScreen(
                onAccountClick = { accountType ->
                    navController.navigateSingleTopTo("${SingleAccount.route}/$accountType")
                }
            )
        }
        composable(route = Accounts.route) {
            AccountsScreen(
                onAccountClick = { accountType ->
                    navController.navigateSingleTopTo("${SingleAccount.route}/$accountType")
                }
            )
        }
        composable(route = Bills.route) {
            BillsScreen()
        }
        composable(
            route = SingleAccount.routeWithArgs,
            arguments = SingleAccount.arguments,
            deepLinks = SingleAccount.deepLinks
        ) { navBackStackEntry ->
            val accountType =
                navBackStackEntry.arguments?.getString(SingleAccount.accountTypeArg)

            SingleAccountScreen(accountType)
        }
    }
}

@Composable
fun SingleAccountScreen(
    accountType: String? = UserData.accounts.first().name
) {
    // ...
}

private fun NavHostController.navigateToSingleAccount(accountType: String) {
    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
}