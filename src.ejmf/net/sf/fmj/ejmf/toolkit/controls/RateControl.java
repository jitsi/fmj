package net.sf.fmj.ejmf.toolkit.controls;

import java.awt.*;

import javax.media.*;

/**
 * JMF control only: Provides a generic Control over a Controller's rate. The
 * graphical interface is a simple rate TextField. When the user hits enter, the
 * rate in the TextField will be set in the Player.
 *
 * From the book: Essential JMF, Gordon, Talley (ISBN 0130801046). Used with
 * permission.
 *
 * @author Steve Talley & Rob Gordon
 *
 */
public class RateControl implements Control
{
    private Controller controller;

    private Component controlComponent;

    /**
     * Construct a RateControl object for the given Controller.
     */
    public RateControl(Controller controller)
    {
        super();
        this.controller = controller;

    }

    /**
     * For implementation of the Control interface.
     *
     * @return the Control Component for this object.
     */
    public Component getControlComponent()
    {
        if (controlComponent == null)
            controlComponent = new RateControlComponent(controller);

        return controlComponent;
    }
}
