#header()
#set( $appName = $aib.getApplicationNameFormatted() )
/** 
 * Base class of all application service classes.
 *
 * @author $aib.getAuthor()
 */

using System;
using System.Collections;
using Microsoft.AspNetCore.Mvc;

namespace ${appName}.Controllers
{
	public class HomeController : Controller 
	{			
		/**
		 * redirect to home page
		 */	
	    public IActionResult Index()
        {
            return View();
        }

		/**
		 * redirect to about page
		 */	
	    public IActionResult About()
        {
        	ViewData["Message"] = "${appName}";
            return View();
        }

		/**
		 * redirect to contact page
		 */	
	    public IActionResult Contact()
        {
            return View();
        }

		/**
		 * redirect to main app page
		 */	
	    public IActionResult RunApp()
        {
            return View("~/Views/Shared/AppPage.cshtml");
        }
	}
}



