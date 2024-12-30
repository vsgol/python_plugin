package org.pffff.length_words_plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI

class LenghtOfWordsAction : AnAction("Length Of The Words") {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        val selectedText = editor?.selectionModel?.selectedText

        if (selectedText.isNullOrBlank()) {
            Messages.showErrorDialog("No text selected. Please select some text.", "Error")
            return
        }

        // Send the text to the server
        try {
            val url = URI("http://127.0.0.1:5000/process-text").toURL()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            // Encode the text into a JSON payload
            val payload = JSONObject().put("text", selectedText).toString()
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(payload)
            writer.flush()
            writer.close()

            // TODO add waiting time for response

            // Read the response
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val processedText = JSONObject(response).getString("processed_text") // Extract the "processed_text" field
                Messages.showMessageDialog(processedText, "Processed Text", Messages.getInformationIcon())
            } else {
                Messages.showErrorDialog("Server returned response code $responseCode", "Error")
            }

        } catch (ex: Exception) {
            Messages.showErrorDialog("Failed to connect to server: ${ex.message}", "Error")
        }
    }
}
