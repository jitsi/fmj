package net.sf.fmj.media.util;

import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.format.*;

/**
 * This is a utility class to store supported formats in a table. This cannot be
 * an internal class since it has to be used in static methods.
 */
class FormatTable
{
    public Format keys[];
    public Format table[][];
    public int hits[];
    public int last;

    public FormatTable(int size)
    {
        keys = new Format[size];
        table = new Format[size][];
        hits = new int[size];
        last = 0;
    }

    /**
     * Find the least used entry in the table.
     */
    public int findLeastHit()
    {
        int min = hits[0];
        int idx = 0;
        for (int i = 1; i < last; i++)
        {
            if (hits[i] < min)
            {
                min = hits[i];
                idx = i;
            }
        }
        return idx;
    }

    /**
     * Given an input format, check the tables to for its supported formats.
     */
    Format[] get(Format input)
    {
        Format res[] = null;
        for (int i = 0; i < last; i++)
        {
            if (res == null && keys[i].matches(input))
            {
                res = table[i];
                hits[i] = keys.length;
                // System.err.println("found match");
            } else
                hits[i] = hits[i] - 1;
        }
        return res;
    }

    /**
     * Save the supported formats with a input key to the table.
     */
    public void save(Format input, Format supported[])
    {
        int idx;
        if (last >= keys.length)
        {
            idx = findLeastHit();
        } else
        {
            idx = last;
            last++;
        }
        keys[idx] = input;
        table[idx] = supported;
        hits[idx] = keys.length;
    }
}

/**
 * Class where objects can be registered with a unique key (string). The
 * Resource tries to find a ".fmj.resource" file in user.home. If it does, then
 * it reads all the keys and corresponding objects. The resource file is
 * generated and maintained solely by the MediaEngine. The structure of the
 * serialized file is: Number of items in the table: integer Version number :
 * integer Key for item 1 : UTF Value of item 1 : Object Key for item 2 : UTF
 * Value of item 2 : Object and so on......
 */
public class Resource
{
    // Hashtable that stores all the properties.
    private static Hashtable<String, Object> hash = null;

    // Name of the properties file, including path.
    private static String filename = null;

    // Version number of the serialized file format. Will need
    // to be incremented if the format changes so that an old
    // implementation will expect errors with a new format.
    private static final int versionNumber = 200;
    private static final String USERHOME = "user.home";
    private static String userhome = null;

    /**
     * Static code block to read in the resource file and initialize the hash
     * table.
     */
    static
    {
        hash = new Hashtable<String, Object>();

        boolean securityPrivelege = true;

        try
        {
            if (securityPrivelege)
                userhome = System.getProperty(USERHOME);
        } catch (Exception e)
        {
            userhome = null;
            securityPrivelege = false;
        }

        if (userhome == null)
        {
            securityPrivelege = false;
        }
        InputStream is = null;

        if (securityPrivelege)
        {
            is = findResourceFile();
            if (is == null)
            {
                securityPrivelege = false; // there is no access to .fmj.resources
            }
        }

        if (!readResource(is))
        {
            hash = new Hashtable<String, Object>();
        }
    }

    // We have 3 sets of tables, one for audio formats, one for
    // video formats, one for "other" formats. This will speed
    // up the search a little.
    static FormatTable audioFmtTbl;

    static FormatTable videoFmtTbl;

    static FormatTable miscFmtTbl;

    static Object fmtTblSync = new Object();

    static int AUDIO_TBL_SIZE = 40;

    static int VIDEO_TBL_SIZE = 20;

    static int MISC_TBL_SIZE = 10;

    static String AUDIO_SIZE_KEY = "ATS";

    static String AUDIO_INPUT_KEY = "AI.";

    static String AUDIO_FORMAT_KEY = "AF.";

    // ///////////////////////////////////////////////////////
    //
    // Static methods to get/save supported formats
    // to a runtime/static database. This is to speed up
    // the runtime for TrackControl.getSupportedFormats()
    // ///////////////////////////////////////////////////////

    static String AUDIO_HIT_KEY = "AH.";
    static String VIDEO_SIZE_KEY = "VTS";
    static String VIDEO_INPUT_KEY = "VI.";

    static String VIDEO_FORMAT_KEY = "VF.";

    static String VIDEO_HIT_KEY = "VH.";
    static String MISC_SIZE_KEY = "MTS";
    static String MISC_INPUT_KEY = "MI.";

    static String MISC_FORMAT_KEY = "MF.";
    static String MISC_HIT_KEY = "MH.";
    static boolean needSaving = false;

    /**
     * Writes all the properties in the hashtable to the .fmj.resources file. If
     * the file is non-existent or the writing failed for some reason, it throws
     * an IOException.
     */
    public static final synchronized boolean commit() throws IOException
    {
        /*
         * XXX The file .fmj.resource is not written because there is no code to
         * read it.
         */
/*
        if (filename == null)
            throw new IOException("Can't find resource file");

        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        int tableSize = hash.size();
        oos.writeInt(tableSize);
        oos.writeInt(versionNumber);
        for (Enumeration e = hash.keys(); e.hasMoreElements();)
        {
            String key = (String) e.nextElement();
            Object value = hash.get(key);

            oos.writeUTF(key); // write key as UTF chars.
            oos.writeObject(value);
            oos.flush();
        }
        oos.close();
*/
        return true;
    }

    /**
     * Remove the permanent registry.
     */
    public static final synchronized void destroy()
    {
        if (filename == null)
            return;
        try
        {
            File file = new File(filename);
            file.delete();
        } catch (Throwable t)
        {
            filename = null;
        }
    }

    private static final synchronized InputStream findResourceFile()
    {
        InputStream ris = null;

        if (userhome == null)
            return null;

        try
        {
            filename = userhome + File.separator + ".fmj.resource";
            // System.out.println("Resource: file name is " + filename);
            ris = getResourceStream(new File(filename));
        } catch (Throwable t)
        {
            filename = null;
            return null;
        }

        return ris;
    }

    /**
     * Returns the value corresponding to the specified key. Returns null if no
     * such property is found.
     */
    public static final synchronized Object get(String key)
    {
        return (key != null) ? hash.get(key) : null;
    }

    /**
     * Given an input format, check the tables to for its supported formats.
     */
    public static final Format[] getDB(Format input)
    {
        synchronized (fmtTblSync)
        {
            if (audioFmtTbl == null)
                initDB();

            if (input instanceof AudioFormat)
                return audioFmtTbl.get(input);
            else if (input instanceof VideoFormat)
                return videoFmtTbl.get(input);

            return miscFmtTbl.get(input);
        }
    }

    private static final FileInputStream getResourceStream(File file)
            throws IOException
    {
        try
        {
            if (!file.exists())
            {
                // System.out.println("file doesnt exist");
                return null;
            } else
            {
                return new FileInputStream(file.getPath());
            }
        } catch (Throwable t)
        {
            return null;
        }
    }

    /**
     * Initialize the supported format tables.
     */
    static final void initDB()
    {
        synchronized (fmtTblSync)
        {
            audioFmtTbl = new FormatTable(AUDIO_TBL_SIZE);
            videoFmtTbl = new FormatTable(VIDEO_TBL_SIZE);
            miscFmtTbl = new FormatTable(MISC_TBL_SIZE);
            loadDB();
        }
    }

    /**
     * Load the supported format table from the resource file.
     */
    private static final void loadDB()
    {
        synchronized (fmtTblSync)
        {
            int i, size;
            Object key, value, hit;

            key = Resource.get(AUDIO_SIZE_KEY);
            if (key instanceof Integer)
                size = ((Integer) key).intValue();
            else
                size = 0;
            if (size > AUDIO_TBL_SIZE)
            {
                // Something's wrong.
                System.err.println("Resource file is corrupted");
                size = AUDIO_TBL_SIZE;
            }
            // System.err.println("audio table size = " + size);
            audioFmtTbl.last = size;
            for (i = 0; i < size; i++)
            {
                key = Resource.get(AUDIO_INPUT_KEY + i);
                value = Resource.get(AUDIO_FORMAT_KEY + i);
                hit = Resource.get(AUDIO_HIT_KEY + i);
                if (key instanceof Format && value instanceof Format[]
                        && hit instanceof Integer)
                {
                    audioFmtTbl.keys[i] = (Format) key;
                    audioFmtTbl.table[i] = (Format[]) value;
                    audioFmtTbl.hits[i] = ((Integer) hit).intValue();
                } else
                {
                    System.err.println("Resource file is corrupted");
                    audioFmtTbl.last = 0;
                    break;
                }
            }

            key = Resource.get(VIDEO_SIZE_KEY);
            if (key instanceof Integer)
                size = ((Integer) key).intValue();
            else
                size = 0;
            if (size > VIDEO_TBL_SIZE)
            {
                // Something's wrong.
                System.err.println("Resource file is corrupted");
                size = VIDEO_TBL_SIZE;
            }
            // System.err.println("video table size = " + size);
            videoFmtTbl.last = size;
            for (i = 0; i < size; i++)
            {
                key = Resource.get(VIDEO_INPUT_KEY + i);
                value = Resource.get(VIDEO_FORMAT_KEY + i);
                hit = Resource.get(VIDEO_HIT_KEY + i);
                if (key instanceof Format && value instanceof Format[]
                        && hit instanceof Integer)
                {
                    videoFmtTbl.keys[i] = (Format) key;
                    videoFmtTbl.table[i] = (Format[]) value;
                    videoFmtTbl.hits[i] = ((Integer) hit).intValue();
                } else
                {
                    System.err.println("Resource file is corrupted");
                    videoFmtTbl.last = 0;
                    break;
                }
            }

            key = Resource.get(MISC_SIZE_KEY);
            if (key instanceof Integer)
                size = ((Integer) key).intValue();
            else
                size = 0;
            if (size > MISC_TBL_SIZE)
            {
                // Something's wrong.
                System.err.println("Resource file is corrupted");
                size = MISC_TBL_SIZE;
            }
            // System.err.println("misc table size = " + size);
            miscFmtTbl.last = size;
            for (i = 0; i < size; i++)
            {
                key = Resource.get(MISC_INPUT_KEY + i);
                value = Resource.get(MISC_FORMAT_KEY + i);
                hit = Resource.get(MISC_HIT_KEY + i);
                if (key instanceof Format && value instanceof Format[]
                        && hit instanceof Integer)
                {
                    miscFmtTbl.keys[i] = (Format) key;
                    miscFmtTbl.table[i] = (Format[]) value;
                    miscFmtTbl.hits[i] = ((Integer) hit).intValue();
                } else
                {
                    System.err.println("Resource file is corrupted");
                    miscFmtTbl.last = 0;
                    break;
                }
            }

        } // synchronized
    }

    /**
     * Reset (destroy) the supported format tables.
     */
    public static final void purgeDB()
    {
        synchronized (fmtTblSync)
        {
            if (audioFmtTbl == null)
                return;
            audioFmtTbl = new FormatTable(AUDIO_TBL_SIZE);
            videoFmtTbl = new FormatTable(VIDEO_TBL_SIZE);
            miscFmtTbl = new FormatTable(MISC_TBL_SIZE);
        }
    }

    /**
     * Save an input format and it's supported formats to the table.
     */
    public static final Format[] putDB(Format input, Format supported[])
    {
        synchronized (fmtTblSync)
        {
            Format in = input.relax();
            Format list[] = new Format[supported.length];
            for (int i = 0; i < supported.length; i++)
                list[i] = supported[i].relax();

            if (in instanceof AudioFormat)
                audioFmtTbl.save(in, list);
            else if (in instanceof VideoFormat)
                videoFmtTbl.save(in, list);
            else
                miscFmtTbl.save(in, list);

            needSaving = true;

            return list;
        }
    }

    private static final synchronized boolean readResource(InputStream ris)
    {
        if (ris == null)
            return false;

        try
        {
            // Inner class with skipHeader so that the protected method
            // readStreamHeader can be called.
            ObjectInputStream ois = new ObjectInputStream(ris);

            int tableSize = ois.readInt();
            int version = ois.readInt();
            if (version > 200)
            {
                System.err.println("Version number mismatch.\nThere could be"
                        + " errors in reading the resource");
            }
            hash = new Hashtable<String, Object>();
            for (int i = 0; i < tableSize; i++)
            {
                String key = ois.readUTF();
                boolean failed = false;
                try
                {
                    Object value = ois.readObject();
                    hash.put(key, value);
                } catch (ClassNotFoundException cnfe)
                {
                    failed = true;
                } catch (OptionalDataException ode)
                {
                    failed = true;
                }
            }
            ois.close();
            ris.close();
        } catch (IOException ioe)
        {
            System.err.println("IOException in readResource: " + ioe);
            return false;
        } catch (Throwable t)
        {
            return false;
        }

        return true;
    }

    /**
     * Removes a property from the hashtable. Returns false if the property was
     * not found.
     */
    public static final synchronized boolean remove(String key)
    {
        if (key != null)
        {
            if (hash.containsKey(key))
            {
                hash.remove(key);
                return true;
            }
        }

        return false;
    }

    /**
     * Removes an entire set of properties with the keys starting with the value
     * "keyStart".
     */
    public static final synchronized void removeGroup(String keyStart)
    {
        Vector<String> keys = new Vector<String>();
        if (keyStart != null)
        {
            Enumeration<String> e = hash.keys();
            while (e.hasMoreElements())
            {
                String key = e.nextElement();
                if (key.startsWith(keyStart))
                    keys.addElement(key);
            }
        }

        for (int i = 0; i < keys.size(); i++)
        {
            hash.remove(keys.elementAt(i));
        }
    }

    public static final synchronized void reset()
    {
        hash = new Hashtable<String, Object>();
    }

    /**
     * Save the table of supported formats to the resource file.
     */
    public static final void saveDB()
    {
        synchronized (fmtTblSync)
        {
            if (!needSaving)
                return;

            Resource.reset();

            int i;
            Resource.set(AUDIO_SIZE_KEY, new Integer(audioFmtTbl.last));
            for (i = 0; i < audioFmtTbl.last; i++)
            {
                Resource.set(AUDIO_INPUT_KEY + i, audioFmtTbl.keys[i]);
                Resource.set(AUDIO_FORMAT_KEY + i, audioFmtTbl.table[i]);
                Resource.set(AUDIO_HIT_KEY + i,
                        new Integer(audioFmtTbl.hits[i]));
            }
            Resource.set(VIDEO_SIZE_KEY, new Integer(videoFmtTbl.last));
            for (i = 0; i < videoFmtTbl.last; i++)
            {
                Resource.set(VIDEO_INPUT_KEY + i, videoFmtTbl.keys[i]);
                Resource.set(VIDEO_FORMAT_KEY + i, videoFmtTbl.table[i]);
                Resource.set(VIDEO_HIT_KEY + i,
                        new Integer(videoFmtTbl.hits[i]));
            }
            Resource.set(MISC_SIZE_KEY, new Integer(miscFmtTbl.last));
            for (i = 0; i < miscFmtTbl.last; i++)
            {
                Resource.set(MISC_INPUT_KEY + i, miscFmtTbl.keys[i]);
                Resource.set(MISC_FORMAT_KEY + i, miscFmtTbl.table[i]);
                Resource.set(MISC_HIT_KEY + i, new Integer(miscFmtTbl.hits[i]));
            }

            try
            {
                Resource.commit();
            } catch (Throwable e)
            {
                // System.err.println("Cannot save resource file: " + e);
            }

            needSaving = false;

        } // synchronized
    }

    /**
     * Add or modify a property. The key and the value should be non-null.
     * Returns false if it couldn't add/modify the property.
     */
    public static final synchronized boolean set(String key, Object value)
    {
        if (key != null && value != null)
        {
            hash.put(key, value);
            return true;
        } else
            return false;
    }
}
