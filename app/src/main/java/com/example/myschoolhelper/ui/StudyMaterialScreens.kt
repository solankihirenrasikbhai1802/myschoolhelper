package com.example.myschoolhelper.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myschoolhelper.data.StudyMaterial
import com.example.myschoolhelper.data.StudyMaterialRepository
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyMaterialAdminScreen(onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var pdfUrl by remember { mutableStateOf("") }

    val classes = listOf("Grade 9", "Grade 10", "Grade 11", "Grade 12")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Study Material") },
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
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (Chapter Name)") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedClass,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Class/Grade") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    classes.forEach { className ->
                        DropdownMenuItem(
                            text = { Text(className) },
                            onClick = {
                                selectedClass = className
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pdfUrl,
                onValueChange = { pdfUrl = it },
                label = { Text("PDF URL (Simulated Upload)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://example.com/file.pdf") }
            )

            Button(
                onClick = {
                    if (title.isNotBlank() && selectedClass.isNotBlank() && subject.isNotBlank()) {
                        StudyMaterialRepository.addMaterial(
                            StudyMaterial(
                                id = UUID.randomUUID().toString(),
                                className = selectedClass,
                                subject = subject,
                                title = title,
                                pdfUrl = if (pdfUrl.isBlank()) "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf" else pdfUrl
                            )
                        )
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Upload Material")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyMaterialStudentScreen(studentClass: String, onBack: () -> Unit) {
    val materials = StudyMaterialRepository.getMaterialsForClass(studentClass)
    val groupedMaterials = materials.groupBy { it.subject }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Materials - $studentClass") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (materials.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No study materials available for your class yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedMaterials.forEach { (subject, items) ->
                    item {
                        Text(
                            text = subject,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(items) { material ->
                        StudyMaterialItem(material)
                    }
                }
            }
        }
    }
}

@Composable
fun StudyMaterialItem(material: StudyMaterial) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = material.title, fontWeight = FontWeight.Bold)
                Text(text = material.subject, fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = { 
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(material.pdfUrl))
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Download, contentDescription = "View/Download")
            }
        }
    }
}
