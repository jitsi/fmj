package com.sun.media.controls;

import java.awt.*;

import javax.media.*;
import javax.media.control.*;

/**
 * TODO: Stub
 *
 * @author Ken Larson
 *
 */
public class H263Adapter implements H263Control
{
    private boolean advancedPrediction;
    private boolean arithmeticCoding;
    private boolean errorCompensation;
    private boolean pbFrames;
    private boolean unrestrictedVector;
    private int hrd_B;
    private int bppMaxKb;

    public H263Adapter(Codec owner, boolean prediction, boolean coding,
            boolean compensation, boolean frames, boolean vector, int hrd_b,
            int kb, boolean settable)
    {
        advancedPrediction = prediction;
        arithmeticCoding = coding;
        errorCompensation = compensation;
        pbFrames = frames;
        unrestrictedVector = vector;
        hrd_B = hrd_b;
        bppMaxKb = kb;
    }

    public boolean getAdvancedPrediction()
    {
        return advancedPrediction;
    }

    public boolean getArithmeticCoding()
    {
        return arithmeticCoding;
    }

    public int getBppMaxKb()
    {
        return bppMaxKb;
    }

    public Component getControlComponent()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean getErrorCompensation()
    {
        return errorCompensation;
    }

    public int getHRD_B()
    {
        return hrd_B;
    }

    public boolean getPBFrames()
    {
        return pbFrames;
    }

    public boolean getUnrestrictedVector()
    {
        return unrestrictedVector;
    }

    public boolean isAdvancedPredictionSupported()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean isArithmeticCodingSupported()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean isErrorCompensationSupported()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean isPBFramesSupported()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean isUnrestrictedVectorSupported()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean setAdvancedPrediction(boolean newAdvancedPredictionMode)
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean setArithmeticCoding(boolean newArithmeticCodingMode)
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean setErrorCompensation(boolean newtErrorCompensationMode)
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean setPBFrames(boolean newPBFramesMode)
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean setUnrestrictedVector(boolean newUnrestrictedVectorMode)
    {
        throw new UnsupportedOperationException(); // TODO
    }

}
