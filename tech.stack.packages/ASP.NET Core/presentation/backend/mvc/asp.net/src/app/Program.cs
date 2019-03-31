#set( $appName = $aib.getApplicationNameFormatted() )
#header()
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Hosting;

namespace ${appName}
{
    public class Program
    {
        public static void Main(string[] args)
        {
		    var config = new ConfigurationBuilder()
		        .SetBasePath(Directory.GetCurrentDirectory())
#if ( $container )		        
		        .AddJsonFile("hosting.json", true)
#endif		        
		        .Build();
		                
            var host = new WebHostBuilder()
                .UseKestrel()
                .UseContentRoot(Directory.GetCurrentDirectory())
                .UseIISIntegration()
                .UseStartup<Startup>()
                .UseApplicationInsights()
#if ( $container )
				.UseUrls("${container.getHost()}:${container.getPort()}") // remove if using hosting.json from above
#endif                
                .Build();

            host.Run();
        }
    }
}
