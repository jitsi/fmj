jlong ptr2jlong(void *ptr);
void *jlong2ptr(jlong jl);
void *getPeerPtr(JNIEnv *pEnv, jobject jObj);

// the following functions are needed because on some (non-windows) platforms, 
// a wchar_t is larger than a jchar.  A jchar is always 2 bytes, and on windows
// a wchar_t is 2 bytes, but on linux a wchar_t is 4 bytes.
jchar wchar_t_to_jchar(wchar_t wc);
void wchar_t_to_jchar_array(const wchar_t *src, jchar *dest);
// caller must delete result.
jchar *wchar_t_to_jchar_array_alloc(const wchar_t *src);

size_t jslen(const jchar *s);
wchar_t jchar_to_wchar_t(jchar jc);
void jchar_to_wchar_t_array(const jchar *src, wchar_t *dest);
// caller must delete result.
wchar_t *jchar_to_wchar_t_array_alloc(const jchar *src);


GUID toGUID(JNIEnv *pEnv, jobject jGuid);
void fromGUID(JNIEnv *pEnv, GUID src, jobject jGuid);