#header()
package ${aib.getRootPackageName()};

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Configuration
@ComponentScan("${aib.getRootPackageName()}")
@SpringBootApplication
public class Application 
{
    public static void main(String[] args) 
    {
    	ApplicationContext context =
    	    	new ClassPathXmlApplicationContext(new String[] {"spring-bean.xml"});
    	
        SpringApplication.run(Application.class, args);
    }
    
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
