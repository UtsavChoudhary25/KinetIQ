package com.utsav.kinetiq.data.remote

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.utsav.kinetiq.domain.model.MealAnalysisResult
import com.utsav.kinetiq.domain.model.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GeminiRepository(
    initialApiKey: String
) {
    private var apiKey: String = initialApiKey

    private val candidateModels = listOf(
        "gemini-1.5-flash",
        "gemini-1.5-flash-latest",
        "gemini-1.5-pro",
        "gemini-2.0-flash"
    )

    fun updateApiKey(newKey: String) {
        if (newKey.isNotBlank()) {
            apiKey = newKey
            Log.d("GeminiRepository", "API key updated successfully.")
        }
    }

    private fun isOpenRouterKey(key: String): Boolean {
        val trimmed = key.trim()
        return trimmed.startsWith("sk-or-") || trimmed.startsWith("sk-") || trimmed.contains("openrouter")
    }

    private fun getModel(modelName: String, jsonMode: Boolean = false): GenerativeModel {
        return if (jsonMode) {
            GenerativeModel(
                modelName = modelName,
                apiKey = apiKey,
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                }
            )
        } else {
            GenerativeModel(
                modelName = modelName,
                apiKey = apiKey
            )
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private suspend fun callOpenRouter(
        promptText: String,
        jsonMode: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val models = listOf(
            "google/gemini-2.0-flash-001",
            "google/gemini-flash-1.5",
            "openai/gpt-4o-mini",
            "meta-llama/llama-3.2-11b-vision-instruct:free"
        )

        var lastError: Exception? = null

        for (model in models) {
            try {
                val url = URL("https://openrouter.ai/api/v1/chat/completions")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", "Bearer ${apiKey.trim()}")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("HTTP-Referer", "https://kinetiq.app")
                conn.setRequestProperty("X-Title", "KINETIQ Fitness App")
                conn.doOutput = true
                conn.connectTimeout = 15000
                conn.readTimeout = 30000

                val body = JSONObject()
                body.put("model", model)

                val messagesArray = JSONArray()
                val userMsg = JSONObject()
                userMsg.put("role", "user")
                userMsg.put("content", promptText)
                messagesArray.put(userMsg)
                body.put("messages", messagesArray)

                if (jsonMode) {
                    val responseFormat = JSONObject()
                    responseFormat.put("type", "json_object")
                    body.put("response_format", responseFormat)
                }

                OutputStreamWriter(conn.outputStream).use { writer ->
                    writer.write(body.toString())
                    writer.flush()
                }

                val responseCode = conn.responseCode
                val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
                val responseString = BufferedReader(InputStreamReader(stream)).use { it.readText() }

                if (responseCode in 200..299) {
                    val responseJson = JSONObject(responseString)
                    val choices = responseJson.optJSONArray("choices")
                    if (choices != null && choices.length() > 0) {
                        val messageObj = choices.getJSONObject(0).optJSONObject("message")
                        val content = messageObj?.optString("content")
                        if (!content.isNullOrBlank()) {
                            return@withContext content
                        }
                    }
                } else {
                    Log.e("OpenRouter", "Model $model returned error code $responseCode: $responseString")
                    lastError = Exception("OpenRouter error ($responseCode): $responseString")
                }
            } catch (e: Exception) {
                Log.e("OpenRouter", "Failed call for model $model: ${e.message}", e)
                lastError = e
            }
        }
        throw lastError ?: Exception("OpenRouter requests failed.")
    }

    private suspend fun callOpenRouterVision(
        bitmap: Bitmap,
        promptText: String
    ): String = withContext(Dispatchers.IO) {
        val processedBitmap = prepareBitmap(bitmap)
        val outputStream = ByteArrayOutputStream()
        processedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        val imageUrl = "data:image/jpeg;base64,$base64Image"

        val visionModels = listOf(
            "google/gemini-2.0-flash-001",
            "google/gemini-flash-1.5",
            "openai/gpt-4o-mini",
            "meta-llama/llama-3.2-11b-vision-instruct:free"
        )

        var lastError: Exception? = null

        for (model in visionModels) {
            try {
                val url = URL("https://openrouter.ai/api/v1/chat/completions")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", "Bearer ${apiKey.trim()}")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("HTTP-Referer", "https://kinetiq.app")
                conn.setRequestProperty("X-Title", "KINETIQ Fitness App")
                conn.doOutput = true
                conn.connectTimeout = 20000
                conn.readTimeout = 40000

                val body = JSONObject()
                body.put("model", model)

                val responseFormat = JSONObject()
                responseFormat.put("type", "json_object")
                body.put("response_format", responseFormat)

                val messagesArray = JSONArray()
                val userMsg = JSONObject()
                userMsg.put("role", "user")

                val contentArray = JSONArray()

                val textPart = JSONObject()
                textPart.put("type", "text")
                textPart.put("text", promptText)
                contentArray.put(textPart)

                val imagePart = JSONObject()
                imagePart.put("type", "image_url")
                val imageObj = JSONObject()
                imageObj.put("url", imageUrl)
                imagePart.put("image_url", imageObj)
                contentArray.put(imagePart)

                userMsg.put("content", contentArray)
                messagesArray.put(userMsg)
                body.put("messages", messagesArray)

                OutputStreamWriter(conn.outputStream).use { writer ->
                    writer.write(body.toString())
                    writer.flush()
                }

                val responseCode = conn.responseCode
                val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
                val responseString = BufferedReader(InputStreamReader(stream)).use { it.readText() }

                if (responseCode in 200..299) {
                    val responseJson = JSONObject(responseString)
                    val choices = responseJson.optJSONArray("choices")
                    if (choices != null && choices.length() > 0) {
                        val messageObj = choices.getJSONObject(0).optJSONObject("message")
                        val content = messageObj?.optString("content")
                        if (!content.isNullOrBlank()) {
                            return@withContext content
                        }
                    }
                } else {
                    Log.e("OpenRouterVision", "Model $model returned error code $responseCode: $responseString")
                    lastError = Exception("OpenRouter vision error ($responseCode): $responseString")
                }
            } catch (e: Exception) {
                Log.e("OpenRouterVision", "Failed vision call for model $model: ${e.message}", e)
                lastError = e
            }
        }
        throw lastError ?: Exception("OpenRouter vision requests failed.")
    }

    suspend fun generateWorkout(
        goal: String,
        muscleGroup: String,
        durationMinutes: Int,
        equipment: String,
        fitnessLevel: String = "Intermediate",
        workoutStyle: String = "Straight Sets",
        limitations: String = "None"
    ): Workout? {
        val prompt = """
            Generate a high-performance, personalized workout plan for a premium fitness app named KINETIQ.
            User Profile & Preferences:
            - Primary Fitness Goal: $goal
            - Target Muscle Group / Split: $muscleGroup
            - Available Session Time: $durationMinutes minutes
            - Available Equipment: $equipment
            - Fitness Experience Level: $fitnessLevel
            - Workout Format Style: $workoutStyle
            - Physical Limitations / Focus: $limitations
            
            Provide a science-backed workout sequence with 4-6 exercises.
            Return the response strictly as a JSON object with this exact structure:
            {
              "id": "generated_id",
              "title": "Short Catchy Session Title",
              "durationMinutes": $durationMinutes,
              "difficulty": "$fitnessLevel",
              "aiRationale": "1-2 sentence concise scientific explanation of why this sequence and volume targets the user's $goal goals effectively.",
              "restSeconds": 60,
              "exercises": [
                {
                  "exercise": {
                    "id": "ex_1",
                    "name": "Exercise Name",
                    "muscleGroup": "Target Muscle",
                    "equipment": "Required Equipment",
                    "instructions": "Key form cues and execution technique."
                  },
                  "sets": [
                    { "reps": 12, "weight": 20.0, "isCompleted": false },
                    { "reps": 12, "weight": 20.0, "isCompleted": false },
                    { "reps": 12, "weight": 20.0, "isCompleted": false }
                  ]
                }
              ]
            }
        """.trimIndent()

        if (isOpenRouterKey(apiKey)) {
            try {
                val text = callOpenRouter(prompt, jsonMode = true)
                val jsonContent = if (text.startsWith("```json")) {
                    text.substringAfter("```json").substringBeforeLast("```").trim()
                } else {
                    text
                }
                return json.decodeFromString<Workout>(jsonContent)
            } catch (e: Exception) {
                Log.e("GeminiRepository", "OpenRouter workout generation error: ${e.message}", e)
                return null
            }
        }

        for (modelName in candidateModels) {
            try {
                val model = getModel(modelName, jsonMode = true)
                val response = model.generateContent(prompt)
                val text = response.text?.trim() ?: continue
                val jsonContent = if (text.startsWith("```json")) {
                    text.substringAfter("```json").substringBeforeLast("```").trim()
                } else {
                    text
                }
                val workout = json.decodeFromString<Workout>(jsonContent)
                return workout
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    suspend fun getCoachResponse(message: String): String {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isBlank()) {
            return "Error: API key is empty. Please tap ⚙️ settings and enter your API key."
        }

        if (isOpenRouterKey(trimmedKey)) {
            return try {
                val prompt = "You are KINETIQ Coach, a premium, professional AI fitness trainer. Be concise, encouraging, and science-based. User says: $message"
                callOpenRouter(prompt)
            } catch (e: Exception) {
                e.printStackTrace()
                "Error: ${e.localizedMessage ?: "OpenRouter connection failed"}. Check your API key and internet."
            }
        }

        if (!trimmedKey.startsWith("AIza")) {
            return "Error: Invalid key format. OpenRouter keys start with 'sk-or-' and Google keys start with 'AIza...'. Get a key and update in settings (⚙️)."
        }

        val prompt = "You are KINETIQ Coach, a premium, professional AI fitness trainer. Be concise, encouraging, and science-based. User says: $message"
        
        var lastException: Exception? = null
        for (modelName in candidateModels) {
            try {
                val model = getModel(modelName)
                val response = model.generateContent(prompt)
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return text
                }
            } catch (e: Exception) {
                lastException = e
                e.printStackTrace()
                val err = e.localizedMessage ?: ""
                if (err.contains("404") || err.contains("not found", ignoreCase = true) || err.contains("GRpcError")) {
                    continue
                } else {
                    break
                }
            }
        }
        return "Error: ${lastException?.localizedMessage ?: "Connection failed"}. Make sure 'Generative Language API' is active on your Google Cloud project."
    }

    private fun prepareBitmap(source: Bitmap): Bitmap {
        val config = if (source.config == Bitmap.Config.HARDWARE) {
            Bitmap.Config.ARGB_8888
        } else {
            source.config ?: Bitmap.Config.ARGB_8888
        }
        val softwareBitmap = if (source.config == Bitmap.Config.HARDWARE) {
            source.copy(config, false) ?: source
        } else {
            source
        }

        val maxDimension = 1024
        val width = softwareBitmap.width
        val height = softwareBitmap.height

        return if (width > maxDimension || height > maxDimension) {
            val scale = maxDimension.toFloat() / maxOf(width, height)
            Bitmap.createScaledBitmap(softwareBitmap, (width * scale).toInt(), (height * scale).toInt(), true)
        } else {
            softwareBitmap
        }
    }

    suspend fun analyzeMealImage(bitmap: Bitmap): MealAnalysisResult {
        if (apiKey.isBlank()) {
            throw IllegalArgumentException("API Key is missing. Please set your API Key in AI Coach settings.")
        }

        val prompt = """
            You are KINETIQ AI Nutritionist. Carefully analyze the food/dish in the attached image.
            Identify the specific food, dish, or beverage shown. Estimate total calories, macronutrients (protein, carbs, fats in grams),
            provide a health rating ("Healthy", "Moderate", or "High Calorie"), list individual ingredients or items detected, and give a concise dietary tip.

            Output JSON with this exact schema:
            {
              "foodName": "Name of the detected dish or food item",
              "estimatedCalories": 350,
              "proteinGrams": 25.0,
              "carbsGrams": 40.0,
              "fatsGrams": 12.0,
              "healthRating": "Healthy",
              "breakdownItems": ["Ingredient 1", "Ingredient 2"],
              "tips": "Practical nutritional advice"
            }
        """.trimIndent()

        if (isOpenRouterKey(apiKey)) {
            try {
                val text = callOpenRouterVision(bitmap, prompt)
                val jsonStartIndex = text.indexOf('{')
                val jsonEndIndex = text.lastIndexOf('}')

                val jsonContent = if (jsonStartIndex != -1 && jsonEndIndex != -1 && jsonEndIndex > jsonStartIndex) {
                    text.substring(jsonStartIndex, jsonEndIndex + 1)
                } else {
                    text
                }

                return json.decodeFromString<MealAnalysisResult>(jsonContent)
            } catch (e: Exception) {
                Log.e("GeminiRepository", "OpenRouter vision analysis error: ${e.message}", e)
                throw IllegalStateException("Could not analyze meal image with OpenRouter AI: ${e.message}")
            }
        }

        val processedBitmap = prepareBitmap(bitmap)

        val inputContent = content {
            image(processedBitmap)
            text(prompt)
        }

        var lastException: Exception? = null
        val visionModels = listOf("gemini-1.5-flash", "gemini-1.5-pro", "gemini-2.0-flash")

        for (modelName in visionModels) {
            try {
                Log.d("GeminiRepository", "Analyzing meal image with model: $modelName")
                val model = getModel(modelName, jsonMode = true)
                val response = model.generateContent(inputContent)
                val text = response.text?.trim()
                Log.d("GeminiRepository", "Gemini response text: $text")

                if (text.isNullOrBlank()) continue

                val jsonStartIndex = text.indexOf('{')
                val jsonEndIndex = text.lastIndexOf('}')

                val jsonContent = if (jsonStartIndex != -1 && jsonEndIndex != -1 && jsonEndIndex > jsonStartIndex) {
                    text.substring(jsonStartIndex, jsonEndIndex + 1)
                } else {
                    text
                }

                return json.decodeFromString<MealAnalysisResult>(jsonContent)
            } catch (e: Exception) {
                lastException = e
                Log.e("GeminiRepository", "Error analyzing meal with $modelName: ${e.localizedMessage}", e)
            }
        }

        throw lastException ?: IllegalStateException("Could not analyze meal image with Gemini AI.")
    }

    suspend fun generateRecipeFromIngredients(availableIngredients: String): com.utsav.kinetiq.domain.model.Recipe? {
        if (apiKey.isBlank()) return null

        val prompt = """
            You are KINETIQ Chef AI. Generate a single healthy, delicious recipe using primarily these available ingredients: $availableIngredients.
            Return the response strictly as a JSON object matching this exact schema:
            {
              "id": "recipe_ai_${System.currentTimeMillis()}",
              "title": "Creative Healthy Recipe Title",
              "description": "Short appetizing description explaining the nutrition benefits.",
              "category": "High Protein",
              "fitnessGoal": "Muscle Growth",
              "prepTimeMinutes": 10,
              "cookTimeMinutes": 15,
              "calories": 420,
              "proteinGrams": 32,
              "carbsGrams": 38,
              "fatGrams": 12,
              "servings": 2,
              "difficulty": "Easy",
              "imageUrl": "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=800&q=80",
              "ingredients": ["1 cup ingredient 1", "2 tbsp ingredient 2"],
              "instructions": ["Step 1 prep instructions", "Step 2 cooking instructions"],
              "isFavorite": false
            }
        """.trimIndent()

        if (isOpenRouterKey(apiKey)) {
            try {
                val text = callOpenRouter(prompt, jsonMode = true)
                val jsonStartIndex = text.indexOf('{')
                val jsonEndIndex = text.lastIndexOf('}')
                val jsonContent = if (jsonStartIndex != -1 && jsonEndIndex != -1 && jsonEndIndex > jsonStartIndex) {
                    text.substring(jsonStartIndex, jsonEndIndex + 1)
                } else text
                return json.decodeFromString<com.utsav.kinetiq.domain.model.Recipe>(jsonContent)
            } catch (e: Exception) {
                Log.e("GeminiRepository", "Error generating recipe with OpenRouter: ${e.message}", e)
                return null
            }
        }

        for (modelName in candidateModels) {
            try {
                val model = getModel(modelName, jsonMode = true)
                val response = model.generateContent(prompt)
                val text = response.text?.trim() ?: continue
                val jsonStartIndex = text.indexOf('{')
                val jsonEndIndex = text.lastIndexOf('}')
                val jsonContent = if (jsonStartIndex != -1 && jsonEndIndex != -1 && jsonEndIndex > jsonStartIndex) {
                    text.substring(jsonStartIndex, jsonEndIndex + 1)
                } else text
                return json.decodeFromString<com.utsav.kinetiq.domain.model.Recipe>(jsonContent)
            } catch (e: Exception) {
                Log.e("GeminiRepository", "Error generating recipe with $modelName: ${e.message}")
            }
        }
        return null
    }

    suspend fun analyzeSpokenMeal(spokenText: String): MealAnalysisResult? {
        if (apiKey.isBlank()) return null

        val prompt = """
            You are KINETIQ AI Nutritionist. Parse the user's spoken meal description: "$spokenText".
            Identify food items, estimate total calories, macronutrients (protein, carbs, fats in grams),
            provide a health rating ("Healthy", "Moderate", or "High Calorie"), list breakdown items, and give a concise tip.

            Output JSON with this exact schema:
            {
              "foodName": "Name of the spoken meal/food",
              "estimatedCalories": 400,
              "proteinGrams": 28.0,
              "carbsGrams": 45.0,
              "fatsGrams": 14.0,
              "healthRating": "Healthy",
              "breakdownItems": ["Item 1", "Item 2"],
              "tips": "Quick nutritional advice"
            }
        """.trimIndent()

        if (isOpenRouterKey(apiKey)) {
            try {
                val text = callOpenRouter(prompt, jsonMode = true)
                val jsonStartIndex = text.indexOf('{')
                val jsonEndIndex = text.lastIndexOf('}')
                val jsonContent = if (jsonStartIndex != -1 && jsonEndIndex != -1 && jsonEndIndex > jsonStartIndex) {
                    text.substring(jsonStartIndex, jsonEndIndex + 1)
                } else text
                return json.decodeFromString<MealAnalysisResult>(jsonContent)
            } catch (e: Exception) {
                Log.e("GeminiRepository", "Error analyzing spoken meal with OpenRouter: ${e.message}", e)
                return null
            }
        }

        for (modelName in candidateModels) {
            try {
                val model = getModel(modelName, jsonMode = true)
                val response = model.generateContent(prompt)
                val text = response.text?.trim() ?: continue
                val jsonStartIndex = text.indexOf('{')
                val jsonEndIndex = text.lastIndexOf('}')
                val jsonContent = if (jsonStartIndex != -1 && jsonEndIndex != -1 && jsonEndIndex > jsonStartIndex) {
                    text.substring(jsonStartIndex, jsonEndIndex + 1)
                } else text
                return json.decodeFromString<MealAnalysisResult>(jsonContent)
            } catch (e: Exception) {
                Log.e("GeminiRepository", "Error analyzing spoken meal with $modelName: ${e.message}")
            }
        }
        return null
    }
}
