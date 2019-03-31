#header()
#set( $appName = $aib.getApplicationNameFormatted() )
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;

namespace ${appName}
{
	public class ApplicationLogger
	{
		private static ILoggerFactory _Factory = null;
	
		public static void ConfigureLogger(ILoggerFactory factory)
		{
			factory.AddDebug(LogLevel.None).AddConsole();
		}
	
		public static ILoggerFactory LoggerFactory
		{
			get
			{
				if (_Factory == null)
				{
					_Factory = new LoggerFactory();
					ConfigureLogger(_Factory);
				}
				return _Factory;
			}
			set { _Factory = value; }
		}
	}    			
}	
