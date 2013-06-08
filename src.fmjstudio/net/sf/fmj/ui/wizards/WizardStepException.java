package net.sf.fmj.ui.wizards;

/**
 * Exception with an (ideally) human-readable message, as a result of processing
 * a wizard step.
 *
 * @author Ken Larson
 *
 */
public class WizardStepException extends Exception
{
    public WizardStepException()
    {
        super();
    }

    public WizardStepException(String arg0)
    {
        super(arg0);
    }

    public WizardStepException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }

    public WizardStepException(Throwable arg0)
    {
        super(arg0);
    }

}
