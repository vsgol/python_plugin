package org.pffff.length_words_plugin

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import java.io.File
import java.io.IOException
import java.lang.ProcessBuilder

@Service
class ServerManager {
    private var serverProcess: Process? = null
    private val logger = Logger.getInstance(ServerManager::class.java)

    fun startServer() {
        try {
            if (serverProcess == null || !serverProcess!!.isAlive) {
                val scriptPath = "/path/to/text_processor_server.py" // Adjust the path to your Python script
                serverProcess = ProcessBuilder("python", scriptPath)
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
                logger.info("Python server started.")
            }
        } catch (e: IOException) {
            logger.error("Failed to start Python server", e)
        }
    }

    fun stopServer() {
        try {
            serverProcess?.destroy()
            logger.info("Python server stopped.")
        } catch (e: Exception) {
            logger.error("Failed to stop Python server", e)
        }
    }

    fun isServerRunning(): Boolean {
        return serverProcess?.isAlive == true
    }
}
