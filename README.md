# ChatGPT Webview plugin for JetBrains IDEs

<!-- Plugin description -->
ChatGPT Webview plugin for JetBrains IDEs. Open a web browser to chat with ChatGPT without leaving your IDE, and without API keys.
Plugin works by default Jetbrains CEF, and plugin don't steel your credentials.

**We don't steel your credentials, store your personal data in the plugin.**

## Features
- Open a web browser to chat with ChatGPT without leaving your IDE, and without API keys.

<!-- Plugin description end -->

## Compilation
```bash
./gradlew --stop
```
```bash
./gradlew clean buildPlugin
```
Build `.zip` file will be located in `build/distributions`

## Installation
Download `.zip` file from releases and install it manually using
<kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>, or you can build file manually and install it.

## Changelog
**0.1.0 - Initial and final release**

## Note
This plugin open a ChatGPT webview in your IDE, ChatGPT owner's is OpenAI company.

_Code is so "dirty", because Kotlin and Java is not my main languages, and this plugin I developed for fun and for my friend, who want to "ChatGPT without API key in PHPStorm". Say - doing._