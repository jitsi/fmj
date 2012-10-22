#include <jni.h>
#include <windows.h>
#include <dshow.h>
#include "net_sf_jdshow_IMediaControl.h"
#include "jniutils.h"
#include "utils.h"

JNIEXPORT jint JNICALL Java_net_sf_jdshow_IMediaControl_Run
  (JNIEnv *pEnv, jobject o)
{
	IMediaControl *p = (IMediaControl *) getPeerPtr(pEnv, o);
	return p->Run();
}

/*
 * Class:     net_sf_jdshow_IMediaControl
 * Method:    Stop
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sf_jdshow_IMediaControl_Stop
  (JNIEnv *pEnv, jobject o)
{
	IMediaControl *pMediaControl = (IMediaControl *) getPeerPtr(pEnv, o);
	return pMediaControl->Stop();
}
