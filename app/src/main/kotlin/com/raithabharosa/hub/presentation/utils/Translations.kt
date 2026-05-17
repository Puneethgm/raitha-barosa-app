package com.raithabharosa.hub.presentation.utils

object Translations {
    private val englishStrings = mapOf(
        "english" to "English",
        "kannada" to "Kannada",
        "hindi" to "Hindi",
        "language" to "Language",
        "data_advices" to "Data advices",
        "sowing_readiness" to "Sowing Readiness (%)",
        "threshold" to "Threshold (75%)",
        "sowing_readiness_label" to "Sowing\nReadiness",
        "scheduled_actions_label_short" to "Scheduled\nActions",
        "weather_today" to "Weather\nToday",
        "dashboard_title" to "Dashboard",
        "settings_title" to "Settings",
        "logout" to "Logout",
        "appearance" to "Appearance",
        "system_theme" to "System",
        "light_theme" to "Light",
        "dark_theme" to "Dark",
        "crop_advice" to "Crop Advice",
        "fertilizer_label" to "Fertilizer",
        "quantity_label" to "Quantity",
        "weather_label" to "Weather",
        "scheduled_actions_label" to "Your Scheduled Actions"
    )

    private val hindiStrings = mapOf(
        "english" to "English",
        "kannada" to "ಕನ್ನಡ",
        "hindi" to "हिन्दी",
        "language" to "भाषा",
        "data_advices" to "डेटा सुझाव",
        "sowing_readiness" to "बुवाई तत्परता (%)",
        "threshold" to "थ्रेशहोल्ड (75%)",
        "sowing_readiness_label" to "बुवाई\nतत्परता",
        "scheduled_actions_label_short" to "निर्धारित\nकार्य",
        "weather_today" to "आज की\nमौसम",
        "dashboard_title" to "डैशबोर्ड",
        "settings_title" to "सेटिंग्स",
        "logout" to "लॉग आउट",
        "appearance" to "दिखावट",
        "system_theme" to "सिस्टम",
        "light_theme" to "हल्का",
        "dark_theme" to "गहरा",
        "crop_advice" to "फसल सलाह",
        "fertilizer_label" to "खाद",
        "quantity_label" to "मात्रा",
        "weather_label" to "मौसम",
        "scheduled_actions_label" to "आपके निर्धारित कार्य"
    )

    private val kannadaStrings = mapOf(
        "english" to "English",
        "kannada" to "ಕನ್ನಡ",
        "hindi" to "ಹಿಂದಿ",
        "language" to "ಭಾಷೆ",
        "data_advices" to "ಡೇಟಾ ಸಲಹೆಗಳು",
        "sowing_readiness" to "ಬುಗ್ಗೆ ತಯಾರಿ (%)",
        "threshold" to "ಥ್ರೆಶೋಲ್ಡ್ (75%)",
        "sowing_readiness_label" to "ಬುಗ್ಗೆ\nತಯಾರಿ",
        "scheduled_actions_label_short" to "ಯೋಜಿಸಿದ\nಕೆಲಸ",
        "weather_today" to "ಇಂದು ಹವಾಮಾನ",
        "dashboard_title" to "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್",
        "settings_title" to "ಸೆಟ್ಟಿಂಗ್‌ಗಳು",
        "logout" to "ಲಾಗ್ ಔಟ್",
        "appearance" to "ಗೋಚರ",
        "system_theme" to "ವ್ಯವಸ್ಥೆ",
        "light_theme" to "ಹಗುರ",
        "dark_theme" to "ಕತ್ತು",
        "crop_advice" to "ಬೆಳೆ ಸಲಹೆ",
        "fertilizer_label" to "ಗೊಬ್ಬರ",
        "quantity_label" to "ಪರಿಮಾಣ",
        "weather_label" to "ಹವಾಮಾನ",
        "scheduled_actions_label" to "ನಿಮ್ಮ ಯೋಜಿಸಿದ ಕೆಲಸಗಳು"
    )

    fun getString(key: String, language: String): String {
        return when (language) {
            "hi" -> hindiStrings[key] ?: englishStrings[key] ?: key
            "kn" -> kannadaStrings[key] ?: englishStrings[key] ?: key
            else -> englishStrings[key] ?: key
        }
    }
}
