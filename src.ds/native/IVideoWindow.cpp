#include <jni.h>
#include <windows.h>
#include <dshow.h>
#include "net_sf_jdshow_IVideoWindow.h"
#include "jniutils.h"
#include "utils.h"


JNIEXPORT jint JNICALL Java_net_sf_jdshow_IVideoWindow_put_1Owner
  (JNIEnv *pEnv, jobject o, jlong hwnd)
{
	IVideoWindow *pVideoWindow = (IVideoWindow *) getPeerPtr(pEnv, o);
	return pVideoWindow->put_Owner(hwnd);
}


JNIEXPORT jint JNICALL Java_net_sf_jdshow_IVideoWindow_put_1WindowStyle
  (JNIEnv *pEnv, jobject o, jint style)
{
	IVideoWindow *pVideoWindow = (IVideoWindow *) getPeerPtr(pEnv, o);
	return pVideoWindow->put_WindowStyle(style);
}  


JNIEXPORT jint JNICALL Java_net_sf_jdshow_IVideoWindow_put_1MessageDrain
  (JNIEnv *pEnv, jobject o, jlong hwnd)
{
	IVideoWindow *pVideoWindow = (IVideoWindow *) getPeerPtr(pEnv, o);
	return pVideoWindow->put_MessageDrain(hwnd);
}

JNIEXPORT jint JNICALL Java_net_sf_jdshow_IVideoWindow_put_1Top
  (JNIEnv *pEnv, jobject o, jint value)
{
	IVideoWindow *pVideoWindow = (IVideoWindow *) getPeerPtr(pEnv, o);
	return pVideoWindow->put_Top(value);
}

JNIEXPORT jint JNICALL Java_net_sf_jdshow_IVideoWindow_put_1Left
  (JNIEnv *pEnv, jobject o, jint value)
{
	IVideoWindow *pVideoWindow = (IVideoWindow *) getPeerPtr(pEnv, o);
	return pVideoWindow->put_Left(value);
}  

JNIEXPORT jint JNICALL Java_net_sf_jdshow_IVideoWindow_get_1Width
  (JNIEnv *pEnv, jobject o, jlongArray a1)
{
	jlong *p1 = GetLongArr(pEnv, a1);
	
	IVideoWindow *pVideoWindow = (IVideoWindow *) getPeerPtr(pEnv, o);
	int hr = pVideoWindow->get_Width((long *) p1);
	
	ReleaseLongArr(pEnv, a1, p1);

	return hr;
}   


JNIEXPORT jint JNICALL Java_net_sf_jdshow_IVideoWindow_get_1Height
  (JNIEnv *pEnv, jobject o, jlongArray a1)
{
	jlong *p1 = GetLongArr(pEnv, a1);
	
	IVideoWindow *pVideoWindow = (IVideoWindow *) getPeerPtr(pEnv, o);
	int hr = pVideoWindow->get_Height((long *) p1);
	
	ReleaseLongArr(pEnv, a1, p1);

	return hr;
}   
