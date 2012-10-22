package net.sf.fmj.qt.media.content.unknown;

import net.sf.fmj.media.AbstractGainControl;
import quicktime.std.StdQTException;
import quicktime.std.movies.Movie;

/**
 * 
 * @author Ken Larson
 *
 */
public class QTGainControl extends AbstractGainControl
{
	private final Movie m;

	public QTGainControl(Movie m)
	{
		super();
		this.m = m;
	}


	public float getLevel()
	{
		if (getMute())
			return getSavedLevelDuringMute();
		try
		{
			final float result = m.getVolume();
			System.out.println("Result: " + result);
			return result;
		} catch (StdQTException e)
		{
			throw new RuntimeException(e);
		}
	}



	public float setLevel(float level)
	{
		System.out.println("new level: " + level);
		
		try
		{
			m.setVolume(level);
		} catch (StdQTException e)
		{
			throw new RuntimeException(e);
		}
		
		return getLevel();
	}


}
