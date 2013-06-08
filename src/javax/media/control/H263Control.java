package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/H263Control.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface H263Control extends Control
{
    public boolean getAdvancedPrediction();

    public boolean getArithmeticCoding();

    public int getBppMaxKb();

    public boolean getErrorCompensation();

    public int getHRD_B();

    public boolean getPBFrames();

    public boolean getUnrestrictedVector();

    public boolean isAdvancedPredictionSupported();

    public boolean isArithmeticCodingSupported();

    public boolean isErrorCompensationSupported();

    public boolean isPBFramesSupported();

    public boolean isUnrestrictedVectorSupported();

    public boolean setAdvancedPrediction(boolean newAdvancedPredictionMode);

    public boolean setArithmeticCoding(boolean newArithmeticCodingMode);

    public boolean setErrorCompensation(boolean newtErrorCompensationMode);

    public boolean setPBFrames(boolean newPBFramesMode);

    public boolean setUnrestrictedVector(boolean newUnrestrictedVectorMode);
}
