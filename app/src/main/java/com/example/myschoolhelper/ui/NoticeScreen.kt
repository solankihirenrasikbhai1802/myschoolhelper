package com.example.myschoolhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Notice(val title: String, val date: String, val content: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeScreen(onBack: () -> Unit) {
    val notices = listOf(
        Notice("Summer Vacation", "2024-05-15", "School will remain closed from June 1st to June 30th for summer break."),
        Notice("Exam Schedule", "2024-05-10", "Final exams for all classes will start from May 25th. Check the timetable."),
        Notice("New Uniform Policy", "2024-05-01", "Starting from next session, a new uniform policy will be implemented.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notice Board") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(notices) { notice ->
                NoticeItem(notice)
            }
        }
    }
}

@Composable
fun NoticeItem(notice: Notice) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFFFA500))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = notice.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Text(text = notice.date, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = notice.content)
        }
    }
}
