#include <jni.h>
#include <string.h>
#include <windows.h>
#include "jniutils.h"

jlong ptr2jlong(void *ptr)
{
	jlong jl = 0;
	if (sizeof(void *) > sizeof(jlong))
	{	fprintf(stderr, "sizeof(void *) > sizeof(jlong)\n");
		return 0;
		//(* (int *) 0) = 0;	// crash.
	}
	
	memcpy(&jl, &ptr, sizeof(void *));
	return jl;
}

void *jlong2ptr(jlong jl)
{
	
	void *ptr = 0;
	if (sizeof(void *) > sizeof(jlong))
	{	fprintf(stderr, "sizeof(void *) > sizeof(jlong)\n");
		return 0;
		//(* (int *) 0) = 0;	// crash.
	}
	
	memcpy(&ptr, &jl, sizeof(void *));
	return ptr;
}

// Get a peer ptr from a Java object which extends Peer.  The peer ptr is the C++ pointer to the C++ peer.
void *getPeerPtr(JNIEnv *pEnv, jobject jObj)
{
	// TODO: we need to make sure the object passed in really is of the right class.	
	jclass jPeerClass = pEnv->FindClass("net/sf/jdshow/Peered");
	jmethodID jPeerGetPeerPtrMethodID = pEnv->GetMethodID(jPeerClass, "getPeerPtr", "()J");
	jlong result = pEnv->CallLongMethod(jObj, jPeerGetPeerPtrMethodID);
	return jlong2ptr(result);

}


// the following functions are needed because on some (non-windows) platforms, 
// a wchar_t is larger than a jchar.  A jchar is always 2 bytes, and on windows
// a wchar_t is 2 bytes, but on linux a wchar_t is 4 bytes.
jchar wchar_t_to_jchar(wchar_t wc)
{
	return (jchar) wc;
}
void wchar_t_to_jchar_array(const wchar_t *src, jchar *dest)
{	int len = wcslen(src);
	for (int i = 0; i <= len; ++i)
	{	dest[i] = wchar_t_to_jchar(src[i]);
	}
}
// caller must delete result.
jchar *wchar_t_to_jchar_array_alloc(const wchar_t *src)
{	jchar *result = new jchar[wcslen(src) + 1];
	wchar_t_to_jchar_array(src, result);
	return result;
}

size_t jslen(const jchar *s)
{	size_t result = 0;
	while (*s != 0)
	{	++s;
		++result;
	}
	return result;
}
wchar_t jchar_to_wchar_t(jchar jc)
{
	return (wchar_t) jc;
}
void jchar_to_wchar_t_array(const jchar *src, wchar_t *dest)
{	int len = jslen(src);
	for (int i = 0; i <= len; ++i)
	{	dest[i] = jchar_to_wchar_t(src[i]);
	}
}
// caller must delete result.
wchar_t *jchar_to_wchar_t_array_alloc(const jchar *src)
{	wchar_t *result = new wchar_t[jslen(src) + 1];
	jchar_to_wchar_t_array(src, result);
	return result;
}


GUID toGUID(JNIEnv *pEnv, jobject jGuid)
{
	GUID result;
	memset(&result, 0, sizeof(GUID));
	
	jclass jGuidClass = pEnv->FindClass("net/sf/jdshow/GUID");

    jfieldID  data1FieldID = pEnv->GetFieldID(jGuidClass, "Data1", "I");
    jint jData1 = pEnv->GetIntField(jGuid, data1FieldID);
	result.Data1 = jData1;
     
    jfieldID  data2FieldID = pEnv->GetFieldID(jGuidClass, "Data2", "S");
    jshort jData2 = pEnv->GetIntField(jGuid, data2FieldID);
	result.Data2 = jData2;

    jfieldID  data3FieldID = pEnv->GetFieldID(jGuidClass, "Data3", "S");
    jshort jData3 = pEnv->GetShortField(jGuid, data3FieldID);
	result.Data3 = jData3;     
	
	jfieldID  data4FieldID = pEnv->GetFieldID(jGuidClass, "Data4", "[B");
    jbyteArray jData4 = (jbyteArray) pEnv->GetObjectField(jGuid, data4FieldID);
    jbyte *jData4Bytes = GetByteArr(pEnv, jData4);
	memcpy(result.Data4, jData4Bytes, sizeof(result.Data4));
	ReleaseByteArr(pEnv, jData4, jData4Bytes);
	
	return result;
	
	
}

void fromGUID(JNIEnv *pEnv, GUID src, jobject jGuid)
{
	
	jclass jGuidClass = pEnv->FindClass("net/sf/jdshow/GUID");

    jfieldID data1FieldID = pEnv->GetFieldID(jGuidClass, "Data1", "I");
    pEnv->SetIntField(jGuid, data1FieldID, src.Data1);
	  
    jfieldID  data2FieldID = pEnv->GetFieldID(jGuidClass, "Data2", "S");
    pEnv->SetIntField(jGuid, data2FieldID, src.Data2);
	
    jfieldID  data3FieldID = pEnv->GetFieldID(jGuidClass, "Data3", "S");
    pEnv->SetShortField(jGuid, data3FieldID, src.Data3);
	
	jfieldID  data4FieldID = pEnv->GetFieldID(jGuidClass, "Data4", "[B");
    jbyteArray jData4 = (jbyteArray) pEnv->GetObjectField(jGuid, data4FieldID);
    jbyte *jData4Bytes = GetByteArr(pEnv, jData4);
	memcpy(jData4Bytes, src.Data4, sizeof(src.Data4));
	ReleaseByteArr(pEnv, jData4, jData4Bytes);
	
	
	
}