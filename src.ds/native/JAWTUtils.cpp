#include <windows.h>
#include <assert.h>
#include "jawt_md.h"
#include "net_sf_jdshow_JAWTUtils.h"


JNIEXPORT jlong JNICALL Java_net_sf_jdshow_JAWTUtils_getWindowHandle
  (JNIEnv *env, jclass, jobject canvas)
{
		
	    JAWT awt;
        JAWT_DrawingSurface* ds;
        JAWT_DrawingSurfaceInfo* dsi;
        JAWT_Win32DrawingSurfaceInfo* dsi_win;
        jboolean result;
        jint lock;
        // Get the AWT
        awt.version = JAWT_VERSION_1_3;
        result = JAWT_GetAWT(env, &awt);
        assert(result != JNI_FALSE);
        // Get the drawing surface
        ds = awt.GetDrawingSurface(env, canvas);
        if(ds == NULL)
            return 0;
        // Lock the drawing surface
        lock = ds->Lock(ds);
        assert((lock & JAWT_LOCK_ERROR) == 0);
        // Get the drawing surface info
        dsi = ds->GetDrawingSurfaceInfo(ds);
        // Get the platform-specific drawing info
        dsi_win = (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;
        HDC hdc = dsi_win->hdc;
        HWND hWnd = dsi_win->hwnd;
       

        // Free the drawing surface info
        ds->FreeDrawingSurfaceInfo(dsi);
        // Unlock the drawing surface
        ds->Unlock(ds);
        // Free the drawing surface
        awt.FreeDrawingSurface(ds);

		return (jlong) hWnd;
}