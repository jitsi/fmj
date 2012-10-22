#include <jni.h>
#include <windows.h>
#include <dshow.h>
#include "net_sf_jdshow_IMediaSeeking.h"
#include "jniutils.h"
#include "utils.h"

JNIEXPORT jint JNICALL Java_net_sf_jdshow_IMediaSeeking_GetPositions
  (JNIEnv *pEnv, jobject o, jlongArray a1, jlongArray a2)
{
	jlong *p1 = GetLongArr(pEnv, a1);
	jlong *p2 = GetLongArr(pEnv, a2);
	
	IMediaSeeking *pMediaSeeking = (IMediaSeeking *) getPeerPtr(pEnv, o);
	int hr = pMediaSeeking->GetPositions(p1, p2);
	
	ReleaseLongArr(pEnv, a1, p1);
	ReleaseLongArr(pEnv, a2, p2);

	return hr;
}

JNIEXPORT jint JNICALL Java_net_sf_jdshow_IMediaSeeking_SetPositions
  (JNIEnv *pEnv, jobject o, jlongArray a1, jint f1, jlongArray a2, jint f2)
{
	jlong *p1 = GetLongArr(pEnv, a1);
	jlong *p2 = GetLongArr(pEnv, a2);
	
	IMediaSeeking *pMediaSeeking = (IMediaSeeking *) getPeerPtr(pEnv, o);
	int hr = pMediaSeeking->SetPositions(p1, f1, p2, f2);
	
	ReleaseLongArr(pEnv, a1, p1);
	ReleaseLongArr(pEnv, a2, p2);

	return hr;

}

JNIEXPORT jint JNICALL Java_net_sf_jdshow_IMediaSeeking_SetRate
  (JNIEnv *pEnv, jobject o, jdouble rate)
{
	IMediaSeeking *pMediaSeeking = (IMediaSeeking *) getPeerPtr(pEnv, o);
	return pMediaSeeking->SetRate(rate);

}

JNIEXPORT jint JNICALL Java_net_sf_jdshow_IMediaSeeking_GetRate
  (JNIEnv *pEnv, jobject o, jdoubleArray a)
{
	jdouble *p = GetDoubleArr(pEnv, a);
	
	IMediaSeeking *pMediaSeeking = (IMediaSeeking *) getPeerPtr(pEnv, o);
	int hr = pMediaSeeking->GetRate(p);
	ReleaseDoubleArr(pEnv, a, p);
	return hr;	
}  


JNIEXPORT jint JNICALL Java_net_sf_jdshow_IMediaSeeking_GetDuration
  (JNIEnv *pEnv, jobject o, jlongArray a1)
{
	jlong *p1 = GetLongArr(pEnv, a1);
	
	IMediaSeeking *pMediaSeeking = (IMediaSeeking *) getPeerPtr(pEnv, o);
	int hr = pMediaSeeking->GetDuration(p1);
	
	ReleaseLongArr(pEnv, a1, p1);

	return hr;
}
  
JNIEXPORT jint JNICALL Java_net_sf_jdshow_IMediaSeeking_GetCurrentPosition
  (JNIEnv *pEnv, jobject o, jlongArray a1)
{
	jlong *p1 = GetLongArr(pEnv, a1);
	
	IMediaSeeking *pMediaSeeking = (IMediaSeeking *) getPeerPtr(pEnv, o);
	int hr = pMediaSeeking->GetCurrentPosition(p1);
	
	ReleaseLongArr(pEnv, a1, p1);

	return hr;
}
  
  

JNIEXPORT jobject JNICALL Java_net_sf_jdshow_IMediaSeeking_Init_1IID
  (JNIEnv *pEnv, jclass, jobject jGuid)
{
	fromGUID(pEnv, IID_IMediaSeeking, jGuid);
	return jGuid;
}


