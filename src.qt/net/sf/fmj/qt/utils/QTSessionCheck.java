package net.sf.fmj.qt.utils;

import quicktime.QTException;
import quicktime.QTSession;

/**
 * Copied from QTJ Developer's Notebook, Adamson.
 * @author Ken Larson
 *
 */
public class QTSessionCheck
{
	private Thread shutdownHook;
	  private static QTSessionCheck instance;
	  private QTSessionCheck( ) throws QTException {
	      super( );
	      // init
	      QTSession.open( );
	      // create shutdown handler
	      shutdownHook = new Thread("QTSessionCheck Close Thread") {
	              public void run( ) {
	                  QTSession.close( );
	              }
	          };
	      //Runtime.getRuntime( ).addShutdownHook(shutdownHook);	// TODO: this is causing crashes.
	  }
	  private static QTSessionCheck getInstance( ) throws QTException {
	      if (instance == null)
	          instance = new QTSessionCheck( );
	      return instance;
	  }
	  
	  public static void check( ) throws QTException {
	      // gets instance.  if a new one needs to be created,
	      // it calls QTSession.open( ) and creates a shutdown hook
	      // to call QTSession.close( )
	      getInstance( );
	  }

}
