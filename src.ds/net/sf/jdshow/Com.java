package net.sf.jdshow;

/**
 * General COM and Win32 definitions
 * @author Ken Larson
 *
 */
public class Com
{
	public static final int S_OK = 0;
	// from http://www.csn.ul.ie/~caolan/pub/winresdump/winresdump/resfmt.h;
	/* Window Styles */
	public static final int WS_OVERLAPPED    = 0x00000000;

	public static final int WS_POPUP         = 0x80000000;

	public static final int WS_CHILD         = 0x40000000;
	public static final int WS_MINIMIZE      = 0x20000000;

	public static final int WS_VISIBLE       = 0x10000000;



	public static final int WS_DISABLED      = 0x08000000;
	public static final int WS_CLIPSIBLINGS  = 0x04000000;
	public static final int WS_CLIPCHILDREN  = 0x02000000;
	public static final int WS_MAXIMIZE      = 0x01000000;
	public static final int WS_CAPTION       = 0x00C00000;
	public static final int WS_BORDER        = 0x00800000;
	public static final int WS_DLGFRAME      = 0x00400000;
	public static final int WS_VSCROLL       = 0x00200000;
	public static final int WS_HSCROLL       = 0x00100000;
	public static final int WS_SYSMENU       = 0x00080000;
	public static final int WS_THICKFRAME    = 0x00040000;
	public static final int WS_GROUP         = 0x00020000;
	public static final int WS_TABSTOP       = 0x00010000;
	public static final int WS_MINIMIZEBOX   = 0x00020000;
	public static final int WS_MAXIMIZEBOX   = 0x00010000;
	public static final int WS_TILED         = WS_OVERLAPPED;
	public static final int WS_ICONIC         = WS_MINIMIZE;
	public static final int WS_SIZEBOX        = WS_THICKFRAME;
	public static final int WS_OVERLAPPEDWINDOW  = (WS_OVERLAPPED | WS_CAPTION | WS_SYSMENU | WS_THICKFRAME| WS_MINIMIZEBOX | WS_MAXIMIZEBOX);
	public static final int WS_POPUPWINDOW  = (WS_POPUP | WS_BORDER | WS_SYSMENU);
	public static final int WS_CHILDWINDOW = (WS_CHILD);
	public static final int WS_TILEDWINDOW  = (WS_OVERLAPPEDWINDOW);
	//DEFINE_GUID(IID_IMediaControl,0x56a868b1,0x0ad4,0x11ce,0xb0,0x3a,0x00,0x20,0xaf,0x0b,0xa7,0x70);
	public static final GUID IID_IMediaControl = new GUID(0x56a868b1,0x0ad4,0x11ce,0xb0,0x3a,0x00,0x20,0xaf,0x0b,0xa7,0x70);
	//DEFINE_GUID(IID_IVideoWindow,0x56a868b4,0x0ad4,0x11ce,0xb0,0x3a,0x00,0x20,0xaf,0x0b,0xa7,0x70);
	public static final GUID IID_IVideoWindow = new GUID(0x56a868b4,0x0ad4,0x11ce,0xb0,0x3a,0x00,0x20,0xaf,0x0b,0xa7,0x70);
	public static final GUID IID_IMediaSeeking = IMediaSeeking.Init_IID(new GUID());
	public static final GUID CLSID_FilterGraph = new GUID(0xe436ebb3, 0x524f, 0x11ce, 0x9f, 0x53, 0x00, 0x20, 0xaf, 0x0b, 0xa7, 0x70);
	public static final GUID IID_IGraphBuilder = IGraphBuilder.Init_IID(new GUID());

	//enum tagCLSCTX
    //{
	public static final int CLSCTX_INPROC_SERVER	= 0x1;

	public static final int CLSCTX_INPROC_HANDLER	= 0x2;

	public static final int CLSCTX_LOCAL_SERVER	= 0x4;

	public static final int CLSCTX_INPROC_SERVER16	= 0x8;

	public static final int CLSCTX_REMOTE_SERVER	= 0x10;

	public static final int CLSCTX_INPROC_HANDLER16	= 0x20;
	public static final int CLSCTX_RESERVED1	= 0x40;
	public static final int CLSCTX_RESERVED2	= 0x80;
	public static final int CLSCTX_RESERVED3	= 0x100;
	public static final int CLSCTX_RESERVED4	= 0x200;
	public static final int CLSCTX_NO_CODE_DOWNLOAD	= 0x400;
	public static final int CLSCTX_RESERVED5	= 0x800;
	public static final int CLSCTX_NO_CUSTOM_MARSHAL	= 0x1000;
	public static final int CLSCTX_ENABLE_CODE_DOWNLOAD	= 0x2000;
	public static final int CLSCTX_NO_FAILURE_LOG	= 0x4000;
	public static final int CLSCTX_DISABLE_AAA	= 0x8000;
	public static final int CLSCTX_ENABLE_AAA	= 0x10000;
	public static final int CLSCTX_FROM_DEFAULT_CONTEXT	= 0x20000;
    //} 	CLSCTX;
	public static final int  CLSCTX_INPROC           = (CLSCTX_INPROC_SERVER|CLSCTX_INPROC_HANDLER);
	//	 With DCOM, CLSCTX_REMOTE_SERVER should be included
//	#if (_WIN32_WINNT >= 0x0400 ) || defined(_WIN32_DCOM) // DCOM
	public static final int  CLSCTX_ALL 			 = (CLSCTX_INPROC_SERVER|
						                                 CLSCTX_INPROC_HANDLER|
						                                 CLSCTX_LOCAL_SERVER|
						                                 CLSCTX_REMOTE_SERVER);
	public static final int  CLSCTX_SERVER           = (CLSCTX_INPROC_SERVER|CLSCTX_LOCAL_SERVER|CLSCTX_REMOTE_SERVER);
//	#else
//	public static final int  CLSCTX_ALL              (CLSCTX_INPROC_SERVER| \
//	                                 CLSCTX_INPROC_HANDLER| \
//	                                 CLSCTX_LOCAL_SERVER )
//
//	public static final int  CLSCTX_SERVER           (CLSCTX_INPROC_SERVER|CLSCTX_LOCAL_SERVER)
//	#endif
	public static native int CoCreateInstance(GUID rclsid, long pUnkOuter, int dwClsContext, GUID riid, long[] p);
	public static native void CoInitialize();	// TODO: result?

	public static native void CoUninitialize();	// TODO: result?

public static final boolean FAILED(int hr)
	{	return hr < 0;
	}

	public static final boolean SUCCEEDED(int hr)
	{	return hr >= 0;	// TODO: is this right, or must it be zero?
	}

}
