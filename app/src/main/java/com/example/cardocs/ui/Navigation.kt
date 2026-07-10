package com.example.cardocs.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "cars"
    ){
        composable("cars") {
            CarsScreen(
                onCarClick = { carId, carName ->
                    navController.navigate("documents/$carId/$carName")
                }
            )
        }

        composable(
            route = "documents/{carId}/{carName}",
            arguments = listOf(
                navArgument("carId") { type = NavType.IntType },
                navArgument("carName") { type = NavType.StringType }
            )
        ) {
            backStackEntry ->
            val carId = backStackEntry.arguments?.getInt("carId") ?: return@composable
            val carName = backStackEntry.arguments?.getString("carName") ?: ""

            DocumentsScreen(
                carId = carId,
                carName = carName,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}