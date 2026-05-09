package com.example.santepriceindex

object PricingEngine {
    /**
     * The formula:
     * 1. Add transport to the base cost.
     * 2. Adjust for waste (if 10% is wasted, you only have 90% left to sell).
     * 3. Add the desired profit.
     */
    fun calculateRRP(
        mandiPrice: Double,
        transportPerKg: Double,
        wastePercent: Double, // e.g., 0.1 for 10%
        desiredProfit: Double
    ): Double {
        val totalCost = mandiPrice + transportPerKg
        val priceAfterWaste = totalCost / (1 - wastePercent)
        return priceAfterWaste + desiredProfit
    }
}