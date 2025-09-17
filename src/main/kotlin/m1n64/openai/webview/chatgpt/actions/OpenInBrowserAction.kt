package m1n64.openai.webview.chatgpt.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.Messages
import m1n64.openai.webview.chatgpt.BrowserPanel
import java.awt.Desktop
import java.net.URI

class OpenInBrowserAction(private val panel: BrowserPanel, private val fallbackUrl: String) :
    AnAction("Open in Browser", "Open current page in external browser", AllIcons.General.Web), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        try {
            val url = panel.browser?.cefBrowser?.url ?: fallbackUrl
            Desktop.getDesktop().browse(URI(url))
        } catch (ex: Exception) {
            Messages.showErrorDialog(panel.project, ex.message, "Cannot Open External Browser")
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
}