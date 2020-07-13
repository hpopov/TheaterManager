package ua.com.kl.cmathtutor.shell.command;

import ua.com.kl.cmathtutor.shell.ShellExecutable;

public class ExceptionWrapperUtils {

    public static String handleException(ShellExecutable shellExecutable) {
        try {
            return shellExecutable.execute();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
