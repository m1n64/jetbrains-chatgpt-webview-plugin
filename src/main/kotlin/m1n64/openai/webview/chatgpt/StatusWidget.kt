package m1n64.openai.webview.chatgpt

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.util.Consumer
import com.intellij.util.messages.MessageBusConnection
import java.awt.event.MouseEvent
import javax.swing.SwingConstants

class ChatStatusWidgetFactory : StatusBarWidgetFactory {
    override fun getId(): String = "ChatGPTStatusWidget"
    override fun getDisplayName(): String = "ChatGPT Status"
    override fun isAvailable(project: Project): Boolean = true
    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
    override fun createWidget(project: Project): StatusBarWidget = ChatStatusWidget(project)
    override fun disposeWidget(widget: StatusBarWidget) {}
}

class ChatStatusWidget(private val project: Project) :
    StatusBarWidget, StatusBarWidget.TextPresentation {

    private var statusBar: StatusBar? = null
    private var text: String = "ChatGPT: idle"
    private var conn: MessageBusConnection? = null

    override fun ID(): String = "ChatGPTStatusWidget"

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
        conn = project.messageBus.connect().apply {
            subscribe(ChatEvents.TOPIC, object : ChatEvents {
                override fun onState(state: ChatState) {
                    text = when (state) {
                        ChatState.LOADING -> "ChatGPT: loading…"
                        ChatState.READY   -> "ChatGPT: online"
                        ChatState.ERROR   -> "ChatGPT: error"
                    }
                    statusBar.updateWidget(ID())
                }
            })
        }
    }

    override fun dispose() {
        conn?.disconnect()
        conn = null
    }

    // TextPresentation
    override fun getText(): String = text
    override fun getTooltipText(): String = "ChatGPT Web View status"
    override fun getClickConsumer(): Consumer<MouseEvent>? =
        Consumer { Ui.showToolWindow(project) }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun getAlignment(): Float = SwingConstants.CENTER.toFloat()
}
