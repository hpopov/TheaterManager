package ua.com.kl.cmathtutor.shell.auth;

import java.util.Date;

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
public class AuthenticationCommands implements CommandMarker {

    @Autowired
    private AuthenticationState authenticationState;
    @Autowired
    private UserService userService;

    @CliAvailabilityIndicator({ "auth log-out" })
    public boolean isLogoutAvailable() {
	return authenticationState.isAuthenticated();
    }

    @CliAvailabilityIndicator({ "auth current" })
    public boolean isCurrentUserAvailable() {
	return authenticationState.isAuthenticated();
    }

    @CliAvailabilityIndicator({ "auth sign-up", "auth sign-in" })
    public boolean isSignAvailable() {
	return !authenticationState.isAuthenticated();
    }

    @CliCommand(value = "auth sign-up", help = "Register new user")
    public String signUp(
	    @CliOption(key = { "name" }, mandatory = false, help = "First name of the user") final String firstName,
	    @CliOption(key = { "surname" }, mandatory = false, help = "Last name of the user") final String lastName,
	    @CliOption(key = { "email" }, mandatory = true, help = "User email (is used as login)") final String email,
	    @CliOption(key = { "password" }, mandatory = true, help = "User password") final String password,
	    @CliOption(key = {
		    "birthday" }, mandatory = true, help = "User birth date in dd-mm-yyyy format") final Date birthdayDate) {
	User registeredUser = userService.create(User.builder()
		.birthdayDate(birthdayDate)
		.email(email)
		.firstName(firstName)
		.lastName(lastName)
		.password(password)
		.build());
	return String.format("User with email %s was registered[id=%s]", registeredUser.getEmail(),
		registeredUser.getId());
    }

    @CliCommand(value = "auth sign-in", help = "Sign user in")
    public String signIn(
	    @CliOption(key = { "email" }, mandatory = true, help = "User email (is used as login)") final String email,
	    @CliOption(key = { "password" }, mandatory = true, help = "User password") final String password) {
	User user;
	try {
	    user = userService.getByEmail(email);
	} catch (NotFoundException e) {
	    return e.getMessage();
	}
	if (password.equals(user.getPassword())) {
	    authenticationState.setAuthenticatedUser(user);
	    return "User with email " + email + " logged in successfully.";
	}
	return "Incorrect password for user with email " + email + ".";
    }

    @CliCommand(value = "auth log-out", help = "Log user out")
    public String logOut() {
	String email = authenticationState.getAuthenticatedUser().getEmail();
	authenticationState.setAuthenticatedUser(null);
	return "User with email " + email + " logged out successfully.";
    }

    @CliCommand(value = "auth current", help = "View currently authenticated user")
    public User getCurrentUser() {
	return authenticationState.getAuthenticatedUser();
    }
}
