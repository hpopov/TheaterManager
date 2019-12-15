package ua.com.kl.cmathtutor;

import java.io.IOException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    public static void main(String[] args) throws IOException {
	ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfiguraion.class);
	ctx.close();
    }

}
