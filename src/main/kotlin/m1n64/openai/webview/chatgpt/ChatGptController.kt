package m1n64.openai.webview.chatgpt

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class ChatGptController(private val project: Project) {
    @Volatile private var panel: BrowserPanel? = null

    fun attach(panel: BrowserPanel) { this.panel = panel }
    fun detach(panel: BrowserPanel) { if (this.panel === panel) this.panel = null }

    fun showAndInsert(text: String) {
        Ui.showToolWindow(project)
        panel?.insertTextIntoChat(text) ?: Ui.info(project, "ChatGPT", "Waiting for ChatGPT tool window...")
    }
}