package ua.com.kl.cmathtutor.shell.command.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.service.UserService;
import ua.com.kl.cmathtutor.shell.command.ExceptionWrapperUtils;

@Component
public class ViewUsersCommands implements CommandMarker {

    @Autowired
    private UserService userService;

    @CliAvailabilityIndicator({ "user all", "user by-id", "user by-email" })
    public boolean isUserCommandsAvailable() {
        return true;
    }

    @CliCommand(value = "user all", help = "View list of all registered users")
    public String getAllUsers() {
        return "Currently registered users are:" + OsUtils.LINE_SEPARATOR +
                userService.getAll().stream().map(user -> user.getEmail() + "[" + user.getId() + "]")
                        .map(ustr -> ustr + OsUtils.LINE_SEPARATOR).collect(StringBuilder::new,
                                StringBuilder::append, StringBuilder::append);
    }

    @CliCommand(value = "user by-id", help = "View user by id")
    public String getUserById(
            @CliOption(key = {
                    "id" },
                mandatory = true,
                help = "User id (is shown in square brackets invoking 'user all' command)") final int id
    ) {
        return ExceptionWrapperUtils.handleException(() -> userService.getById(id).toString());
    }

    @CliCommand(value = "user by-email", help = "View user by email")
    public String getUserByEmail(
            @CliOption(key = {
                    "email" },
                mandatory = true, help = "User email (is used as login)") final String email
    )
            throws NotFoundException {
        return ExceptionWrapperUtils.handleException(() -> userService.getByEmail(email).toString());
    }
}
