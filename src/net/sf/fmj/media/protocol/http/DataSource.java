package net.sf.fmj.media.protocol.http;

import java.net.*;

import net.sf.fmj.media.protocol.*;

/**
 * HTTP protocol handler DataSource. TODO: move http-specific code from
 * URLDataSource to here.
 *
 * @author Ken Larson
 *
 */
public class DataSource extends URLDataSource
{
    public DataSource()
    {
        super();
    }

    public DataSource(URL url)
    {
        super(url);
    }

}
