package ua.com.kl.cmathtutor.shell.auth;

import java.util.Calendar;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import ua.com.kl.cmathtutor.domain.entity.User;
import ua.com.kl.cmathtutor.service.UserService;

@Component
public class AuthenticationState {

    @Autowired
    private UserService userService;
    @Getter
    @Setter
    private User authenticatedUser;

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
    }

    public boolean isAuthenticated() {
	return Objects.nonNull(authenticatedUser);
    }

    public boolean isAdminAuthenticated() {
	return isAuthenticated() && getAuthenticatedUser().isAdmin();
    }

}
