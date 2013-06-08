package net.sf.jdshow;

import java.awt.*;

import javax.swing.*;

/**
 *
 * @author Ken Larson
 *
 */
public class Tester
{
    public static void main(String[] args) throws ComException,
            InterruptedException
    {
        System.loadLibrary("jdshow");

        final long hwnd;
        {
            Frame f = new Frame("hello");

            // f.setSize(300,400);
            JWindow w = new JWindow(f);

            w.setBackground(new Color(0, 0, 0, 255));

            w.getContentPane().setBackground(new Color(0, 0, 0, 255));

            // JAWTUtils my = new JAWTUtils();
            // w.getContentPane().add(my);
            Canvas c = new Canvas();
            w.getContentPane().add(c);

            w.setBounds(0, 0, 600, 500);

            // System.out.println("getWindowHandle: " +
            // JAWTUtils.getWindowHandle(c));

            w.setVisible(true);

            System.out.println("getWindowHandle: "
                    + JAWTUtils.getWindowHandle(c));
            hwnd = JAWTUtils.getWindowHandle(c);
        }

        int hr;

        Com.CoInitialize();

        long[] p = new long[1];
        hr = Com.CoCreateInstance(Com.CLSID_FilterGraph, 0L, Com.CLSCTX_ALL,
                Com.IID_IGraphBuilder, p);
        if (Com.FAILED(hr))
            throw new ComException(hr);

        IGraphBuilder pGraphBuilder = new IGraphBuilder(p[0]);

        // TODO: not working on win2k:
        //
        // long[] pWc = new long[1];
        // //IVMRWindowlessControl *pWc = NULL;
        // hr = WindowlessVMR.InitWindowlessVMR(hwnd,
        // pGraphBuilder.getPeerPtr(), p);
        // if (Com.FAILED(hr))
        // throw new ComException(hr);
        //
        // {
        //
        // // Release the VMR interface when you are done.
        // // TODO: pWc->Release();
        // }

        String MediaFile = args[0];
        hr = pGraphBuilder.RenderFile(MediaFile, "");
        if (Com.FAILED(hr))
            throw new ComException(hr);

        hr = pGraphBuilder.QueryInterface(Com.IID_IMediaControl, p);
        if (Com.FAILED(hr))
            throw new ComException(hr);

        IMediaControl pMediaControl = new IMediaControl(p[0]);

        WindowedRendering.InitWindowedRendering2(hwnd, pGraphBuilder);
        if (Com.FAILED(hr))
            throw new ComException(hr);

        hr = pMediaControl.Run();
        if (Com.FAILED(hr))
            throw new ComException(hr);

        Thread.sleep(10000);

        pMediaControl.Stop();
        pMediaControl.Release();
        pGraphBuilder.Release();

        Com.CoUninitialize();
    }
}
