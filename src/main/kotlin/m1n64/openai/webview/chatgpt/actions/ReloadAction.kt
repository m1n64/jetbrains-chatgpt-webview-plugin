package m1n64.openai.webview.chatgpt.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import m1n64.openai.webview.chatgpt.BrowserPanel

class ReloadAction(private val panel: BrowserPanel) :
    AnAction("Reload", "Reload page", AllIcons.Actions.Refresh), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        panel.browser?.cefBrowser?.reload()
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
}