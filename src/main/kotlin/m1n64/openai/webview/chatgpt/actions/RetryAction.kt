package m1n64.openai.webview.chatgpt.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import m1n64.openai.webview.chatgpt.BrowserPanel

class RetryAction(private val panel: BrowserPanel) :
    AnAction("Retry", "Recreate browser instance", AllIcons.Actions.Restart), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        panel.tryCreateBrowser()
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
}