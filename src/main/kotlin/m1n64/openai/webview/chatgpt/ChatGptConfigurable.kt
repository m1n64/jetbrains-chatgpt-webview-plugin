package m1n64.openai.webview.chatgpt

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import javax.swing.*

class ChatGptConfigurable: SearchableConfigurable {
    private val urlField = JBTextField()
    private val hotkeyField = JBTextField()
    private var panel: JPanel? = null

    override fun getId(): String = "m1n64.openai.webview.chatgpt.settings"
    override fun getDisplayName(): String = "ChatGPT Web View"

    override fun createComponent(): JComponent {
        val settings = ChatGptSettings.instance().state
        urlField.text = settings.url
        hotkeyField.text = settings.hotkey

        val form = JPanel()
        form.layout = BoxLayout(form, BoxLayout.Y_AXIS)
        form.border = JBUI.Borders.empty(10)

        form.add(JLabel("Chat URL:"))
        form.add(urlField)
        form.add(Box.createVerticalStrut(8))

        form.add(JLabel("Custom Hotkey (e.g. ctrl alt G, shift meta G). Leave empty to use Keymap:"))
        form.add(hotkeyField)

        panel = form
        return form
    }

    override fun isModified(): Boolean {
        val s = ChatGptSettings.instance().state
        return s.url != urlField.text.trim() || s.hotkey != hotkeyField.text.trim()
    }

    override fun apply() {
        val s = ChatGptSettings.instance().state
        val newUrl = urlField.text.trim().ifEmpty { "https://chatgpt.com/" }
        if (s.url != newUrl) {
            s.url = newUrl
            s.lastUrl = ""
        }

        s.hotkey = hotkeyField.text.trim()

        ShowChatGptToolWindowAction.applyCustomShortcut(s.hotkey)
    }

    override fun reset() {
        val s = ChatGptSettings.instance().state
        urlField.text = s.url
        hotkeyField.text = s.hotkey
    }
}