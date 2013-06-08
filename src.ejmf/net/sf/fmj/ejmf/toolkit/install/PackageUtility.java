package net.sf.fmj.ejmf.toolkit.install;

import java.util.*;

import javax.media.*;

/**
 * The PackageUtility class provides methods to register or remove package
 * prefixes from the Java Media Framework's persistant lists of protocol and
 * content prefix lists.
 *
 * From the book: Essential JMF, Gordon, Talley (ISBN 0130801046). Used with
 * permission.
 *
 * @author Steve Talley & Rob Gordon
 */
public class PackageUtility
{
    /**
     * Adds a content prefix to the JMF content prefix list. If the content
     * prefix already exists in the list, no action is taken. The new content
     * prefix list will only be valid during the current session.
     *
     * @param prefix
     *            the content prefix to add
     */
    public static void addContentPrefix(String prefix)
    {
        addContentPrefix(prefix, false);
    }

    /**
     * Adds a content prefix to the JMF content prefix list. If the content
     * prefix already exists in the list, no action is taken. If commit is true,
     * the list will be commited and become persistent over future JMF sessions.
     * Otherwise, the new content prefix list will only be valid during the
     * current session.
     *
     * @param prefix
     *            the content prefix to add
     * @param commit
     *            boolean indicating whether to make new content list persistent
     */
    public static void addContentPrefix(String prefix, boolean commit)
    {
        Vector packagePrefix = PackageManager.getContentPrefixList();

        if (!packagePrefix.contains(prefix))
        {
            packagePrefix.addElement(prefix);
            PackageManager.setContentPrefixList(packagePrefix);
            if (commit)
            {
                PackageManager.commitContentPrefixList();
            }
        }
    }

    /**
     * Adds a protocol prefix to the JMF protocol prefix list. If the protocol
     * prefix already exists in the list, no action is taken. The new protocol
     * prefix list will only be valid during the current session.
     *
     * @param prefix
     *            the protocol prefix to add
     */
    public static void addProtocolPrefix(String prefix)
    {
        addProtocolPrefix(prefix, false);
    }

    /**
     * Adds a protocol prefix to the JMF protocol prefix list. If the protocol
     * prefix already exists in the list, no action is taken. If commit is true,
     * the list will be commited and become persistent over future JMF sessions.
     * Otherwise, the new protocol prefix list will only be valid during the
     * current session.
     *
     * @param prefix
     *            the protocol prefix to add
     * @param commit
     *            boolean indicating whether to make new protocol list
     *            persistent
     */
    public static void addProtocolPrefix(String prefix, boolean commit)
    {
        Vector packagePrefix = PackageManager.getProtocolPrefixList();

        if (!packagePrefix.contains(prefix))
        {
            packagePrefix.addElement(prefix);
            PackageManager.setProtocolPrefixList(packagePrefix);
            if (commit)
            {
                PackageManager.commitProtocolPrefixList();
            }
        }
    }

    /**
     * Removes a content prefix from the JMF content prefix list. If the content
     * prefix does not exist in the list, no action is taken. The new content
     * prefix list will only be valid during the current session.
     *
     * @param prefix
     *            the content prefix to remove
     */
    public static void removeContentPrefix(String prefix)
    {
        removeContentPrefix(prefix, false);
    }

    /**
     * Removes a content prefix from the JMF content prefix list. If the content
     * prefix does not exist in the list, no action is taken. If commit is true,
     * the list will be commited and become persistent over future JMF sessions.
     * Otherwise, the new content prefix list will only be valid during the
     * current session.
     *
     * @param prefix
     *            the content prefix to remove
     * @param commit
     *            boolean indicating whether to make new content list persistent
     */
    public static void removeContentPrefix(String prefix, boolean commit)
    {
        Vector packagePrefix = PackageManager.getContentPrefixList();

        if (packagePrefix.contains(prefix))
        {
            packagePrefix.removeElement(prefix);
            PackageManager.setContentPrefixList(packagePrefix);
            if (commit)
            {
                PackageManager.commitContentPrefixList();
            }
        }
    }

    /**
     * Removes a protocol prefix from the JMF protocol prefix list. If the
     * protocol prefix does not exist in the list, no action is taken. The new
     * protocol prefix list will only be valid during the current session.
     *
     * @param prefix
     *            the protocol prefix to remove
     */
    public static void removeProtocolPrefix(String prefix)
    {
        removeProtocolPrefix(prefix, false);
    }

    /**
     * Removes a protocol prefix from the JMF protocol prefix list. If the
     * protocol prefix does not exist in the list, no action is taken. If commit
     * is true, the list will be commited and become persistent over future JMF
     * sessions. Otherwise, the new protocol prefix list will only be valid
     * during the current session.
     *
     * @param prefix
     *            the protocol prefix to remove
     * @param commit
     *            boolean indicating whether to make new protocol list
     *            persistent
     */
    public static void removeProtocolPrefix(String prefix, boolean commit)
    {
        Vector packagePrefix = PackageManager.getProtocolPrefixList();

        if (packagePrefix.contains(prefix))
        {
            packagePrefix.removeElement(prefix);
            PackageManager.setProtocolPrefixList(packagePrefix);
            if (commit)
            {
                PackageManager.commitProtocolPrefixList();
            }
        }
    }
}
