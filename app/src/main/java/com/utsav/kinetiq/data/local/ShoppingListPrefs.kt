package com.utsav.kinetiq.data.local

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ShoppingListItem(
    val id: String,
    val text: String,
    val recipeTitle: String = "",
    val isChecked: Boolean = false
)

class ShoppingListPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("kinetiq_shopping_list_prefs", Context.MODE_PRIVATE)

    private val _shoppingList = MutableStateFlow<List<ShoppingListItem>>(emptyList())
    val shoppingList: StateFlow<List<ShoppingListItem>> = _shoppingList.asStateFlow()

    init {
        loadShoppingList()
    }

    private fun loadShoppingList() {
        val json = prefs.getString("shopping_items_json", "[]") ?: "[]"
        try {
            val items = Json.decodeFromString<List<ShoppingListItem>>(json)
            _shoppingList.value = items
        } catch (e: Exception) {
            _shoppingList.value = emptyList()
        }
    }

    private fun saveShoppingList(items: List<ShoppingListItem>) {
        _shoppingList.value = items
        val json = Json.encodeToString(items)
        prefs.edit().putString("shopping_items_json", json).apply()
    }

    fun addItem(text: String, recipeTitle: String = "") {
        val current = _shoppingList.value.toMutableList()
        val newItem = ShoppingListItem(
            id = System.currentTimeMillis().toString() + "_" + (0..1000).random(),
            text = text,
            recipeTitle = recipeTitle,
            isChecked = false
        )
        current.add(newItem)
        saveShoppingList(current)
    }

    fun addItems(texts: List<String>, recipeTitle: String = "") {
        val current = _shoppingList.value.toMutableList()
        texts.forEach { text ->
            if (current.none { it.text.equals(text, ignoreCase = true) }) {
                current.add(
                    ShoppingListItem(
                        id = System.currentTimeMillis().toString() + "_" + (0..1000).random(),
                        text = text,
                        recipeTitle = recipeTitle,
                        isChecked = false
                    )
                )
            }
        }
        saveShoppingList(current)
    }

    fun toggleItem(id: String) {
        val current = _shoppingList.value.map {
            if (it.id == id) it.copy(isChecked = !it.isChecked) else it
        }
        saveShoppingList(current)
    }

    fun removeItem(id: String) {
        val current = _shoppingList.value.filter { it.id != id }
        saveShoppingList(current)
    }

    fun clearChecked() {
        val current = _shoppingList.value.filter { !it.isChecked }
        saveShoppingList(current)
    }
}
