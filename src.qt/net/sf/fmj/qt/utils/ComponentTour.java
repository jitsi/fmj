package net.sf.fmj.qt.utils;

import quicktime.QTException;
import quicktime.std.comp.ComponentDescription;
import quicktime.std.comp.ComponentIdentifier;
import quicktime.util.QTUtils;

public class ComponentTour
{
	  public static void main (String[  ] args) {
	      try {
	          QTSessionCheck.check( );
	          /* use this wildcard to show all components in QT
	          */
	          ComponentDescription wildcard =
	              new ComponentDescription( ); 
	          ComponentIdentifier ci = null;
	          while ( (ci = ComponentIdentifier.find(ci, wildcard)) != null) {
	              ComponentDescription cd = ci.getInfo( );
	              System.out.println (cd.getName( ) + 
	                                  " (" + 
	                                  QTUtils.fromOSType (cd.getType( )) +
	                                  "/" +
	                                  QTUtils.fromOSType (cd.getSubType( )) +
	                                  ") " + 
	                                  cd.getInformationString( ));
	          }
	          
	      } catch (QTException qte) {
	          qte.printStackTrace( );
	      }
	  }
}
