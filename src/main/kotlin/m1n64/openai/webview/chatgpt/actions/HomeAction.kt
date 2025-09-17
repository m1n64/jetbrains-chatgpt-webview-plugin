package m1n64.openai.webview.chatgpt.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import m1n64.openai.webview.chatgpt.BrowserPanel

class HomeAction(private val panel: BrowserPanel, private val homeUrl: String) :
    AnAction("Home", "Go to home page", AllIcons.Nodes.HomeFolder), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        panel.browser?.loadURL(homeUrl)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
}