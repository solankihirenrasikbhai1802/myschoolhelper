package com.example.myschoolhelper.data

import java.util.Date

data class StudyMaterial(
    val id: String,
    val className: String,
    val subject: String,
    val title: String,
    val pdfUrl: String,
    val uploadDate: Date = Date()
)
