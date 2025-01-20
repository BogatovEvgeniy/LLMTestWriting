package ui

import coordinator.MainProcess
import coordinator.TestGenerationConfig
import javax.swing.*
import java.awt.*
import javax.swing.event.ChangeListener

class TestGeneratorUI : JFrame("LLM Test Generator") {
    // Main components
    private val startButton = JButton("Generate and Analyze Tests")
    private val resultArea = JTextArea()

    // Weight spinners
    private val basicMetricsWeight = JSpinner(SpinnerNumberModel(0.2, 0.0, 1.0, 0.05))
    private val coverageMetricsWeight = JSpinner(SpinnerNumberModel(0.2, 0.0, 1.0, 0.05))
    private val qualityMetricsWeight = JSpinner(SpinnerNumberModel(0.2, 0.0, 1.0, 0.05))
    private val readabilityMetricsWeight = JSpinner(SpinnerNumberModel(0.2, 0.0, 1.0, 0.05))
    private val timingMetricsWeight = JSpinner(SpinnerNumberModel(0.2, 0.0, 1.0, 0.05))

    // LLM checkboxes
    private val useGPT = JCheckBox("GPT", false)
    private val useGemini = JCheckBox("Gemini", false)
    private val useCodeLlama = JCheckBox("CodeLlama", false)

    // Prompt configuration
    private val promptArea = JTextArea("Provide me a set of unit test for the provided codebase", 5, 40)

    // Reference to metrics panel
    private lateinit var metricsPanel: JPanel

        init {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(1000, 600)
        layout = BorderLayout()
        setupComponents()
        setupActions()
        addValidation()
    }

    private fun setupComponents() {
        val mainPanel = JPanel(GridBagLayout())
        val constraints = GridBagConstraints()
        constraints.insets = Insets(5, 5, 5, 5)

        // Metrics Configuration Panel
        metricsPanel = createMetricsPanel()
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
        val panel = JPanel(GridBagLayout())
        panel.border = BorderFactory.createTitledBorder("Metrics Weights Configuration")
        val constraints = GridBagConstraints()

        // Configure basic constraints
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.insets = Insets(5, 5, 5, 5)  // Add some padding
        constraints.gridx = 0
        constraints.weightx = 1.0

        // Add weight components vertically
        var row = 0

        // Basic Metrics
        constraints.gridy = row++
        panel.add(JLabel("Basic Metric Weight:"), constraints)
        constraints.gridy = row++
        panel.add(basicMetricsWeight, constraints)

        // Coverage Metrics
        constraints.gridy = row++
        panel.add(JLabel("Coverage Metric Weight:"), constraints)
        constraints.gridy = row++
        panel.add(coverageMetricsWeight, constraints)

        // Quality Metrics
        constraints.gridy = row++
        panel.add(JLabel("Quality Metric Weight:"), constraints)
        constraints.gridy = row++
        panel.add(qualityMetricsWeight, constraints)

        // Readability Metrics
        constraints.gridy = row++
        panel.add(JLabel("Readability Metric Weight:"), constraints)
        constraints.gridy = row++
        panel.add(readabilityMetricsWeight, constraints)

        // Timing Metrics
        constraints.gridy = row++
        panel.add(JLabel("Timing Metric Weight:"), constraints)
        constraints.gridy = row++
        panel.add(timingMetricsWeight, constraints)

        // Normalize Button
        constraints.gridy = row++
        val normalizeButton = JButton("Normalize Weights")
        normalizeButton.addActionListener { normalizeWeights() }
        panel.add(normalizeButton, constraints)

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
                (readabilityMetricsWeight.value as Double)+
                (timingMetricsWeight.value as Double)

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
            timingMetricsWeight = timingMetricsWeight.value as Double,
            useGPT = useGPT.isSelected,
            useGemini = useGemini.isSelected,
            useCodeLlama = useCodeLlama.isSelected,
            prompt = promptArea.text.trim()
        )
    }

    private fun addValidation() {
        // Status label for weight validation
        val statusLabel = JLabel()
        statusLabel.foreground = Color.RED

        // Add status label to the metrics panel
        val metricsPanel = getMetricsPanel()
        metricsPanel.add(statusLabel)

        // Create change listener for spinners
        val changeListener = ChangeListener {
            try {
                validateWeights()
                statusLabel.text = ""
                startButton.isEnabled = true
            } catch (e: IllegalArgumentException) {
                statusLabel.text = e.message
                startButton.isEnabled = false
            }
        }

        basicMetricsWeight.addChangeListener(changeListener)
        coverageMetricsWeight.addChangeListener(changeListener)
        qualityMetricsWeight.addChangeListener(changeListener)
        readabilityMetricsWeight.addChangeListener(changeListener)
        timingMetricsWeight.addChangeListener(changeListener)
        val llmListener = { _: Any ->
            startButton.isEnabled = useGPT.isSelected || useGemini.isSelected || useCodeLlama.isSelected
        }

        useGPT.addActionListener(llmListener)
        useGemini.addActionListener(llmListener)
        useCodeLlama.addActionListener(llmListener)
    }

    private fun validateWeights() {
        val sum = (basicMetricsWeight.value as Double) +
                (coverageMetricsWeight.value as Double) +
                (qualityMetricsWeight.value as Double) +
                (readabilityMetricsWeight.value as Double) +
                (timingMetricsWeight.value as Double)

        if (Math.abs(sum - 1.0) > 0.001) {
            throw IllegalArgumentException("Weights must sum to 1.0 (current: %.2f)".format(sum))
        }
    }

    private fun getMetricsPanel(): JPanel {
        if (!::metricsPanel.isInitialized) {
            throw IllegalStateException("Metrics panel not initialized yet")
        }
        return metricsPanel
    }

    fun normalizeWeights() {
        val sum = (basicMetricsWeight.value as Double) +
                (coverageMetricsWeight.value as Double) +
                (qualityMetricsWeight.value as Double) +
                (readabilityMetricsWeight.value as Double) +
                (timingMetricsWeight.value as Double)

        if (sum > 0) {
            basicMetricsWeight.value = (basicMetricsWeight.value as Double) / sum
            coverageMetricsWeight.value = (coverageMetricsWeight.value as Double) / sum
            qualityMetricsWeight.value = (qualityMetricsWeight.value as Double) / sum
            readabilityMetricsWeight.value = (readabilityMetricsWeight.value as Double) / sum
            timingMetricsWeight.value = (timingMetricsWeight.value as Double) / sum
        }
    }
}