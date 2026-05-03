package com.example.myschoolhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myschoolhelper.data.local.SessionManager
import com.example.myschoolhelper.ui.*
import com.example.myschoolhelper.ui.theme.MYSCHOOLHELPERTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MYSCHOOLHELPERTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = {
                val nextDestination = if (sessionManager.isLoggedIn()) {
                    when (sessionManager.getUserRole().lowercase()) {  // Convert to lowercase for comparison
                        "admin" -> Screen.AdminDashboard.route
                        "principal" -> Screen.PrincipalDashboard.route
                        "teacher" -> Screen.TeacherDashboard.route
                        "student" -> Screen.StudentDashboard.route
                        else -> Screen.Login.route
                    }
                } else {
                    Screen.Login.route
                }
                navController.navigate(nextDestination) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = { role ->
                val destination = when (role.lowercase()) {  // Convert to lowercase for comparison
                    "admin" -> Screen.AdminDashboard.route
                    "principal" -> Screen.PrincipalDashboard.route
                    "teacher" -> Screen.TeacherDashboard.route
                    "student" -> Screen.StudentDashboard.route
                    else -> Screen.Login.route
                }
                navController.navigate(destination) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        
        val commonOnLogout = {
            sessionManager.logout()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }

        val commonOnNavigate = { route: String ->
            when (route) {
                "profile" -> navController.navigate(Screen.Profile.route)
                "notices" -> navController.navigate(Screen.Notices.route)
                "attendance" -> navController.navigate(Screen.Attendance.route)
                "fees" -> navController.navigate(Screen.Fees.route)
                "study_material_admin" -> navController.navigate(Screen.StudyMaterialAdmin.route)
                "study_material_student" -> navController.navigate(Screen.StudyMaterialStudent.route)
            }
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboard(
                onLogout = commonOnLogout,
                onNavigate = commonOnNavigate
            )
        }
        composable(Screen.PrincipalDashboard.route) {
            PrincipalDashboard(
                onLogout = commonOnLogout,
                onNavigate = commonOnNavigate
            )
        }
        composable(Screen.TeacherDashboard.route) {
            TeacherDashboard(
                onLogout = commonOnLogout,
                onNavigate = commonOnNavigate
            )
        }
        composable(Screen.StudentDashboard.route) {
            StudentDashboard(
                onLogout = commonOnLogout,
                onNavigate = commonOnNavigate
            )
        }
        composable(Screen.StudyMaterialAdmin.route) {
            StudyMaterialAdminScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.StudyMaterialStudent.route) {
            StudyMaterialStudentScreen(studentClass = "Grade 10", onBack = { navController.popBackStack() })
        }
        composable(Screen.Profile.route) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Attendance.route) {
            AttendanceScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Notices.route) {
            NoticeScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Fees.route) {
            FeesScreen(onBack = { navController.popBackStack() })
        }
    }
}
