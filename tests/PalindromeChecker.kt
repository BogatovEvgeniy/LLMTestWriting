class PalindromeChecker {
    fun isPalindrome(str: String?): Boolean {
        if (str.isNullOrEmpty()) {
            return true
        }

        val normalizedStr = str.lowercase()
        var left = 0
        var right = normalizedStr.length - 1

        while (left < right) {
            if (normalizedStr[left] != normalizedStr[right]) {
                return false
            }
            left++
            right--
        }
        return true
    }
}
