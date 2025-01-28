import ui.TestGeneratorUI
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        TestGeneratorUI().apply {
            isVisible = true

        }
    }
}