package ua.com.kl.cmathtutor.shell.command.auth;

import java.util.Objects;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import ua.com.kl.cmathtutor.domain.entity.User;

@Component
public class AuthenticationState {

    @Getter
    @Setter
    private User authenticatedUser;

    public boolean isAuthenticated() {
        return Objects.nonNull(authenticatedUser);
    }

    public boolean isAdminAuthenticated() {
        return isAuthenticated() && getAuthenticatedUser().isAdmin();
    }
}
