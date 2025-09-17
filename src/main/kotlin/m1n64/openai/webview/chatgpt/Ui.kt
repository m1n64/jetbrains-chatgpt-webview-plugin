package m1n64.openai.webview.chatgpt

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager

object Ui {
    fun showToolWindow(project: Project) {
        ToolWindowManager.getInstance(project).getToolWindow("ChatGPT")?.apply {
            activate(null, true, true); show()
        }
    }
    fun info(project: Project, title: String, msg: String) = Messages.showInfoMessage(project, msg, title)
}