package net.sf.fmj.media.rtp;

import java.util.*;

public class RTPSourceInfoCache
{
    public SSRCCache ssrccache;
    Hashtable<String,RTPSourceInfo> cache;
    RTPSourceInfoCache main;

    public RTPSourceInfoCache()
    {
        cache = new Hashtable<String,RTPSourceInfo>(20);
    }

    public RTPSourceInfo get(String cname, boolean local)
    {
        RTPSourceInfo info;
        synchronized (this)
        {
            info = cache.get(cname);
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

    public Hashtable<String,RTPSourceInfo> getCacheTable()
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
