package m1n64.openai.webview.chatgpt

import com.intellij.util.messages.Topic

enum class ChatState { LOADING, READY, ERROR }

interface ChatEvents {
    fun onState(state: ChatState)

    companion object {
        val TOPIC: Topic<ChatEvents> =
            Topic.create("ChatGPT WebView Events", ChatEvents::class.java)
    }
}