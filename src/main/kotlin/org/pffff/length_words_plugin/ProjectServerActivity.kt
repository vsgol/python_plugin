package org.pffff.length_words_plugin

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.startup.ProjectActivity
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.outputStream
import kotlin.io.path.pathString

class ProjectServerActivity : ProjectActivity, ProjectManagerListener {

    private val logger = Logger.getInstance(ProjectServerActivity::class.java)
    private var serverProcess: Process? = null
    private var activeProjects = 0
    private var unpackedExecutable: Path? = null

    override suspend fun execute(project: Project) {
        logger.info("Project was opened")
        synchronized(this) {
            activeProjects++
            if (activeProjects == 1) {
                startServer()
            }
        }

        // Add a project listener to detect project closing
        ProjectManager.getInstance().addProjectManagerListener(project, object : ProjectManagerListener {
            override fun projectClosing(closedProject: Project) {
                logger.info("Project was closed")
                synchronized(this@ProjectServerActivity) {
                    activeProjects--
                    if (activeProjects == 0) {
                        stopServer()
                    }
                }
            }
        })
    }


    fun startServer() {
        if (serverProcess == null || !serverProcess!!.isAlive) {
            try {
                // Detect operating system
                val os = System.getProperty("os.name").lowercase()
                val serverExecutable = when {
                    os.contains("win") -> "server/windows/text_processor_server.exe"
                    os.contains("nix") || os.contains("nux") -> "server/linux/text_processor_server"
                    os.contains("mac") -> throw UnsupportedOperationException("macOS executable is not available.")
                    else -> throw UnsupportedOperationException("Unsupported OS: $os")
                }

                // Get the absolute path to the server executable
                val resourceStream = javaClass.getResourceAsStream("/$serverExecutable")
                    ?: throw IOException("Executable not found for OS: $os ($serverExecutable)")

                unpackedExecutable = kotlin.io.path.createTempFile(suffix=if (os.contains("win")) ".exe" else "")
                unpackedExecutable!!.toFile().setExecutable(true)
                resourceStream.use { input ->
                    unpackedExecutable!!.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                // Start the server process
                logger.info("Starting Python server using: $unpackedExecutable")
                serverProcess = ProcessBuilder(unpackedExecutable!!.pathString).start()
                logger.info("Python server started successfully.")
            } catch (e: IOException) {
                logger.error("Failed to start Python server", e)
                stopServer()
            } catch (e: UnsupportedOperationException) {
                logger.error("Unsupported OS detected", e)
                stopServer()
            } catch (e: Exception) {
                logger.error("Unsupported exception", e)
                stopServer()
            }
        } else {
            logger.info("Python server is already running.")
        }
    }

    fun stopServer() {
        try {
            serverProcess?.destroyForcibly()
            unpackedExecutable?.deleteIfExists()
            logger.info("Python server stopped.")
        } catch (e: Exception) {
            logger.error("Failed to stop Python server", e)
        }
    }

    fun isServerRunning(): Boolean {
        return serverProcess?.isAlive == true
    }
}
