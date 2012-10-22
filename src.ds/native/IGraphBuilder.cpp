#include <jni.h>
#include <windows.h>
#include <dshow.h>
#include "net_sf_jdshow_IGraphBuilder.h"
#include "jniutils.h"
#include "utils.h"

JNIEXPORT jint JNICALL Java_net_sf_jdshow_IGraphBuilder_RenderFile
  (JNIEnv *pEnv, jobject o, jstring jFileStr, jstring playlistStr)
{

	// TODO: handle null strings
	const jchar *jFileStrArray = pEnv->GetStringChars(jFileStr, NULL);
	const wchar_t *szFileStr = jchar_to_wchar_t_array_alloc(jFileStrArray);	// TODO: delete

	IGraphBuilder *pGraphBuilder = (IGraphBuilder *) getPeerPtr(pEnv, o);
	jint result = pGraphBuilder->RenderFile(szFileStr, NULL);	// TODO: playlist param

	pEnv->ReleaseStringChars(jFileStr, jFileStrArray);

	return result;
}


JNIEXPORT jobject JNICALL Java_net_sf_jdshow_IGraphBuilder_Init_1IID
  (JNIEnv *pEnv, jclass, jobject jGuid)
{
	fromGUID(pEnv, IID_IGraphBuilder, jGuid);
	return jGuid;
}