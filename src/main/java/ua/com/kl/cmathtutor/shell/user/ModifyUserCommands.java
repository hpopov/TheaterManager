package ua.com.kl.cmathtutor.shell.user;

import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.exception.NotFoundException;
import ua.com.kl.cmathtutor.service.UserService;
import ua.com.kl.cmathtutor.shell.auth.AuthenticationState;

@Component
public class ModifyUserCommands implements CommandMarker {

    @Autowired
    private AuthenticationState authenticationState;
    @Autowired
    private UserService userService;

    @CliAvailabilityIndicator({ "profile edit" })
    public boolean isModifyUserCommandsAvailable() {
	return authenticationState.isAuthenticated();
    }

    @CliCommand(value = "profile edit", help = "Update currently authenticated user data")
    public String signUp(
	    @CliOption(key = { "name" }, mandatory = false, help = "First name of the user") final String firstName,
	    @CliOption(key = { "surname" }, mandatory = false, help = "Last name of the user") final String lastName,
	    @CliOption(key = { "password" }, mandatory = false, help = "User password") final String password,
	    @CliOption(key = {
		    "birthday" }, mandatory = false, help = "User birth date in dd-mm-yyyy format") final Date birthdayDate)
	    throws NotFoundException {
	if (Objects.isNull(firstName) && Objects.isNull(lastName) && Objects.isNull(password)
		&& Objects.isNull(birthdayDate)) {
	    return "No fields to update was specified. Nothing to do...";
	}
	User user = authenticationState.getAuthenticatedUser();
	if (Objects.nonNull(firstName)) {
	    user.setFirstName(firstName);
	}
	if (Objects.nonNull(lastName)) {
	    user.setLastName(lastName);
	}
	if (Objects.nonNull(password)) {
	    user.setPassword(password);
	}
	if (Objects.nonNull(birthdayDate)) {
	    user.setBirthdayDate(birthdayDate);
	}
	user = userService.updateById(user.getId(), user);
	return "Currently authenticated user was updated:\r\n" + user;
    }
}
