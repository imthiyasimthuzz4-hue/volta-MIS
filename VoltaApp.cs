using System;
using System.Diagnostics;
using System.IO;
using System.Net.Sockets;
using System.Threading;
using System.Windows.Forms;

namespace VoltaMis
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            string appDir = AppDomain.CurrentDomain.BaseDirectory;
            string jarPath = Path.Combine(appDir, @"target\volta-mis-1.0.0.jar");

            // 1. Check if VOLTA MIS server is already listening on port 8080
            bool isRunning = IsPortListening("127.0.0.1", 8080);
            if (!isRunning)
            {
                if (!File.Exists(jarPath))
                {
                    MessageBox.Show("VOLTA MIS application package was not found at:\n" + jarPath + "\n\nPlease ensure the application has been built.", "VOLTA MIS Launcher", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                // Start backend server silently in background
                ProcessStartInfo psi = new ProcessStartInfo();
                psi.FileName = "java.exe";
                psi.Arguments = string.Format("-jar \"{0}\"", jarPath);
                psi.WorkingDirectory = appDir;
                psi.WindowStyle = ProcessWindowStyle.Hidden;
                psi.CreateNoWindow = true;
                psi.UseShellExecute = false;

                try
                {
                    Process.Start(psi);
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Could not launch Java runtime: " + ex.Message + "\n\nPlease ensure Java 21 is installed.", "VOLTA MIS Launcher", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                // Wait up to 30 seconds for server to initialize
                for (int i = 0; i < 30; i++)
                {
                    Thread.Sleep(1000);
                    if (IsPortListening("127.0.0.1", 8080))
                    {
                        isRunning = true;
                        break;
                    }
                }
            }

            // 2. Launch Native Windows Standalone Application Window
            string edgePath = @"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe";
            if (!File.Exists(edgePath))
            {
                edgePath = @"C:\Program Files\Microsoft\Edge\Application\msedge.exe";
            }

            string targetUrl = "http://localhost:8080";
            if (File.Exists(edgePath))
            {
                ProcessStartInfo appWindow = new ProcessStartInfo();
                appWindow.FileName = edgePath;
                appWindow.Arguments = string.Format("--app=\"{0}\" --window-size=1366,820", targetUrl);
                Process.Start(appWindow);
            }
            else
            {
                // Fallback to default system browser
                Process.Start(targetUrl);
            }
        }

        static bool IsPortListening(string host, int port)
        {
            try
            {
                using (TcpClient client = new TcpClient())
                {
                    IAsyncResult result = client.BeginConnect(host, port, null, null);
                    bool success = result.AsyncWaitHandle.WaitOne(500);
                    if (!success) return false;
                    client.EndConnect(result);
                    return true;
                }
            }
            catch
            {
                return false;
            }
        }
    }
}
