package com.example.myschoolhelper.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myschoolhelper.data.local.SessionManager
import com.example.myschoolhelper.ui.components.DashboardLayout
import com.example.myschoolhelper.ui.components.NavItem
import com.example.myschoolhelper.ui.components.StatCard

@Composable
fun AdminDashboard(onLogout: () -> Unit, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userName = sessionManager.getUserName()

    val navItems = listOf(
        NavItem("Schools", Icons.Default.School, "schools"),
        NavItem("Students", Icons.Default.People, "students"),
        NavItem("Exams", Icons.Default.Assignment, "exams"),
        NavItem("Attendance", Icons.Default.CheckCircle, "attendance"),
        NavItem("Reports", Icons.Default.BarChart, "reports"),
        NavItem("Payments", Icons.Default.Payments, "payments")
    )

    DashboardLayout(
        title = "Admin Dashboard",
        role = "System Administrator",
        navItems = navItems,
        onLogout = onLogout,
        onNavigate = onNavigate
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Welcome, $userName!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("System Overview", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard("Total Schools", "125", Icons.Default.Business, Color(0xFF1A73E8), Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                StatCard("Total Students", "45,000", Icons.Default.Groups, Color(0xFF34A853), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard("Paid Schools", "110", Icons.Default.Verified, Color(0xFFFBBC04), Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                StatCard("Pending", "15", Icons.Default.Pending, Color(0xFFEA4335), Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            ActionCard(
                title = "Study Materials",
                icon = Icons.Default.MenuBook,
                color = Color(0xFF673AB7),
                onClick = { onNavigate("study_material_admin") }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("School Payments Status", style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    repeat(5) { i ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Green Valley International #$i")
                            Text(if(i%2==0) "Paid" else "Unpaid", color = if(i%2==0) Color.Green else Color.Red)
                        }
                        if(i < 4) HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun PrincipalDashboard(onLogout: () -> Unit, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userName = sessionManager.getUserName()

    val navItems = listOf(
        NavItem("Students", Icons.Default.People, "students"),
        NavItem("Results", Icons.Default.Grade, "results"),
        NavItem("Announcements", Icons.Default.Campaign, "notices"),
        NavItem("Attendance", Icons.Default.CheckCircle, "attendance"),
        NavItem("Performance", Icons.Default.TrendingUp, "performance")
    )

    DashboardLayout(
        title = "Principal Dashboard",
        role = "Principal - School ERP",
        navItems = navItems,
        onLogout = onLogout,
        onNavigate = onNavigate
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Welcome, $userName!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard("Total Students", "1,200", Icons.Default.Person, Color(0xFF673AB7), Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                StatCard("Fee Collected", "85%", Icons.Default.AccountBalanceWallet, Color(0xFF009688), Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Recent Announcements", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = { onNavigate("notices") }, 
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Announcement")
            }
        }
    }
}

@Composable
fun TeacherDashboard(onLogout: () -> Unit, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userName = sessionManager.getUserName()

    val navItems = listOf(
        NavItem("Mark Attendance", Icons.Default.HowToReg, "attendance"),
        NavItem("Upload Results", Icons.Default.UploadFile, "results"),
        NavItem("Exams", Icons.Default.Quiz, "exams"),
        NavItem("Homework", Icons.Default.MenuBook, "homework"),
        NavItem("Class List", Icons.Default.FormatListBulleted, "classes")
    )

    DashboardLayout(
        title = "Teacher Dashboard",
        role = "Teacher - School ERP",
        navItems = navItems,
        onLogout = onLogout,
        onNavigate = onNavigate
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Welcome, $userName!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(300.dp)
            ) {
                item { ActionCard("Daily Attendance", Icons.Default.CheckCircle, Color(0xFF4CAF50), onClick = { onNavigate("attendance") }) }
                item { ActionCard("Upload Marks", Icons.Default.Publish, Color(0xFF2196F3)) }
                item { ActionCard("Create Test", Icons.Default.AddBox, Color(0xFFFF9800)) }
                item { ActionCard("Assign Homework", Icons.Default.Edit, Color(0xFF9C27B0)) }
            }
        }
    }
}

@Composable
fun StudentDashboard(onLogout: () -> Unit, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userName = sessionManager.getUserName()

    val navItems = listOf(
        NavItem("Attendance", Icons.Default.CalendarToday, "attendance"),
        NavItem("Results", Icons.Default.Star, "results"),
        NavItem("Homework", Icons.Default.Description, "homework"),
        NavItem("Fees", Icons.Default.Payments, "fees"),
        NavItem("Notice Board", Icons.Default.Notifications, "notices")
    )

    DashboardLayout(
        title = "Student Dashboard",
        role = "${userName} - Grade 10A",
        navItems = navItems,
        onLogout = onLogout,
        onNavigate = onNavigate
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Welcome back, $userName!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            StatCard("My Attendance", "92%", Icons.Default.Timeline, Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    ActionCard(
                        title = "Study Materials",
                        icon = Icons.Default.AutoStories,
                        color = Color(0xFF2196F3),
                        onClick = { onNavigate("study_material_student") }
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.weight(1f)) {
                    ActionCard(
                        title = "My Fees",
                        icon = Icons.Default.Payments,
                        color = Color(0xFF4CAF50),
                        onClick = { onNavigate("fees") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Upcoming Exams", style = MaterialTheme.typography.titleMedium)
            repeat(3) { i ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Mathematics Mid-term", fontWeight = FontWeight.Bold)
                            Text("Dec 15, 2024", fontSize = 12.sp, color = Color.Gray)
                        }
                        Button(onClick = {}) { Text("View") }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(title: String, icon: ImageVector, color: Color, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}
