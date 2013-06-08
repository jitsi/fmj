package javax.media.protocol;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/RateConfigureable.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface RateConfigureable
{
    public RateConfiguration[] getRateConfigurations();

    public RateConfiguration setRateConfiguration(RateConfiguration config);
}
