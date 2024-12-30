import os
import platform
import subprocess

def build_server():
    system = platform.system().lower()
    if system == "windows":
        output_dir = "dist/windows"
    elif system == "linux":
        output_dir = "dist/linux"
    elif system == "darwin":
        output_dir = "dist/macos"
    else:
        raise Exception(f"Unsupported OS: {system}")
    pyinstaller_command = ["pyinstaller", "--onefile", "--distpath", output_dir, "text_processor_server.py"]

    print(f"Building for {system}")
    subprocess.run(pyinstaller_command, check=True)
    print(f"Build complete. Executable saved in {output_dir}.")

if __name__ == "__main__":
    build_server()
