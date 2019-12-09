package ua.com.kl.cmathtutor;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    public static void main(String[] args) {
	ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfiguraion.class);
	ctx.close();
    }

}
