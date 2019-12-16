package ua.com.kl.cmathtutor.shell.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.service.UserService;

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
	return "Currently registered users are:\r\n" +
		userService.getAll().stream().map(user -> user.getEmail() + "[" + user.getId() + "]")
			.map(ustr -> ustr + "\r\n").collect(StringBuilder::new,
				StringBuilder::append, StringBuilder::append);
    }

    @CliCommand(value = "user by-id", help = "View user by id")
    public User getUserById(@CliOption(key = {
	    "id" }, mandatory = true, help = "User id (is shown in square brackets invoking 'user all' command)") final int id)
	    throws NotFoundException {
	return userService.getById(id);
    }

    @CliCommand(value = "user by-email", help = "View user by email")
    public User getUserByEmail(@CliOption(key = {
	    "email" }, mandatory = true, help = "User email (is used as login)") final String email)
	    throws NotFoundException {
	return userService.getByEmail(email);
    }
}
