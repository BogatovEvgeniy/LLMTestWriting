package prompt

import PromptProvider

class SimplePrompt : PromptProvider {
    override fun generatePrompt() = "Provide me a set of unit test for the provided codebase"
}
