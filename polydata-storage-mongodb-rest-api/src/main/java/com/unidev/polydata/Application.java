package com.unidev.polydata;

import com.unidev.platform.j2ee.common.WebUtils;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.jminix.console.servlet.MiniConsoleServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class Application implements ServletContextInitializer {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public WebUtils webUtils() {
    return new WebUtils();
  }

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    servletContext.addServlet("JmxMiniConsoleServlet", MiniConsoleServlet.class)
        .addMapping("/jmx/*");
  }

  @Bean
  public Docket apiDocs() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
  }

}

