package m1n64.openai.webview.chatgpt

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Desktop
import java.net.URI
import javax.swing.*
import com.intellij.openapi.actionSystem.*
import m1n64.openai.webview.chatgpt.actions.*
import javax.swing.JComponent

class BrowserPanel(val project: Project) : JPanel(BorderLayout()) {
    var browser: JBCefBrowser? = null

    private val startUrl: String
        get() = ChatGptSettings.instance().state.url.ifBlank { "https://chatgpt.com/" }
    private val initialUrl: String get() = ChatGptSettings.instance().state.lastUrl.takeIf { it.isNotBlank() } ?: startUrl

    private var isReady = false
    private var pendingInsert: String? = null

    init {
        layout = BorderLayout()
        tryCreateBrowser()
    }

    fun tryCreateBrowser() {
        removeAll()
        isReady = false

        if (!JBCefApp.isSupported()) {
            showFallback("JCEF is not supported on this platform.", showRetry = false)
            return
        }

        try {
            publish(ChatState.LOADING)

            val b = JBCefBrowser(initialUrl)
            browser = b

            b.jbCefClient.addLoadHandler(object : org.cef.handler.CefLoadHandlerAdapter() {
                override fun onLoadingStateChange(
                    browser: org.cef.browser.CefBrowser?,
                    isLoading: Boolean,
                    canGoBack: Boolean,
                    canGoForward: Boolean
                ) {
                    isReady = !isLoading
                    publish(if (isLoading) ChatState.LOADING else ChatState.READY)
                    if (isReady) {
                        pendingInsert?.let {
                            executeInsertJs(it, retries = 5, delayMs = 300)
                            pendingInsert = null
                        }
                    }
                }

                override fun onLoadError(
                    browser: org.cef.browser.CefBrowser?,
                    frame: org.cef.browser.CefFrame?,
                    errorCode: org.cef.handler.CefLoadHandler.ErrorCode?,
                    errorText: String?,
                    failedUrl: String?
                ) {
                    showFallback("Load error: $errorCode $errorText\n$failedUrl")
                }
            }, b.cefBrowser)

            b.jbCefClient.addDisplayHandler(object : org.cef.handler.CefDisplayHandlerAdapter() {
                override fun onAddressChange(
                    browser: org.cef.browser.CefBrowser?,
                    frame: org.cef.browser.CefFrame?,
                    url: String?
                ) {
                    if (frame?.isMain == true && !url.isNullOrBlank() && url != "about:blank") {
                        ChatGptSettings.instance().state.lastUrl = url
                    }
                }
            }, b.cefBrowser)

            add(buildToolbar(), BorderLayout.NORTH)
            add(b.component, BorderLayout.CENTER)
            revalidate()
            repaint()
        } catch (t: Throwable) {
            showFallback("Browser init failed: ${t.message ?: t::class.java.simpleName}")
        }
    }

    private fun publish(state: ChatState) {
        project.messageBus.syncPublisher(ChatEvents.TOPIC).onState(state)
    }

    private fun showFallback(message: String, showRetry: Boolean = true) {
        publish(ChatState.ERROR)
        removeAll()
        val fallback = NonOpaquePanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(16)
            val box = Box.createVerticalBox()
            box.add(JLabel(message))
            if (showRetry) {
                box.add(Box.createVerticalStrut(8))
                box.add(JButton("Retry").apply { addActionListener { tryCreateBrowser() } })
            }
            add(box, BorderLayout.CENTER)
        }
        add(fallback, BorderLayout.CENTER)
        revalidate()
        repaint()
    }


    private fun buildToolbar(): JComponent {
        val group = DefaultActionGroup().apply {
            add(BackAction(this@BrowserPanel))
            add(ForwardAction(this@BrowserPanel))
            add(ReloadAction(this@BrowserPanel))
            add(HomeAction(this@BrowserPanel, startUrl))
            addSeparator()
            add(OpenInBrowserAction(this@BrowserPanel, initialUrl))
            add(RetryAction(this@BrowserPanel))
        }
        val toolbar = ActionManager.getInstance()
            .createActionToolbar("ChatGPTWebViewToolbar", group, true)
        toolbar.targetComponent = this
        return toolbar.component

        /*return JToolBar().apply {
            isFloatable = false
            border = JBUI.Borders.empty(4, 6)

            add(JButton("Back").apply {
                addActionListener { browser?.cefBrowser?.takeIf { it.canGoBack() }?.goBack() }
            })
            add(JButton("Forward").apply {
                addActionListener { browser?.cefBrowser?.takeIf { it.canGoForward() }?.goForward() }
            })
            add(JButton("Reload").apply {
                addActionListener { browser?.cefBrowser?.reload() }
            })
            add(JButton("Home").apply {
                addActionListener { browser?.loadURL(startUrl) }
            })
            add(Box.createHorizontalStrut(12))
            add(JButton("Open in Browser").apply {
                addActionListener {
                    try {
                        val url = browser?.cefBrowser?.url ?: startUrl
                        Desktop.getDesktop().browse(URI(url))
                    } catch (e: Exception) {
                        Messages.showErrorDialog(project, e.message, "Cannot Open External Browser")
                    }
                }
            })
        }*/
    }

    fun insertTextIntoChat(text: String) {
        val b = browser ?: return
        if (!isReady) {
            pendingInsert = text
            if (b.cefBrowser.url.isNullOrBlank()) b.loadURL(startUrl)
            return
        }
        executeInsertJs(text, retries = 5, delayMs = 300)
    }

    /**
     * WIP
     *
     * @deprecated
     */
    private fun executeInsertJs(text: String, retries: Int, delayMs: Int) {
        val b = browser ?: return
        val safe = text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")

        val js = """
            (function doInsert(attempt, maxAttempts, delayMs) {
              function sleep(ms){ return new Promise(r=>setTimeout(r, ms)); }
              function fireInput(el){
                try {
                  const evt = new InputEvent('input', {bubbles:true, cancelable:true});
                  el.dispatchEvent(evt);
                } catch(e){
                  const evt = document.createEvent('Event');
                  evt.initEvent('input', true, true);
                  el.dispatchEvent(evt);
                }
              }
              function pickField(){
                // try textarea first
                let el = document.querySelector('textarea[data-testid="prompt-textarea"]')
                      || document.querySelector('textarea[aria-label], textarea[role="textbox"]')
                      || document.querySelector('form textarea');
                if (el) return el;
                // then contenteditable
                el = document.querySelector('[contenteditable="true"][role="textbox"]')
                  || document.querySelector('div[contenteditable="true"]');
                return el || null;
              }

              const target = pickField();
              if (!target) {
                if (attempt >= maxAttempts) return "NO_INPUT";
                return sleep(delayMs).then(()=>doInsert(attempt+1, maxAttempts, delayMs));
              }

              const t = "$safe";
              try {
                target.scrollIntoView({block:'nearest'});
                target.focus();

                if (target.tagName && target.tagName.toLowerCase() === 'textarea') {
                  const start = target.selectionStart ?? target.value.length;
                  const end = target.selectionEnd ?? target.value.length;
                  if (typeof target.setRangeText === 'function') {
                    target.setRangeText(t, start, end, 'end');
                  } else {
                    const v = target.value;
                    target.value = v.slice(0, start) + t + v.slice(end);
                    if (target.setSelectionRange) {
                      const pos = start + t.length;
                      target.setSelectionRange(pos, pos);
                    }
                  }
                  fireInput(target);
                  return "OK_TEXTAREA";
                } else if (target.isContentEditable) {
                  try {
                    document.execCommand('insertText', false, t);
                  } catch(e) {
                    target.textContent = (target.textContent || "") + t;
                    fireInput(target);
                  }
                  return "OK_CONTENTEDITABLE";
                } else {
                  return "UNKNOWN_EL";
                }
              } catch(e) {
                if (attempt >= maxAttempts) return "EXCEPTION:" + (e && e.message ? e.message : e);
                return sleep(delayMs).then(()=>doInsert(attempt+1, maxAttempts, delayMs));
              }
            })(0, ${retries}, ${delayMs});
        """.trimIndent()

        b.cefBrowser.executeJavaScript(js, b.cefBrowser.url, 0)
    }
}
