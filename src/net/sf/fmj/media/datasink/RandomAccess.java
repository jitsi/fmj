package net.sf.fmj.media.datasink;

/**
 * This interface can be used to re-arrange media file chunks, if necessary.
 * This can be used for example to create a streamable file where the media
 * header is located at the beginning of the file.
 *
 */
public interface RandomAccess
{
    /**
     * Diable/Enable random access operations
     */
    void setEnabled(boolean t);

    /**
     * The chunk to be written is obtained by seeking 'inOffset' bytes and
     * reading 'numBytes' bytes Return false if any exception was caught, true
     * otherwise
     *
     * If offset is -1 and numBytes > 0, then set the file length to numBytes;
     * return true on success and false otherwise.
     *
     * Set both parameters to -1 to signal that there will be no more writes.
     */
    boolean write(long inOffset, int numBytes);
}
