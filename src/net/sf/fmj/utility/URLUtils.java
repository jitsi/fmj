package net.sf.fmj.utility;

import java.io.*;

import com.lti.utils.*;

/**
 * URL utilities.
 *
 * @author Ken Larson
 *
 */
public final class URLUtils
{
    /** returns null if file does not exist. */
    public static String createAbsoluteFileUrl(String urlStr)
    {
        final String path = URLUtils.extractValidPathFromFileUrl(urlStr);
        if (path == null)
            return null;
        return URLUtils.createUrlStr(new File(path));

    }

    public static String createUrlStr(File file)
    {
        String path = file.getAbsolutePath();
        final String prefix;
        if (path.startsWith("/"))
            prefix = "file://";
        else
            prefix = "file:///"; // handles windows, where the path might might
                                 // be C:\myfile.txt, and what we need is
                                 // file:///C:\myfile.txt

        if (OSUtils.isWindows())
        {
            path = path.replaceAll("\\\\", "/");
        }

        return prefix + StringUtils.replaceSpecialUrlChars(path, true);
    }

    private static boolean exists(String path, boolean checkParentDirOnly)
    {
        final File f = new File(path);

        if (checkParentDirOnly)
            return f.getParentFile() == null || f.getParentFile().exists(); // parent
                                                                            // file
                                                                            // is
                                                                            // null
                                                                            // for
                                                                            // current
                                                                            // dir,
                                                                            // which
                                                                            // we
                                                                            // assume
                                                                            // exists.
        else
            return f.exists();

    }

    /**
     * same as extractValidPathFromFileUrl, but checks if parent dir is valid,
     * not file itself. Intended for new files about to be created.
     */
    public static String extractValidNewFilePathFromFileUrl(String url)
    {
        return extractValidPathFromFileUrl(url, true);

    }

    /**
     * Handles the various violations of the file URL format that are
     * commonplace. For example, file://home/ken/foo.txt really refers to the
     * relative path home/ken/foo.txt, not /home/ken/foo.txt. But we need to be
     * able to handle it. Assumes the local file system, checks for existance
     * for help.
     *
     * @param url
     * @return the file path, null if no valid path found.
     */
    public static String extractValidPathFromFileUrl(String url)
    {
        return extractValidPathFromFileUrl(url, false);
    }

    private static String extractValidPathFromFileUrl(String url,
            boolean checkParentDirOnly)
    {
        if (!url.startsWith("file:"))
            return null;
        String remainder = url.substring("file:".length());

        remainder = StringUtils.restoreSpecialURLChars(remainder);

        if (!remainder.startsWith("/"))
            return remainder;

        // first, try the exact value, as it should be
        if (remainder.startsWith("//"))
        {
            String result = remainder.substring(2);
            if (exists(result, checkParentDirOnly))
            {
                return windowsSafe(result);
            }
        }

        // just start pulling / of the front until we find something
        String result = remainder;

        // no need for two slashes on a real path:

        while (result.startsWith("//"))
            result = result.substring(1);

        if (exists(result, checkParentDirOnly))
            return windowsSafe(result);

        while (result.startsWith("/"))
        {
            result = result.substring(1);
            if (exists(result, checkParentDirOnly))
                return windowsSafe(result);
        }

        return null;

    }

    /**
     * Only call with paths known to exist. Handles the case where Java tells us
     * that /C:/myfile.txt exists.
     */
    private static String windowsSafe(String result)
    {
        if (OSUtils.isWindows() && result.startsWith("/"))
            return result.substring(1);
        else
            return result;
    }

    private URLUtils()
    {
        super();
    }

}
