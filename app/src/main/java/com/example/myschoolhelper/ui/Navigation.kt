package com.example.myschoolhelper.ui

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object AdminDashboard : Screen("admin_dashboard")
    object PrincipalDashboard : Screen("principal_dashboard")
    object TeacherDashboard : Screen("teacher_dashboard")
    object StudentDashboard : Screen("student_dashboard")
    object StudyMaterialAdmin : Screen("study_material_admin")
    object StudyMaterialStudent : Screen("study_material_student")
    object Profile : Screen("profile")
    object Attendance : Screen("attendance")
    object Notices : Screen("notices")
    object Fees : Screen("fees")
}
