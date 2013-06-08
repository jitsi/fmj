package net.sf.fmj.registry;

import java.util.*;

import javax.media.*;

import net.sf.fmj.media.*;

/**
 * The contents of the registry.
 *
 * @author Warren Bloomer
 * @author Ken Larson
 */
class RegistryContents
{
    /** Lists of Plugin for each category */
    @SuppressWarnings("unchecked")
    Vector<String>[] plugins
        = new Vector[]
                {
                    new Vector<String>(),
                    new Vector<String>(),
                    new Vector<String>(),
                    new Vector<String>(),
                    new Vector<String>(),
                };

    /** a List of protocol prefixes */
    Vector<String> protocolPrefixList = new Vector<String>();

    /** a list of content prefixes */
    Vector<String> contentPrefixList = new Vector<String>();

    /** the MIME-type map. It maps file extensions to mime-types. */
    // Hashtable mimetypeMap = new Hashtable();

    /** MIME type map */
    final MimeTable mimeTable = new MimeTable();

    /** a List of protocol prefixes */
    Vector<CaptureDeviceInfo> captureDeviceInfoList
        = new Vector<CaptureDeviceInfo>();
}
