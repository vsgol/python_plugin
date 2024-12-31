# Python Script Runner Plugin for IntelliJ IDEA and PyCharm

This plugin allows you to seamlessly execute Python scripts directly within **IntelliJ IDEA** or **PyCharm** using pyinstaller

---

## Supported Platforms
- **Windows**
- **Linux**

---

## Instalation

This project is written in **Kotlin** and uses **Gradle** as its build system. Follow the steps below to set up and run the plugin:

### Prerequisites

**IntelliJ IDEA 2024.1.7** 
2. **JDK 17 or higher** installed and configured.

### Build the Project

Use the Gradle to build the project:

```bash
./gradlew build
```

Plagin will be in [build/distributions](build/distributions)

---

## Problem Statement

Where do we want to run a Python script? Three approaches were considered:
1. **Run a Separate Python Process**
   - Each task spawns a new Python process to execute.
2. **Thread-Based Python Server**
   - A persistent thread-server runs in the background, ready to execute tasks upon request.
3. **Remote Thread Server**
   - Similar to option 2, but hosted on **JetBrains servers**.

Among these, the **2nd option** was chosen:
- This approach introduces a small memory load but offers significantly faster task execution for each request then first request
- The 3rd option wasn't chosen due to potential network dependency issues and latency.

---

Given the decision to run Python scripts on the user's machine using a thread-based server, the next descision: **how do we provide the Python interpreter?**

### Options Considered:

1. **JVM-Based Python Implementation**
   - Examples: Jython, GraalVM.
   - **Cons**
     - Limited syntax support.
     - Incompatibility with key libraries like `numpy`.
     - Jython lacks Python 3 support entirely.

2. **PyInstaller (Chosen Solution)**
   - Package the Python interpreter, required packages, and scripts into a single executable file.
   - **Pros**
     - Full control over the interpreter and dependencies.
     - Fast set up speed.
   - **Cons**:
     - Separate builds required for each OS.
     - Larger plugin file sizes.

3. **Docker**
   - Containerize the Python environment for consistent execution.
   - **Pros**
     - Full control over the interpreter and dependencies.
     - Reliable.
   - **Cons**
     - Slower startup times.
     - Requires online access.

---

## Why I Chose PyInstaller

I found **PyInstaller** to be an interesting and unique solution. While file size increases with the number of dependencies, the **Fast set up speed** outweighs this downside. Compared to Docker, PyInstaller offers a smoother offline experience and better performance for the user.

--- 

### TODO
- This project currently supports **Windows** and **Linux** systems only.
- MacOS support may be considered in the future.

---

## Building the Python Executable

The script [text_processor_server.py](src/main/resources/server/), included in the plugin's resources, is purely informational. It describes the Python script that will be executed by the executables. Modifying or replacing it does not affect the plugin.

To build the Python executable required for the plugin, refer to the detailed instructions in the [BuildExecutable repository](https://github.com/vsgol/BuildExecutable).

