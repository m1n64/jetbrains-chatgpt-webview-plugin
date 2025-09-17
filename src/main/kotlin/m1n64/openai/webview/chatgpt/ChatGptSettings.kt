package m1n64.openai.webview.chatgpt

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

data class ChatGptSettingsState (
    var url: String = "https://chatgpt.com",
    var hotkey: String = "",
    var lastUrl: String = ""
)

@State(name = "ChatGptSettingsState", storages = [Storage("chatgpt-webview.xml")])
class ChatGptSettings: PersistentStateComponent<ChatGptSettingsState> {
    private var state = ChatGptSettingsState()

    override fun getState(): ChatGptSettingsState = state
    override fun loadState(state: ChatGptSettingsState) { this.state = state }

    companion object {
        fun instance(): ChatGptSettings = service()
    }
}