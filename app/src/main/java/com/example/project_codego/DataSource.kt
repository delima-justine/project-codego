package com.example.project_codego

object DataSource {
    
    fun getIconForContact(icon: String): String {
        return when (icon) {
            "emergency" -> "🚨"
            "coast_guard" -> "⚓"
            "fire" -> "🔥"
            "red_cross" -> "🏥"
            "public_works" -> "🏗️"
            else -> "📞"
        }
    }
}
