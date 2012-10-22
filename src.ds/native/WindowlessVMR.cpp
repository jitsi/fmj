
#include <jni.h>
#include <windows.h>
#include <dshow.h>
#include "net_sf_jdshow_WindowlessVMR.h"
#include "jniutils.h"
#include "utils.h"

HRESULT InitWindowlessVMR( 
    HWND hwndApp,                  // Window to hold the video. 
    IGraphBuilder* pGraph,         // Pointer to the Filter Graph Manager. 
    IVMRWindowlessControl** ppWc   // Receives a pointer to the VMR.
    ) ;

JNIEXPORT jint JNICALL Java_net_sf_jdshow_WindowlessVMR_InitWindowlessVMR
  (JNIEnv *pEnv, jclass, jlong hwnd, jlong pGraph, jlongArray a)
{

	IVMRWindowlessControl *pIVMRWindowlessControl = 0;
	HRESULT hr = InitWindowlessVMR((HWND) hwnd, (IGraphBuilder *) jlong2ptr(pGraph), &pIVMRWindowlessControl);

	jlong *p = GetLongArr(pEnv, a);
	p[0] = ptr2jlong(pIVMRWindowlessControl);
	ReleaseLongArr(pEnv, a, p);

	return hr;
}

// from http://msdn.microsoft.com/library/default.asp?url=/library/en-us/directshow/htm/videorendering.asp

HRESULT InitWindowlessVMR( 
    HWND hwndApp,                  // Window to hold the video. 
    IGraphBuilder* pGraph,         // Pointer to the Filter Graph Manager. 
    IVMRWindowlessControl** ppWc   // Receives a pointer to the VMR.
    ) 
{ 
    if (!pGraph || !ppWc) 
    {
        return E_POINTER;
    }
    IBaseFilter* pVmr = NULL; 
    IVMRWindowlessControl* pWc = NULL; 
    // Create the VMR. 
    HRESULT hr = CoCreateInstance(CLSID_VideoMixingRenderer, NULL, 
        CLSCTX_INPROC, IID_IBaseFilter, (void**)&pVmr); 
    if (FAILED(hr))
    {
        return hr;
    }
    
    // Add the VMR to the filter graph.
    hr = pGraph->AddFilter(pVmr, L"Video Mixing Renderer"); 
    if (FAILED(hr)) 
    {
        pVmr->Release();
        return hr;
    }
    // Set the rendering mode.  
    IVMRFilterConfig* pConfig; 
    hr = pVmr->QueryInterface(IID_IVMRFilterConfig, (void**)&pConfig); 
    if (SUCCEEDED(hr)) 
    { 
        hr = pConfig->SetRenderingMode(VMRMode_Windowless); 
        pConfig->Release(); 
    }
    if (SUCCEEDED(hr))
    {
        // Set the window. 
        hr = pVmr->QueryInterface(IID_IVMRWindowlessControl, (void**)&pWc);
        if( SUCCEEDED(hr)) 
        { 
            hr = pWc->SetVideoClippingWindow(hwndApp); 
            if (SUCCEEDED(hr))
            {
                *ppWc = pWc; // Return this as an AddRef'd pointer. 
            }
            else
            {
                // An error occurred, so release the interface.
                pWc->Release();
            }
        } 
    } 
    pVmr->Release(); 
    return hr; 
} 