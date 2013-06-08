package net.sf.fmj.media.content.merge;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.utility.*;

/**
 * MediaProxy Handler for merge protocol/content type, allowing multiple merged
 * data sources to be specified with a single URL. Creates a merging datasource
 * from all of the component datasources that correspond to the (multiple) URLs
 * embedded within the URL. The separator characters used to separate the
 * embedded URLs are arbitrary, and are determined by looking at the first and
 * last character in the remainder. This allows plenty of choices for avoiding
 * conflicts with characters within the embedded URLs. Examples:
 * merge:[civil:/0][javasound://0] merge:{civil:/0}{javasound://0}
 * merge:<civil:/0><javasound://0>
 *
 * @author Ken Larson
 *
 */
public class Handler implements MediaProxy
{
    private static final Logger logger = LoggerSingleton.logger;

    private net.sf.fmj.media.protocol.merge.DataSource source;

    public DataSource getDataSource() throws IOException, NoDataSourceException
    {
        try
        {
            final String remainder = source.getLocator().getRemainder();

            if (remainder.length() < 3)
                throw new NoDataSourceException(
                        "URL is too short to contain start char, end char, and at least 1 embedded URL");

            final String startComponent = "" + remainder.charAt(0);
            final String stopComponent = ""
                    + remainder.charAt(remainder.length() - 1);
            final String splitOn = "\\" + stopComponent + "\\" + startComponent; // TODO:
                                                                                 // how
                                                                                 // do
                                                                                 // we
                                                                                 // know
                                                                                 // if
                                                                                 // we
                                                                                 // need
                                                                                 // to
                                                                                 // escape
                                                                                 // these?

            final String[] urlComponents = remainder.substring(1,
                    remainder.length() - 1).split(splitOn);
            if (urlComponents.length == 0)
                throw new NoDataSourceException("No URLs embedded within URL: "
                        + source.getLocator());

            final DataSource[] dataSourceComponents = new DataSource[urlComponents.length];

            for (int i = 0; i < urlComponents.length; ++i)
            {
                final String url = urlComponents[i];
                dataSourceComponents[i] = Manager
                        .createDataSource(new MediaLocator(url));
            }

            try
            {
                return Manager.createMergingDataSource(dataSourceComponents);
            } catch (IncompatibleSourceException e)
            {
                logger.log(Level.WARNING, "" + e, e);
                throw new NoDataSourceException("" + e);
            }
        } catch (NoDataSourceException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw e;
        }
    }

    public void setSource(DataSource source) throws IOException,
            IncompatibleSourceException
    {
        if (!(source instanceof net.sf.fmj.media.protocol.merge.DataSource))
            throw new IncompatibleSourceException();

        this.source = (net.sf.fmj.media.protocol.merge.DataSource) source;
    }

}
