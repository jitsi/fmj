#ifndef _JNI_UTILS_INCLUDED_
#define _JNI_UTILS_INCLUDED_

inline const char *GetStr(JNIEnv *env, jstring js)
{	if (js == NULL)
		return NULL;
	return env->GetStringUTFChars(js, NULL);
}
inline void ReleaseStr(JNIEnv *env, jstring js, const char *cs)
{	if (js == NULL)
		return;
	env->ReleaseStringUTFChars(js, cs);
}

inline jint *GetIntArr(JNIEnv *env, jintArray a)
{	if (a == NULL)
		return NULL;
	return env->GetIntArrayElements(a, NULL);
}
inline void ReleaseIntArr(JNIEnv *env, jintArray a, jint *ca)
{	if (a == NULL)
		return;
	env->ReleaseIntArrayElements(a, ca, 0);
}

inline jshort *GetShortArr(JNIEnv *env, jshortArray a)
{	if (a == NULL)
		return NULL;
	return env->GetShortArrayElements(a, NULL);
}
inline void ReleaseShortArr(JNIEnv *env, jshortArray a, jshort *ca)
{	if (a == NULL)
		return;
	env->ReleaseShortArrayElements(a, ca, 0);
}


inline jbyte *GetByteArr(JNIEnv *env, jbyteArray a)
{	if (a == NULL)
		return NULL;
	return env->GetByteArrayElements(a, NULL);
}
inline void ReleaseByteArr(JNIEnv *env, jbyteArray a, jbyte *ca)
{	if (a == NULL)
		return;
	env->ReleaseByteArrayElements(a, ca, 0);
}
inline jlong *GetLongArr(JNIEnv *env, jlongArray a)
{	if (a == NULL)
		return NULL;
	return env->GetLongArrayElements(a, NULL);
}
inline void ReleaseLongArr(JNIEnv *env, jlongArray a, jlong *ca)
{	if (a == NULL)
		return;
	env->ReleaseLongArrayElements(a, ca, 0);
}

inline jdouble *GetDoubleArr(JNIEnv *env, jdoubleArray a)
{	if (a == NULL)
		return NULL;
	return env->GetDoubleArrayElements(a, NULL);
}
inline void ReleaseDoubleArr(JNIEnv *env, jdoubleArray a, jdouble *ca)
{	if (a == NULL)
		return;
	env->ReleaseDoubleArrayElements(a, ca, 0);
}

inline jstring NewStr(JNIEnv *env, const char *utf)
{
	if (utf == NULL)
		return NULL;
	return env->NewStringUTF(utf);
}

inline jbyteArray NewByteArray(JNIEnv *env, const jbyte *ba, jsize len)
{	if (ba == NULL)
		return NULL;
	jbyteArray result = env->NewByteArray(len);

	jbyte *resultElements = env->GetByteArrayElements(result, 0);
	memcpy(resultElements, ba, len);
	env->ReleaseByteArrayElements(result, resultElements, 0);

	return result;
}

#endif
