package ejmf.toolkit.gui.controls;

/** 
  * ProgressBar provides a generalized interface for a component
  * used to display the progression of time. 
  * <p>
  * NOTE: This interface will make it easy to slide JSlider into 
  * StandardControlPanel if performance ever improves to a 
  * point where it could run with a controller without causing
  * stutter.
  */

import javax.swing.event.ChangeListener;

public interface ProgressBar  {
	/**
	* Get current value of ProgressBar.
	* @return value of Slider
	*/
    public int		getValue();
	/**
	* Set current value of ProgressBar.
	* @param value new value of Slider
	*/
    public void		setValue(int value);

	/**
	* Get minimum legal value of ProgressBar.
	* @return minimum legal Slider value
	*/
    public int		getMinimum();
	/**
	* Set legal minimum value of ProgressBar.
	* @param legal minimum value of Slider
	*/
    public void		setMinimum(int value);
   
	/**
	* Get maximum legal value of ProgressBar.
	* @return maximum legal Slider value
	*/
    public int		getMaximum();
	/**
	* Set maximum legal value of ProgressBar.
	* @param value maximum legal value of Slider
	*/
    public void 	setMaximum(int value);

	/**
	* Register ChangeListener with ProgressBar
	*/
    public void		addChangeListener(ChangeListener l);
	/**
	* Remove object as ProgressBar ChangeListener 
	*/
    public void		removeChangeListener(ChangeListener l);
}
