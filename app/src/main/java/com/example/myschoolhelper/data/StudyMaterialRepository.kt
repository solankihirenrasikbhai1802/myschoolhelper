package com.example.myschoolhelper.data

import androidx.compose.runtime.mutableStateListOf

object StudyMaterialRepository {
    private val _materials = mutableStateListOf<StudyMaterial>()
    val materials: List<StudyMaterial> get() = _materials

    fun addMaterial(material: StudyMaterial) {
        _materials.add(material)
    }

    fun getMaterialsForClass(className: String): List<StudyMaterial> {
        return _materials.filter { it.className == className }
    }
}
