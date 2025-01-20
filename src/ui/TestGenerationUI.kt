package ui

import coordinator.MainProcess
import coordinator.TestGenerationConfig
import javax.swing.*
import java.awt.*

class TestGeneratorUI : JFrame("LLM Test Generator") {
    // Main components
    private val startButton = JButton("Generate and Analyze Tests")
    private val resultArea = JTextArea()

    // Weight spinners
    private val basicMetricsWeight = JSpinner(SpinnerNumberModel(0.25, 0.0, 1.0, 0.05))
    private val coverageMetricsWeight = JSpinner(SpinnerNumberModel(0.25, 0.0, 1.0, 0.05))
    private val qualityMetricsWeight = JSpinner(SpinnerNumberModel(0.25, 0.0, 1.0, 0.05))
    private val readabilityMetricsWeight = JSpinner(SpinnerNumberModel(0.25, 0.0, 1.0, 0.05))

    // LLM checkboxes
    private val useGPT = JCheckBox("GPT", false)
    private val useGemini = JCheckBox("Gemini", false)
    private val useCodeLlama = JCheckBox("CodeLlama", false)

    // Prompt configuration
    private val promptArea = JTextArea("Provide me a set of unit test for the provided codebase", 5, 40)

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(1000, 400)
        layout = BorderLayout()
        setupComponents()
        setupActions()
    }

    private fun setupComponents() {
        val mainPanel = JPanel(GridBagLayout())
        val constraints = GridBagConstraints()
        constraints.insets = Insets(5, 5, 5, 5)

        // Metrics Configuration Panel
        val metricsPanel = createMetricsPanel()
        constraints.apply {
            gridx = 0
            gridy = 0
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        }
        mainPanel.add(metricsPanel, constraints)

        // LLM Selection Panel
        val llmPanel = createLLMPanel()
        constraints.apply {
            gridy = 1
            weighty = 0.0
        }
        mainPanel.add(llmPanel, constraints)

        // Prompt Configuration Panel
        val promptPanel = createPromptPanel()
        constraints.apply {
            gridy = 2
            fill = GridBagConstraints.BOTH
            weighty = 0.3
        }
        mainPanel.add(promptPanel, constraints)

        add(mainPanel, BorderLayout.CENTER)

        // Button Panel at the bottom
        val buttonPanel = JPanel()
        buttonPanel.add(startButton)
        add(buttonPanel, BorderLayout.SOUTH)
    }

    private fun createMetricsPanel(): JPanel {
        val panel = JPanel(GridLayout(4, 2, 10, 10))
        panel.border = BorderFactory.createTitledBorder("Metrics Weights Configuration")

        panel.apply {
            add(JLabel("Basic Metrics Weight:"))
            add(basicMetricsWeight)
            add(JLabel("Coverage Metrics Weight:"))
            add(coverageMetricsWeight)
            add(JLabel("Quality Metrics Weight:"))
            add(qualityMetricsWeight)
            add(JLabel("Readability Metrics Weight:"))
            add(readabilityMetricsWeight)
        }

        return panel
    }

    private fun createLLMPanel(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT))
        panel.border = BorderFactory.createTitledBorder("LLM Selection")

        panel.add(useGPT)
        panel.add(useGemini)
        panel.add(useCodeLlama)

        return panel
    }

    private fun createPromptPanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createTitledBorder("Prompt Configuration")

        promptArea.apply {
            lineWrap = true
            wrapStyleWord = true
            font = Font("Monospaced", Font.PLAIN, 12)
        }

        panel.add(JScrollPane(promptArea), BorderLayout.CENTER)
        return panel
    }


    private fun setupActions() {
        startButton.addActionListener {
            startButton.isEnabled = false
            resultArea.text = "Processing...\n"

            Thread {
                try {
                    // Validate and collect configuration
                    val config = collectConfig()

                    // Run the main process
                    val results = MainProcess.run(config)

                    SwingUtilities.invokeLater {
                        resultArea.text = results
                    }
                } catch (e: Exception) {
                    SwingUtilities.invokeLater {
                        resultArea.text += "\nError: ${e.message}\n"
                        e.printStackTrace()
                    }
                } finally {
                    SwingUtilities.invokeLater {
                        startButton.isEnabled = true
                    }
                }
            }.start()
        }
    }

    private fun collectConfig(): TestGenerationConfig {
        // Validate weights sum to 1.0
        val sum = (basicMetricsWeight.value as Double) +
                (coverageMetricsWeight.value as Double) +
                (qualityMetricsWeight.value as Double) +
                (readabilityMetricsWeight.value as Double)

        if (Math.abs(sum - 1.0) > 0.001) {
            throw IllegalArgumentException("Weights must sum to 1.0 (current sum: $sum)")
        }

        // Validate at least one LLM is selected
        if (!useGPT.isSelected && !useGemini.isSelected && !useCodeLlama.isSelected) {
            throw IllegalArgumentException("Please select at least one LLM")
        }

        return TestGenerationConfig(
            basicMetricsWeight = basicMetricsWeight.value as Double,
            coverageMetricsWeight = coverageMetricsWeight.value as Double,
            qualityMetricsWeight = qualityMetricsWeight.value as Double,
            readabilityMetricsWeight = readabilityMetricsWeight.value as Double,
            useGPT = useGPT.isSelected,
            useGemini = useGemini.isSelected,
            useCodeLlama = useCodeLlama.isSelected,
            prompt = promptArea.text.trim()
        )
    }
}