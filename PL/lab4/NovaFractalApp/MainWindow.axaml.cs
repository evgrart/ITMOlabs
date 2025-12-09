using Avalonia;
using Avalonia.Controls;
using Avalonia.Media;
using Avalonia.Media.Imaging;
using Avalonia.Platform;
using Avalonia.Threading;
using System;
using System.Diagnostics;
using System.Runtime.InteropServices;

namespace NovaFractalApp;

public partial class MainWindow : Window
{
    [DllImport("libNovaFractalNative.so", CallingConvention = CallingConvention.Cdecl)]
    public static extern void CalculateNovaFractal(IntPtr buffer, int width, int height, double zoom, double centerX, double centerY);

    private WriteableBitmap? _bitmap;
    private double _zoom = 1.0;
    private Stopwatch _fpsTimer = new Stopwatch();
    private int _frameCount = 0;
        private DispatcherTimer _renderTimer;

    public MainWindow()
    {
        InitializeComponent();
        
        this.Opened += OnOpened;
    }

    private void OnOpened(object? sender, EventArgs e)
    {
        var pixelSize = new PixelSize((int)this.Bounds.Width, (int)this.Bounds.Height);
        
        _bitmap = new WriteableBitmap(pixelSize, new Vector(96, 96), PixelFormat.Bgra8888, AlphaFormat.Opaque);
        
        FractalImage.Source = _bitmap;

        _fpsTimer.Start();

        _renderTimer = new DispatcherTimer(TimeSpan.FromMilliseconds(16), DispatcherPriority.Render, RenderLoop);
        _renderTimer.Start();
    }

    private void RenderLoop(object? sender, EventArgs e)
    {
        if (_bitmap == null) return;

        _zoom *= 1.02; 
        if (_zoom > 2000000.0) _zoom = 1.0; 

        using (var buffer = _bitmap.Lock())
        {
             CalculateNovaFractal(buffer.Address, _bitmap.PixelSize.Width, _bitmap.PixelSize.Height, _zoom, 0.0, 0.0);
        }

        FractalImage.InvalidateVisual();

        _frameCount++;
        if (_fpsTimer.ElapsedMilliseconds >= 1000)
        {
            FpsText.Text = $"FPS: {_frameCount} | Zoom: {_zoom:F2}";
            _frameCount = 0;
            _fpsTimer.Restart();
        }
    }
}