package m1n64.openai.webview.chatgpt

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class BrowserToolWindowFactory: ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = BrowserPanel(project)
        project.getService(ChatGptController::class.java).attach(panel)

        val content = ContentFactory.getInstance().createContent(panel, "", false).also {
            it.setDisposer { project.getService(ChatGptController::class.java).detach(panel) }
        }
        toolWindow.contentManager.addContent(content)
    }
}