package be.enyed.example;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;

@SpringBootApplication
public class LockerExampleApplication {
	
	@Bean
	public static ServletRegistrationBean dHtmlLayoutServlet() {
		Map<String, String> params = new HashMap<>();
		params.put("update-uri", "/zkau");

		DHtmlLayoutServlet dHtmlLayoutServlet = new DHtmlLayoutServlet();
		
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dHtmlLayoutServlet, "*.zul");
		servletRegistrationBean.setLoadOnStartup(1);
		servletRegistrationBean.setInitParameters(params);
		return servletRegistrationBean;
	}

	@Bean
	public static ServletRegistrationBean dHtmlUpdateServlet() {
		DHtmlUpdateServlet updateServlet = new DHtmlUpdateServlet();
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(updateServlet, "/zkau/*");
		return servletRegistrationBean;
	}
	
	@Bean
	public ViewResolver viewResolver() {		
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/pages/");		
		resolver.setSuffix(".zul");
		return resolver;
	}
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(LockerExampleApplication.class, args);
	}

}
