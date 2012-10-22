package net.sf.fmj.media.rtp;

import java.net.*;

import javax.media.*;

public class RTPMediaLocator extends MediaLocator
{
    String address;
    String contentType;
    private boolean valid;
    public static final int PORT_UNDEFINED = -1;
    public static final int SSRC_UNDEFINED = 0;
    public static final int TTL_UNDEFINED = 1;
    int port;
    long ssrc;
    int ttl;

    public RTPMediaLocator(String locatorString) throws MalformedURLException
    {
        super(locatorString);
        address = "";
        contentType = "";
        valid = true;
        port = -1;
        ssrc = 0L;
        ttl = 1;
        parseLocator(locatorString);
    }

    public String getContentType()
    {
        return contentType;
    }

    public String getSessionAddress()
    {
        return address;
    }

    public int getSessionPort()
    {
        return port;
    }

    public long getSSRC()
    {
        return ssrc;
    }

    public int getTTL()
    {
        return ttl;
    }

    public boolean isValid()
    {
        return valid;
    }

    private void parseLocator(String locatorString)
            throws MalformedURLException
    {
        String remainder = getRemainder();
        int colonIndex = remainder.indexOf(":");
        int slashIndex = remainder.indexOf("/", 2);
        int nextcolonIndex = -1;
        int nextslashIndex = -1;
        if (colonIndex != -1)
            nextcolonIndex = remainder.indexOf(":", colonIndex + 1);
        if (slashIndex != -1)
            nextslashIndex = remainder.indexOf("/", slashIndex + 1);
        if (colonIndex != -1)
            address = remainder.substring(2, colonIndex);
        InetAddress Iaddr;
        try
        {
            Iaddr = InetAddress.getByName(address);
        } catch (UnknownHostException e)
        {
            throw new MalformedURLException(
                    "Valid RTP Session Address must be given");
        }
        if (colonIndex == -1 || slashIndex == -1)
            throw new MalformedURLException(
                    "RTP MediaLocator is Invalid. Must be of form rtp://addr:port/content/ttl");
        String portstr = "";
        if (nextcolonIndex == -1)
            portstr = remainder.substring(colonIndex + 1, slashIndex);
        else
            portstr = remainder.substring(colonIndex + 1, nextcolonIndex);
        try
        {
            Integer Iport = Integer.valueOf(portstr);
            port = Iport.intValue();
        } catch (NumberFormatException e)
        {
            throw new MalformedURLException(
                    "RTP MediaLocator Port must be a valid integer");
        }
        if (nextcolonIndex != -1)
        {
            String ssrcstr = remainder
                    .substring(nextcolonIndex + 1, slashIndex);
            try
            {
                Long Lssrcstr = Long.valueOf(ssrcstr);
                ssrc = Lssrcstr.longValue();
            } catch (NumberFormatException e)
            {
            }
        }
        if (slashIndex != -1)
        {
            if (nextslashIndex == -1)
                contentType = remainder.substring(slashIndex + 1,
                        remainder.length());
            else
                contentType = remainder.substring(slashIndex + 1,
                        nextslashIndex);
            if (!contentType.equals("audio") && !contentType.equals("video"))
                throw new MalformedURLException(
                        "Content Type in URL must be audio or video ");
            contentType = "rtp/" + contentType;
        }
        if (nextslashIndex != -1)
        {
            String ttlstr = remainder.substring(nextslashIndex + 1,
                    remainder.length());
            try
            {
                Integer Ittl = Integer.valueOf(ttlstr);
                ttl = Ittl.intValue();
            } catch (NumberFormatException e)
            {
            }
        }
    }
}
