package net.sf.fmj.apps.mediaserver;

import java.util.logging.*;

import net.sf.fmj.utility.*;

/**
 * FmjMediaServer main entry point.
 *
 * @author Ken Larson
 *
 */
public class FmjMediaServer
{
    private static final Logger logger = LoggerSingleton.logger;

    public static void main(String[] args) throws Exception
    {
        int port = 8090;

        FmjStartup.init(); // initialize default FMJ/JMF/logging

        logger.info("Starting FMJ Media Server on port: " + port);
        new MediaHTTPD(port);

        while (true)
        {
            Thread.sleep(10000);
        }
    }
}
