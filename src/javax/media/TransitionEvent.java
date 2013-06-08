package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/TransitionEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class TransitionEvent extends ControllerEvent
{
    int previousState;
    int currentState;
    int targetState;

    public TransitionEvent(Controller from, int previousState,
            int currentState, int targetState)
    {
        super(from);
        this.previousState = previousState;
        this.currentState = currentState;
        this.targetState = targetState;
    }

    public int getCurrentState()
    {
        return currentState;
    }

    public int getPreviousState()
    {
        return previousState;
    }

    public int getTargetState()
    {
        return targetState;
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "[source=" + getSource()
                + ",previousState=" + previousState + ",currentState="
                + currentState + ",targetState=" + targetState + "]";
    }
}
