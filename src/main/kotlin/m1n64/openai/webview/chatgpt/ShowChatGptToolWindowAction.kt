package m1n64.openai.webview.chatgpt

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.wm.ToolWindowManager
import javax.swing.KeyStroke
import com.intellij.openapi.actionSystem.ActionUpdateThread

class ShowChatGptToolWindowAction: AnAction("Show ChatGPT ToolWindow"), DumbAware {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val tw = ToolWindowManager.getInstance(project).getToolWindow("ChatGPT")
        if (tw != null) {
            tw.activate(null, true, true)
            tw.show()
        }
    }

    companion object {
        private const val ACTION_ID = "m1n64.openai.webview.chatgpt.Show"

        fun applyCustomShortcut(hotkeyText: String?) {
            val keymapManager = KeymapManager.getInstance() ?: return
            val keymap = keymapManager.activeKeymap

            // Сначала уберём предыдущие кастомные биндинги для ACTION_ID
            keymap.getShortcuts(ACTION_ID).forEach { sc ->
                if (sc is KeyboardShortcut) {
                    keymap.removeShortcut(ACTION_ID, sc)
                }
            }

            val text = hotkeyText?.trim().orEmpty()
            if (text.isEmpty()) return

            val ks = parseKeyStroke(text) ?: return
            val shortcut = KeyboardShortcut(ks, null)
            keymap.addShortcut(ACTION_ID, shortcut)
        }

        private fun parseKeyStroke(s: String): KeyStroke? {
            return try {
                KeyStroke.getKeyStroke(s.replace("\\s+".toRegex(), " ").trim().uppercase())
                    ?: KeyStroke.getKeyStroke(s)
            } catch (_: Exception) {
                null
            }
        }
    }

}