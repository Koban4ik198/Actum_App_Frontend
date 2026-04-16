package com.actum.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.actum.app.screens.CreateTaskScreen
import com.actum.app.screens.LoginScreen
import com.actum.app.screens.ManagerScreen
import com.actum.app.screens.RoleScreen
import com.actum.app.screens.SpecialistTasksScreen

object Routes {
    const val ROLE = "role"
    const val LOGIN_SPECIALIST = "login_specialist"
    const val LOGIN_MANAGER = "login_manager"
    const val SPECIALIST_TASKS = "specialist_tasks"
    const val MANAGER = "manager"
    const val CREATE_TASK = "create_task"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.ROLE
    ) {
        composable(Routes.ROLE) {
            RoleScreen(
                onSpecialistClick = {
                    navController.navigate(Routes.LOGIN_SPECIALIST)
                },
                onManagerClick = {
                    navController.navigate(Routes.LOGIN_MANAGER)
                }
            )
        }

        composable(Routes.LOGIN_SPECIALIST) {
            LoginScreen(
                role = "Специалист",
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(Routes.SPECIALIST_TASKS)
                }
            )
        }

        composable(Routes.LOGIN_MANAGER) {
            LoginScreen(
                role = "Менеджер",
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(Routes.MANAGER)
                }
            )
        }

        composable(Routes.SPECIALIST_TASKS) {
            SpecialistTasksScreen(
                onBackClick = {
                    navController.navigate(Routes.ROLE) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Routes.MANAGER) {
            ManagerScreen(
                onBackClick = {
                    navController.navigate(Routes.ROLE) {
                        popUpTo(0)
                    }
                },
                onCreateClick = {
                    navController.navigate(Routes.CREATE_TASK)
                }
            )
        }

        composable(Routes.CREATE_TASK) {
            CreateTaskScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onTaskCreated = {
                    navController.popBackStack()
                }
            )
        }
    }
}