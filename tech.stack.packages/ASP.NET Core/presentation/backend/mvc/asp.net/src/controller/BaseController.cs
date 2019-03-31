#header()

#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $classObject.getName() )

/** 
 * Base class of all application service classes.
 *
 * @author $aib.getAuthor()
 */


using System;
using System.Collections;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

using ${appName}.Exceptions;
using ${appName}.Models;
using ${appName}.PrimaryKeys;

namespace ${appName}.Controllers
{
	public abstract class BaseController : Controller
	{	
		
		protected long convertToLong( string strValue )
		{ 
			long retVal = 0;
			
			if ( strValue != null )
			{
				try
				{
					retVal = Convert.ToInt64(strValue);
				}
				catch( Exception exc )
				{
					getLogger().LogError( "Failed to convert " + strValue + " to a long " + exc.ToString() );
				}
			}			
			return( retVal );
				
		}
		
		protected void signalBadRequest()
		{}
		
		abstract protected ILogger getLogger();
	}
}



