package m1n64.openai.webview.chatgpt.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import m1n64.openai.webview.chatgpt.BrowserPanel

class BackAction(private val panel: BrowserPanel) :
    AnAction("Back", "Go back in browser", AllIcons.Actions.Back), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        panel.browser?.cefBrowser?.takeIf { it.canGoBack() }?.goBack()
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = panel.browser?.cefBrowser?.canGoBack() == true
    }
}