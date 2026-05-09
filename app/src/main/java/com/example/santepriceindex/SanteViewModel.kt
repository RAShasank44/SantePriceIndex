package com.example.santepriceindex

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SanteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _items = MutableStateFlow<List<MarketItem>>(emptyList())
    val items = _items.asStateFlow()

    init {
        fetchPrices()
    }

    private fun fetchPrices() {
        db.collection("mandi_prices").addSnapshotListener { snapshot, _ ->
            val vegetableList = snapshot?.toObjects(MarketItem::class.java) ?: emptyList()
            _items.value = vegetableList
        }
    }
}