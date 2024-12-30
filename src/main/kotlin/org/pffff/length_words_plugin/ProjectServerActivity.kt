package org.pffff.length_words_plugin

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.startup.ProjectActivity

@Service(Service.Level.PROJECT)
class ProjectServerActivity : ProjectActivity, ProjectManagerListener {

    private val logger = Logger.getInstance(ProjectServerActivity::class.java)
    private var serverProcess: Process? = null
    private var activeProjects = 0

    override suspend fun execute(project: Project) {
        synchronized(this) {
            activeProjects++
            if (activeProjects == 1) {
                startServer()
            }
        }

        // Add a project listener to detect project closing
        ProjectManager.getInstance().addProjectManagerListener(project, object : ProjectManagerListener {
            override fun projectClosing(closedProject: Project) {
                synchronized(this@ProjectServerActivity) {
                    activeProjects--
                    if (activeProjects == 0) {
                        stopServer()
                    }
                }
            }
        })
    }

    private fun startServer() {
        if (serverProcess == null || !serverProcess!!.isAlive) {
            try {
                val scriptPath = "/path/to/text_processor_server.py" // Update to your Python script's path
                serverProcess = ProcessBuilder("python", scriptPath)
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
                logger.info("Python server started.")
            } catch (e: Exception) {
                logger.error("Failed to start Python server", e)
            }
        } else {
            logger.info("Python server is already running.")
        }
    }

    private fun stopServer() {
        if (serverProcess != null && serverProcess!!.isAlive) {
            try {
                serverProcess!!.destroy()
                logger.info("Python server stopped.")
            } catch (e: Exception) {
                logger.error("Failed to stop Python server", e)
            } finally {
                serverProcess = null
            }
        }
    }
}
