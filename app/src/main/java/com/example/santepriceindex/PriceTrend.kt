package com.example.santepriceindex

data class PricePoint(
    val date: String,
    val price: Double
)

data class PriceTrend(
    val vegetableName: String,
    val history: List<PricePoint>
)