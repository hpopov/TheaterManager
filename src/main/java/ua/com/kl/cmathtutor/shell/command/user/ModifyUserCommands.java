package ua.com.kl.cmathtutor.shell.command.user;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.service.UserService;
import ua.com.kl.cmathtutor.shell.command.ExceptionWrapperUtils;
import ua.com.kl.cmathtutor.shell.command.auth.AuthenticationState;

@Component
public class ModifyUserCommands implements CommandMarker {

    @Autowired
    private AuthenticationState authenticationState;
    @Autowired
    private UserService userService;

    @PostConstruct
    private void init() {
        userService.create(User.builder()
                .birthdayDate(Calendar.getInstance().getTime())
                .email("admin")
                .firstName("Administrator")
                .isAdmin(true)
                .lastName("Admin")
                .password("admin")
                .build());
        userService.create(User.builder()
                .birthdayDate(Calendar.getInstance().getTime())
                .email("tordek")
                .firstName("Hryhorii")
                .lastName("Popov")
                .password("tordek")
                .build());
    }

    @CliAvailabilityIndicator({ "profile edit" })
    public boolean isModifyUserCommandsAvailable() {
        return authenticationState.isAuthenticated();
    }

    @CliCommand(value = "profile edit",
        help = "Update currently authenticated user data [FOR AUTHENNTICATED USERS ONLY]")
    public String signUp(
            @CliOption(key = { "name" }, mandatory = false, help = "First name of the user") final String firstName,
            @CliOption(key = { "surname" }, mandatory = false, help = "Last name of the user") final String lastName,
            @CliOption(key = { "password" }, mandatory = false, help = "User password") final String password,
            @CliOption(key = {
                    "birthday" },
                mandatory = false,
                help = "User birth date in dd-mm-yyyy format") final Date birthdayDate
    ) {
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
        return ExceptionWrapperUtils.handleException(() -> {
            User updatedUser = userService.updateById(user.getId(), user);
            return "Currently authenticated user was updated:" + OsUtils.LINE_SEPARATOR + updatedUser;
        });
    }
}
