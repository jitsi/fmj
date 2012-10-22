package net.sf.fmj.media;

import javax.media.*;

/**
 * Interface that extends Owned and allows the owner to be changed.
 */

public interface Reparentable extends Owned
{
    void setOwner(Object newOwner);
}
