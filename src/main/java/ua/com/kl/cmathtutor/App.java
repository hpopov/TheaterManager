package ua.com.kl.cmathtutor;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    public static void main(String[] args) throws IOException {
	ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfiguraion.class);
	System.out.println(Arrays.asList(ctx.getBeanDefinitionNames()));
	System.out.println(ctx.getBean("auditoriums"));
	ctx.close();
    }

}
