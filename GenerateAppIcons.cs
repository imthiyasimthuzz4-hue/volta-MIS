using System;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.IO;

class IconGenerator
{
    static void Main()
    {
        string outDir = @"src\main\resources\static\images";
        Directory.CreateDirectory(outDir);

        string desktopDir = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.UserProfile), @"OneDrive\Desktop\VOLTA_Official_App_Icons");
        Directory.CreateDirectory(desktopDir);

        // 1. Generate 512x512 Master App Icon
        using (Bitmap bmp512 = CreateAppIcon(512, false))
        {
            bmp512.Save(Path.Combine(outDir, "icon-512.png"), ImageFormat.Png);
            bmp512.Save(Path.Combine(desktopDir, "icon-512-playstore.png"), ImageFormat.Png);
            Console.WriteLine("Saved 512x512 icon");
        }

        // 2. Generate Maskable Icon (safe zone padding for Android 12+)
        using (Bitmap bmpMaskable = CreateAppIcon(512, true))
        {
            bmpMaskable.Save(Path.Combine(outDir, "icon-maskable.png"), ImageFormat.Png);
            bmpMaskable.Save(Path.Combine(desktopDir, "icon-maskable.png"), ImageFormat.Png);
            Console.WriteLine("Saved maskable icon");
        }

        // 3. Generate 192x192 Android Icon
        using (Bitmap bmp192 = CreateAppIcon(192, false))
        {
            bmp192.Save(Path.Combine(outDir, "icon-192.png"), ImageFormat.Png);
            bmp192.Save(Path.Combine(desktopDir, "icon-192-android.png"), ImageFormat.Png);
            Console.WriteLine("Saved 192x192 icon");
        }

        // 4. Generate 180x180 Apple Touch Icon (iOS Home Screen)
        using (Bitmap bmp180 = CreateAppIcon(180, false))
        {
            bmp180.Save(Path.Combine(outDir, "apple-touch-icon.png"), ImageFormat.Png);
            bmp180.Save(Path.Combine(desktopDir, "apple-touch-icon-180.png"), ImageFormat.Png);
            Console.WriteLine("Saved 180x180 Apple touch icon");
        }

        // 5. Generate Favicon 64x64 & 32x32
        using (Bitmap bmp64 = CreateAppIcon(64, false))
        {
            bmp64.Save(Path.Combine(outDir, "favicon.png"), ImageFormat.Png);
            bmp64.Save(Path.Combine(desktopDir, "favicon.png"), ImageFormat.Png);
            Console.WriteLine("Saved favicon.png");
        }

        // Copy to target classes
        string targetDir = @"target\classes\static\images";
        if (Directory.Exists(targetDir))
        {
            File.Copy(Path.Combine(outDir, "icon-512.png"), Path.Combine(targetDir, "icon-512.png"), true);
            File.Copy(Path.Combine(outDir, "icon-maskable.png"), Path.Combine(targetDir, "icon-maskable.png"), true);
            File.Copy(Path.Combine(outDir, "icon-192.png"), Path.Combine(targetDir, "icon-192.png"), true);
            File.Copy(Path.Combine(outDir, "apple-touch-icon.png"), Path.Combine(targetDir, "apple-touch-icon.png"), true);
            File.Copy(Path.Combine(outDir, "favicon.png"), Path.Combine(targetDir, "favicon.png"), true);
            Console.WriteLine("Copied all icons to target/classes");
        }
    }

    static Bitmap CreateAppIcon(int size, bool maskable)
    {
        Bitmap bmp = new Bitmap(size, size, PixelFormat.Format32bppArgb);
        using (Graphics g = Graphics.FromImage(bmp))
        {
            g.SmoothingMode = SmoothingMode.AntiAlias;
            g.InterpolationMode = InterpolationMode.HighQualityBicubic;
            g.PixelOffsetMode = PixelOffsetMode.HighQuality;
            g.TextRenderingHint = System.Drawing.Text.TextRenderingHint.AntiAliasGridFit;

            // Clear transparent
            g.Clear(Color.Transparent);

            // Compute bounds
            Rectangle rect;
            if (maskable)
            {
                // Full bleed square for maskable
                rect = new Rectangle(0, 0, size, size);
                using (LinearGradientBrush bgBrush = new LinearGradientBrush(rect, Color.FromArgb(7, 21, 41), Color.FromArgb(0, 51, 102), 45f))
                {
                    g.FillRectangle(bgBrush, rect);
                }
            }
            else
            {
                // Rounded squircle (modern iOS / Android standard)
                int margin = (int)(size * 0.04);
                rect = new Rectangle(margin, margin, size - 2 * margin, size - 2 * margin);
                int radius = (int)(size * 0.22);

                using (GraphicsPath path = GetRoundedRect(rect, radius))
                {
                    // Gradient Fill
                    using (LinearGradientBrush bgBrush = new LinearGradientBrush(rect, Color.FromArgb(7, 21, 41), Color.FromArgb(0, 51, 102), 45f))
                    {
                        g.FillPath(bgBrush, path);
                    }

                    // Golden Border Glow
                    using (Pen borderPen = new Pen(Color.FromArgb(255, 204, 0), (float)(size * 0.02)))
                    {
                        g.DrawPath(borderPen, path);
                    }
                }
            }

            // Draw Central Lightning Bolt & Energy Circle
            float scale = size / 512f;
            float cx = size / 2f;
            float cy = size * (maskable ? 0.44f : 0.42f);

            // Glow Circle behind lightning
            float circleR = 120f * scale;
            using (GraphicsPath circlePath = new GraphicsPath())
            {
                circlePath.AddEllipse(cx - circleR, cy - circleR, circleR * 2, circleR * 2);
                using (PathGradientBrush pgb = new PathGradientBrush(circlePath))
                {
                    pgb.CenterColor = Color.FromArgb(90, 255, 204, 0);
                    pgb.SurroundColors = new Color[] { Color.Transparent };
                    g.FillEllipse(pgb, cx - circleR, cy - circleR, circleR * 2, circleR * 2);
                }
            }

            // Golden Lightning Bolt Path
            using (GraphicsPath bolt = new GraphicsPath())
            {
                PointF[] pts = new PointF[] {
                    new PointF(cx + 10 * scale, cy - 110 * scale),
                    new PointF(cx - 70 * scale, cy + 5 * scale),
                    new PointF(cx - 10 * scale, cy + 5 * scale),
                    new PointF(cx - 35 * scale, cy + 120 * scale),
                    new PointF(cx + 65 * scale, cy - 5 * scale),
                    new PointF(cx + 5 * scale, cy - 5 * scale)
                };
                bolt.AddPolygon(pts);

                // Golden Gradient for lightning
                using (LinearGradientBrush boltBrush = new LinearGradientBrush(
                    new PointF(cx - 70 * scale, cy - 110 * scale),
                    new PointF(cx + 65 * scale, cy + 120 * scale),
                    Color.FromArgb(255, 240, 100),
                    Color.FromArgb(245, 170, 0)))
                {
                    g.FillPath(boltBrush, bolt);
                }

                // White Core highlight
                using (Pen corePen = new Pen(Color.FromArgb(200, 255, 255, 255), 2.5f * scale))
                {
                    g.DrawPath(corePen, bolt);
                }
            }

            // Text "VOLTA" at bottom
            if (size >= 120)
            {
                float fontSize = 56f * scale;
                using (Font font = new Font("Arial", fontSize, FontStyle.Bold))
                {
                    string text = "VOLTA";
                    SizeF textSize = g.MeasureString(text, font);
                    float tx = cx - (textSize.Width / 2f);
                    float ty = size * (maskable ? 0.72f : 0.74f);

                    // Text Shadow
                    using (SolidBrush shadowBrush = new SolidBrush(Color.FromArgb(160, 0, 0, 0)))
                    {
                        g.DrawString(text, font, shadowBrush, tx + 2 * scale, ty + 2 * scale);
                    }

                    // Text Fill (White/Gold)
                    using (SolidBrush textBrush = new SolidBrush(Color.White))
                    {
                        g.DrawString(text, font, textBrush, tx, ty);
                    }
                }
            }
        }
        return bmp;
    }

    static GraphicsPath GetRoundedRect(Rectangle bounds, int radius)
    {
        int diameter = radius * 2;
        Size size = new Size(diameter, diameter);
        Rectangle arc = new Rectangle(bounds.Location, size);
        GraphicsPath path = new GraphicsPath();

        if (radius == 0)
        {
            path.AddRectangle(bounds);
            return path;
        }

        // top left arc  
        path.AddArc(arc, 180, 90);

        // top right arc  
        arc.X = bounds.Right - diameter;
        path.AddArc(arc, 270, 90);

        // bottom right arc  
        arc.Y = bounds.Bottom - diameter;
        path.AddArc(arc, 0, 90);

        // bottom left arc 
        arc.X = bounds.Left;
        path.AddArc(arc, 90, 90);

        path.CloseFigure();
        return path;
    }
}
