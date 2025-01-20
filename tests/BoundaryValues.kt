fun calculateDiscount(purchaseAmount: Int): Int {
  require(purchaseAmount >= 0) { "Purchase amount cannot be negative." }
  return when {
    purchaseAmount >= 1000 -> 20 // 20% discount for purchases over 1000
    purchaseAmount >= 500 -> 10 // 10% discount for purchases over 500
    else -> 0 // No discount
  }
}

