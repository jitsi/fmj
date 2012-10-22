package net.sf.fmj.media.control;

import java.awt.*;

import javax.media.*;

public class StringControlAdapter extends AtomicControlAdapter implements
        StringControl
{
    String value;
    String title;

    public StringControlAdapter()
    {
        super(null, true, null);
    }

    public StringControlAdapter(Component c, boolean def, Control parent)
    {
        super(c, def, parent);
    }

    public String getTitle()
    {
        return title;
    }

    public String getValue()
    {
        return value;
    }

    public String setTitle(String title)
    {
        this.title = title;
        informListeners();
        return title;
    }

    public String setValue(String value)
    {
        this.value = value;
        informListeners();
        return value;
    }
}
