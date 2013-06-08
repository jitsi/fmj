package net.sf.fmj.media;

import java.util.*;

/**
 * A table for holding mime type/file extension mappings.
 *
 * @author Ken Larson
 *
 */
public class MimeTable
{
    private final Hashtable<String, String> hashTable = new Hashtable<String, String>();
    private static final Hashtable<String, String> reverseHashTable = new Hashtable<String, String>();

    public boolean addMimeType(String fileExtension, String mimeType)
    {
        hashTable.put(fileExtension, mimeType);
        reverseHashTable.put(mimeType, fileExtension);
        return true;
    }

    public void clear()
    {
        hashTable.clear();
        reverseHashTable.clear();
    }

    public String getDefaultExtension(String mimeType)
    {
        return reverseHashTable.get(mimeType);

    }

    public List<String> getExtensions(String mimeType)
    {
        final List<String> result = new ArrayList<String>();
        final Iterator<String> i = hashTable.keySet().iterator();
        while (i.hasNext())
        {
            String k = i.next();
            if (hashTable.get(k).equals(mimeType))
                result.add(k);
        }
        return result;

    }

    public Hashtable<String,String> getMimeTable()
    {
        Hashtable<String,String> result = new Hashtable<String,String>();
        result.putAll(hashTable);
        return result;
    }

    public String getMimeType(String fileExtension)
    {
        return hashTable.get(fileExtension);
    }

    public Set<String> getMimeTypes()
    {
        final Set<String> result = new HashSet<String>();
        final Iterator<String> i = hashTable.values().iterator();
        while (i.hasNext())
        {
            result.add(i.next());
        }
        return result;
    }

    public boolean removeMimeType(String fileExtension)
    {
        if (hashTable.get(fileExtension) == null)
            return false;
        reverseHashTable.remove(hashTable.get(fileExtension));
        hashTable.remove(fileExtension);
        return true;

    }

}
