#include <jni.h>
#include <windows.h>
#include <dshow.h>
#include "net_sf_jdshow_Com.h"
#include "jniutils.h"
#include "utils.h"


JNIEXPORT void JNICALL Java_net_sf_jdshow_Com_CoInitialize
  (JNIEnv *, jclass)
{
	CoInitialize(NULL);	// TODO: return value, parameter value
}


JNIEXPORT void JNICALL Java_net_sf_jdshow_Com_CoUninitialize
  (JNIEnv *, jclass)
{
	CoUninitialize();
}


JNIEXPORT jint JNICALL Java_net_sf_jdshow_Com_CoCreateInstance
  (JNIEnv *pEnv, jclass, jobject jrclsid, jlong jpUnkOuter, jint jdwClsContext, jobject jriid, jlongArray a)
{
	void *pResult = 0;

	int hr = CoCreateInstance(toGUID(pEnv, jrclsid), (LPUNKNOWN) jlong2ptr(jpUnkOuter), jdwClsContext, toGUID(pEnv, jriid),
							(void**)&pResult);

	jlong *p = GetLongArr(pEnv, a);
	p[0] = ptr2jlong(pResult);
	ReleaseLongArr(pEnv, a, p);

	return hr;
}
