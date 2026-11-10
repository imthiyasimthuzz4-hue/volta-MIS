using System;
using System.Diagnostics;
using System.IO;
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

            string cloudUrl = "https://volta-mis-app.onrender.com";

            // 1. Locate Microsoft Edge or Google Chrome for Native App Container
            string browserPath = null;
            string[] candidatePaths = new string[]
            {
                @"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
                @"C:\Program Files\Microsoft\Edge\Application\msedge.exe",
                @"C:\Program Files\Google\Chrome\Application\chrome.exe",
                @"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe"
            };

            foreach (string path in candidatePaths)
            {
                if (File.Exists(path))
                {
                    browserPath = path;
                    break;
                }
            }

            try
            {
                if (!string.IsNullOrEmpty(browserPath))
                {
                    ProcessStartInfo appInfo = new ProcessStartInfo();
                    appInfo.FileName = browserPath;
                    appInfo.Arguments = string.Format("--app=\"{0}\" --window-size=1400,900", cloudUrl);
                    appInfo.UseShellExecute = true;
                    Process.Start(appInfo);
                }
                else
                {
                    Process.Start(new ProcessStartInfo(cloudUrl) { UseShellExecute = true });
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Could not launch VOLTA MIS PC App:\n" + ex.Message, "VOLTA MIS Desktop", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }
    }
}
