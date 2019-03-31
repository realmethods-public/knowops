#header()
package ${aib.getRootPackageName()}.controller.config;  

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Configuration 
@ComponentScan("${aib.getRootPackageName()}") 

public class AppConfig 
{  
	@Bean  
	public UrlBasedViewResolver urlBasedViewResolver() 
	{  
		UrlBasedViewResolver resolver = new UrlBasedViewResolver();  
		resolver.setPrefix("/jsp/");  
		resolver.setSuffix(".jsp");
		resolver.setCache(false);
		resolver.setViewClass(JstlView.class);  
		return resolver;  
	}
}


