package m1n64.openai.webview.chatgpt.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import m1n64.openai.webview.chatgpt.BrowserPanel

class ForwardAction(private val panel: BrowserPanel) :
    AnAction("Forward", "Go forward in browser", AllIcons.Actions.Forward), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        panel.browser?.cefBrowser?.takeIf { it.canGoForward() }?.goForward()
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = panel.browser?.cefBrowser?.canGoForward() == true
    }
}