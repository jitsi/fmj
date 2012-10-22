#include <jni.h>
#include <windows.h>
#include <dshow.h>
#include "net_sf_jdshow_WindowedRendering.h"
#include "jniutils.h"
#include "utils.h"

HRESULT InitWindowedRendering( 
    HWND hwndApp,                  // Window to hold the video. 
    IGraphBuilder* pGraph         // Pointer to the Filter Graph Manager. 
	);

JNIEXPORT jint JNICALL Java_net_sf_jdshow_WindowedRendering_InitWindowedRendering
  (JNIEnv *, jclass, jlong hwnd, jlong pGraph)
{

	HRESULT hr = InitWindowedRendering((HWND) hwnd, (IGraphBuilder *) jlong2ptr(pGraph));

	return hr;
}

// from http://dsforums.agava.com/cgi/yabb/YaBB.cgi?board=directshow;action=display;num=1093528376

#define WM_GRAPHNOTIFY (WM_APP+1)	// doesn't do anything...

HRESULT InitWindowedRendering( 
    HWND hwndApp,                  // Window to hold the video. 
    IGraphBuilder* pGraph         // Pointer to the Filter Graph Manager. 
    ) 
{ 	
	IVideoWindow *piVideoWindow = 0;
	int hr;

	hr = pGraph->QueryInterface(IID_IVideoWindow, (void **)&piVideoWindow);
	if (FAILED(hr)) return hr;
	 
	hr = piVideoWindow->put_Owner((OAHWND)hwndApp);
	if (FAILED(hr)) return hr;
	 
	hr = piVideoWindow->put_WindowStyle(WS_CHILD | WS_CLIPSIBLINGS);
	if (FAILED(hr)) return hr;
	 
	hr = piVideoWindow->put_MessageDrain((OAHWND)hwndApp);
	if (FAILED(hr)) return hr;
	 
	hr = piVideoWindow->put_Top(0);
	if (FAILED(hr)) return hr;

	hr = piVideoWindow->put_Left(0);
	if (FAILED(hr)) return hr;

//	hr = piVideoWindow->put_AutoShow(OAFALSE);
//	if (FAILED(hr)) return hr;
#if 0
	IMediaEventEx *pMediaEvent = 0;
	hr = pGraph->QueryInterface(IID_IMediaEventEx, (void**)&pMediaEvent);
	if (FAILED(hr)) return hr;

	pMediaEvent->SetNotifyWindow((OAHWND)hwndApp, WM_GRAPHNOTIFY, 0); 
#endif

	piVideoWindow->Release(); 
	return S_OK;
}