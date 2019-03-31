#header()
#set( $appName = $aib.getApplicationNameFormatted() )
using System;
using System.Collections;
using Microsoft.AspNetCore.Mvc;

/** 
 * Base class of all application service classes.
 *
 * @author $aib.getAuthor()
 */
namespace ${appName}.Controllers
{
	public class SharedController : Controller 
	{			
		/**
		 * redirect to rended the Shared MultiSelect view
		 */	
	    public IActionResult MultiSelect( string sourceUrl, string modelUrl, string roleName, string value, string text )
        {
            ViewData["sourceUrl"] 	= sourceUrl;
            ViewData["modelUrl"]	= modelUrl;
            ViewData["roleName"] 	= roleName;
        	ViewData["value"] 		= value;
        	ViewData["text"] 		= text;
        
            return PartialView("~/Views/Shared/MultiSelect.cshtml");
        }
	}
}



