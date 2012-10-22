package net.sf.fmj.media.rtp;

import java.util.*;

public class RTPSourceInfoCache
{
    public SSRCCache ssrccache;
    Hashtable cache;
    RTPSourceInfoCache main;

    public RTPSourceInfoCache()
    {
        cache = new Hashtable(20);
    }

    public RTPSourceInfo get(String cname, boolean local)
    {
        RTPSourceInfo info = null;
        synchronized (this)
        {
            info = (RTPSourceInfo) cache.get(cname);
            if (info == null && !local)
            {
                info = new RTPRemoteSourceInfo(cname, main);
                cache.put(cname, info);
            }
            if (info == null && local)
            {
                info = new RTPLocalSourceInfo(cname, main);
                cache.put(cname, info);
            }
        }
        return info;
    }

    public Hashtable getCacheTable()
    {
        return cache;
    }

    public RTPSourceInfoCache getMainCache()
    {
        if (main == null)
            main = new RTPSourceInfoCache();
        return main;
    }

    public void remove(String cname)
    {
        cache.remove(cname);
    }

    public void setMainCache(RTPSourceInfoCache main)
    {
        this.main = main;
    }

    public void setSSRCCache(SSRCCache ssrccache)
    {
        main.ssrccache = ssrccache;
    }
}
