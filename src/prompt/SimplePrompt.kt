package prompt

import PromptProvider

class SimplePrompt(private val customPrompt: String? = null) : PromptProvider {
    override fun generatePrompt() = customPrompt ?:
    "Provide me a set of unit test for the provided codebase"
}