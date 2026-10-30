package com.suraksha.india.model

data class SafetyGuide(
    val id: String,
    val title: String,
    val summary: String,
    val steps: List<String>
)
