package net.sf.fmj.media;

import java.util.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;

/**
 * Merges multiple {@link PushDataSource} that implement {@link CaptureDevice}.
 *
 * @author Ken Larson
 *
 */
public class MergingCaptureDevicePushDataSource extends MergingPushDataSource
        implements CaptureDevice
{
    public MergingCaptureDevicePushDataSource(List<PushDataSource> sources)
    {
        super(sources);
        for (DataSource source : sources)
        {
            if (!(source instanceof CaptureDevice))
                throw new IllegalArgumentException();
        }
    }

    public CaptureDeviceInfo getCaptureDeviceInfo()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public FormatControl[] getFormatControls()
    {
        final List<FormatControl> formatControls = new ArrayList<FormatControl>();
        for (DataSource source : sources)
        {
            for (FormatControl formatControl : ((CaptureDevice) source)
                    .getFormatControls())
                formatControls.add(formatControl);
        }
        return formatControls.toArray(new FormatControl[0]);
    }
}
