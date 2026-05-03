package com.example.myschoolhelper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myschoolhelper.data.local.SessionManager
import com.example.myschoolhelper.ui.theme.SchoolPrimary
import com.example.myschoolhelper.ui.theme.SchoolTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    
    val name = sessionManager.getUserName()
    val email = sessionManager.getUserEmail()
    val role = sessionManager.getUserRole()
    val schoolId = sessionManager.getSchoolId() ?: "N/A"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(SchoolTertiary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = SchoolPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = role, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            ProfileInfoCard(icon = Icons.Default.Email, label = "Email", value = email)
            Spacer(modifier = Modifier.height(12.dp))
            ProfileInfoCard(icon = Icons.Default.School, label = "School ID", value = schoolId)
            Spacer(modifier = Modifier.height(12.dp))
            ProfileInfoCard(icon = Icons.Default.Badge, label = "User Role", value = role)
        }
    }
}

@Composable
fun ProfileInfoCard(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = SchoolPrimary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
