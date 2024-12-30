package org.pffff.length_words_plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.application.ApplicationManager
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class HelloWorldAction : AnAction("Send Text to Python Server") {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        val selectedText = editor?.selectionModel?.selectedText

        if (selectedText.isNullOrBlank()) {
            Messages.showErrorDialog("No text selected. Please select some text.", "Error")
            return
        }

        // Get the ServerManager instance
        val serverManager = ApplicationManager.getApplication().getService(ServerManager::class.java)

        // Check if the server is running, and start it if not
        if (!serverManager.isServerRunning()) {
            Messages.showInfoMessage("Python server is not running. Starting the server...", "Info")
            serverManager.startServer()

            // Wait a short moment to allow the server to boot up
            Thread.sleep(2000) // 2 seconds (adjust if needed)
        }

        // Confirm the server is running
        if (!serverManager.isServerRunning()) {
            Messages.showErrorDialog("Failed to start the Python server. Please check the logs.", "Error")
            return
        }

        // Send the text to the server
        try {
            val url = URL("http://127.0.0.1:5000/process-text")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            // JSON payload
            val payload = """{"text": "$selectedText"}"""
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(payload)
            writer.flush()
            writer.close()

            // Read the response
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                Messages.showMessageDialog(response, "Python Server Response", Messages.getInformationIcon())
            } else {
                Messages.showErrorDialog("Server returned response code $responseCode", "Error")
            }

        } catch (ex: Exception) {
            Messages.showErrorDialog("Failed to connect to server: ${ex.message}", "Error")
        }
    }
}
