package net.sf.fmj.ui.objeditor;

import java.awt.*;

/**
 * Generic interface for a control which edits an object.
 *
 * @author Ken Larson
 *
 */
public interface ObjEditor
{
    public Component getComponent();

    public Object getObject();

    public void setObjectAndUpdateControl(Object o);

    public boolean validateAndUpdateObj();

}
