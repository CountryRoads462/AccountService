package account;

import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    EventRepository eventRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    HttpServletRequest request;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();

        User user = userRepo.findByUsername(username).orElse(null);

        if (user != null) {
            boolean userHasAdminRole = user.getUserGroups().stream()
                    .map(Group::getCode)
                    .anyMatch(code -> code.equals("ROLE_ADMINISTRATOR"));

            if (user.isAccountNonLocked()) {
                eventRepo.save(new Event(
                        EventName.LOGIN_FAILED,
                        username,
                        request.getRequestURI(),
                        request.getRequestURI()
                ));

                if (user.getFailedAttempt() < 4) {
                    user.increaseFailedAttempts();

                } else {
                    if (!userHasAdminRole) {
                        user.setAccountNonLocked(false);

                        eventRepo.save(new Event(
                                EventName.BRUTE_FORCE,
                                username,
                                request.getRequestURI(),
                                request.getRequestURI()
                        ));

                        eventRepo.save(new Event(
                                EventName.LOCK_USER,
                                username,
                                String.format("Lock user %s",
                                        username),
                                request.getRequestURI()
                        ));
                    }
                }

                userRepo.save(user);
            }

        } else {
            eventRepo.save(new Event(
                    EventName.LOGIN_FAILED,
                    username,
                    request.getRequestURI(),
                    request.getRequestURI()
            ));
        }
    }

}