package ua.com.kl.cmathtutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.shell.Bootstrap;

public class App {

    public static void main(String[] args) throws IOException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(args));
        argsList.add("--disableInternalCommands");
        String[] argsArray = argsList.toArray(new String[0]);
        Bootstrap.main(argsArray);
    }
}
