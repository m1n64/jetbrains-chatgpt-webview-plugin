package m1n64.openai.webview.chatgpt

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class ApplyShortcutOnStartup: StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        val s = ChatGptSettings.instance().state
        ShowChatGptToolWindowAction.applyCustomShortcut(s.hotkey)
    }
}