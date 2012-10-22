#include <jni.h>
#include <windows.h>
#include <dshow.h>
#include "net_sf_jdshow_IUnknown.h"
#include "jniutils.h"
#include "utils.h"


JNIEXPORT jlong JNICALL Java_net_sf_jdshow_IUnknown_Release
  (JNIEnv *pEnv, jobject o)
{
	IUnknown *pUnknown = (IUnknown *) getPeerPtr(pEnv, o);
	return pUnknown->Release();

}


JNIEXPORT jint JNICALL Java_net_sf_jdshow_IUnknown_QueryInterface
  (JNIEnv *pEnv, jobject o, jobject jGuid, jlongArray a)
{
	void *pResult = 0;
	
	IUnknown *pUnknown = (IUnknown *) getPeerPtr(pEnv, o);
	int hr = pUnknown->QueryInterface(toGUID(pEnv, jGuid), (void**)&pResult);

	jlong *p = GetLongArr(pEnv, a);
	p[0] = ptr2jlong(pResult);
	ReleaseLongArr(pEnv, a, p);

	return hr;
}