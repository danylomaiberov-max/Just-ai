package com.example.data.templates

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class PromptTemplate(
    val id: String,
    val name: String,
    val description: String,
    val systemPrompt: String,
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val contextWindow: Int = 4096,
    val category: String = "Общие",
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("name", name)
            put("description", description)
            put("systemPrompt", systemPrompt)
            put("temperature", temperature.toDouble())
            put("topP", topP.toDouble())
            put("contextWindow", contextWindow)
            put("category", category)
            put("isCustom", isCustom)
            put("createdAt", createdAt)
        }
    }

    companion object {
        fun fromJsonObject(json: JSONObject): PromptTemplate {
            return PromptTemplate(
                id = json.optString("id", "tpl_${System.currentTimeMillis()}"),
                name = json.optString("name", "Пользовательский шаблон"),
                description = json.optString("description", ""),
                systemPrompt = json.optString("systemPrompt", "You are a helpful assistant."),
                temperature = json.optDouble("temperature", 0.7).toFloat(),
                topP = json.optDouble("topP", 0.9).toFloat(),
                contextWindow = json.optInt("contextWindow", 4096),
                category = json.optString("category", "Кастомные"),
                isCustom = json.optBoolean("isCustom", true),
                createdAt = json.optLong("createdAt", System.currentTimeMillis())
            )
        }
    }
}

object PromptTemplateManager {

    private const val PREFS_NAME = "aether_prompt_templates"
    private const val KEY_CUSTOM_TEMPLATES = "custom_templates_json"
    private const val KEY_ACTIVE_TEMPLATE_ID = "active_template_id"

    val defaultTemplates: List<PromptTemplate> = listOf(
        PromptTemplate(
            id = "deepseek_r1_reasoner",
            name = "DeepSeek-R1 Deep Reasoner",
            description = "Глубокое пошаговое логическое рассуждение с генерацией мыслей <think>...",
            systemPrompt = "You are DeepSeek-R1, an expert reasoning AI running natively on local hardware. Think through problems step-by-step inside <think> tags before delivering a concise, rigorous answer.",
            temperature = 0.6f,
            topP = 0.95f,
            contextWindow = 8192,
            category = "Логика и R1"
        ),
        PromptTemplate(
            id = "cpp_rust_architect",
            name = "C++20 & Rust Архитектор",
            description = "Генерация безопасного, zero-overhead и многопоточного кода для компилятора IDE.",
            systemPrompt = "You are a Principal Systems Engineer specializing in C++20/23, Rust 2024 edition, and low-level memory efficiency. Always provide complete, compilable, and highly optimized code.",
            temperature = 0.2f,
            topP = 0.85f,
            contextWindow = 8192,
            category = "Код и Системы"
        ),
        PromptTemplate(
            id = "pocketpal_cyber_assistant",
            name = "PocketPal AI Cyber Assistant",
            description = "Стандартный универсальный помощник в стиле PocketPal AI со 100% приватностью.",
            systemPrompt = "You are PocketPal AI Core, a fast and helpful on-device assistant. Answer directly, concisely, and protect user data at all times.",
            temperature = 0.7f,
            topP = 0.9f,
            contextWindow = 4096,
            category = "PocketPal Core"
        ),
        PromptTemplate(
            id = "creative_storyteller",
            name = "Креативный писатель и Сценарист",
            description = "Высокая креативность, образный слог, генерация историй, диалогов и сценариев.",
            systemPrompt = "You are a master fiction author and screenwriter. Create immersive, atmospheric prose with vivid descriptions and sharp dialog.",
            temperature = 1.1f,
            topP = 0.98f,
            contextWindow = 4096,
            category = "Творчество"
        ),
        PromptTemplate(
            id = "math_symbolic_solver",
            name = "Математик и Аналитик данных",
            description = "Точные аналитические вычисления, алгоритмы, линейная алгебра и математический анализ.",
            systemPrompt = "You are a mathematical engine. Formulate symbolic proofs, solve algebraic equations step by step, and verify numerical solutions rigorously.",
            temperature = 0.1f,
            topP = 0.7f,
            contextWindow = 4096,
            category = "Точные науки"
        )
    )

    fun loadAllTemplates(context: Context): List<PromptTemplate> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val customJson = prefs.getString(KEY_CUSTOM_TEMPLATES, null)
        val customList = mutableListOf<PromptTemplate>()

        if (!customJson.isNullOrBlank()) {
            try {
                val array = JSONArray(customJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    customList.add(PromptTemplate.fromJsonObject(obj))
                }
            } catch (_: Exception) {}
        }

        return defaultTemplates + customList
    }

    fun saveCustomTemplates(context: Context, customTemplates: List<PromptTemplate>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val array = JSONArray()
        customTemplates.filter { it.isCustom }.forEach { tpl ->
            array.put(tpl.toJsonObject())
        }
        prefs.edit().putString(KEY_CUSTOM_TEMPLATES, array.toString()).apply()
    }

    fun getActiveTemplateId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACTIVE_TEMPLATE_ID, defaultTemplates.first().id) ?: defaultTemplates.first().id
    }

    fun setActiveTemplateId(context: Context, templateId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ACTIVE_TEMPLATE_ID, templateId).apply()
    }

    fun exportToJson(templates: List<PromptTemplate>): String {
        val root = JSONObject()
        root.put("version", "1.0")
        root.put("exportedAt", System.currentTimeMillis())
        root.put("appName", "Aether AI (PocketPal Compatible)")

        val array = JSONArray()
        templates.forEach { tpl ->
            array.put(tpl.toJsonObject())
        }
        root.put("templates", array)
        return root.toString(2)
    }

    fun importFromJson(jsonString: String): List<PromptTemplate> {
        val imported = mutableListOf<PromptTemplate>()
        val root = JSONObject(jsonString)

        val array = if (root.has("templates")) {
            root.getJSONArray("templates")
        } else if (jsonString.trim().startsWith("[")) {
            JSONArray(jsonString)
        } else {
            val arr = JSONArray()
            arr.put(root)
            arr
        }

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val tpl = PromptTemplate.fromJsonObject(obj).copy(
                id = "imported_" + System.currentTimeMillis() + "_" + i,
                isCustom = true
            )
            imported.add(tpl)
        }
        return imported
    }
}
