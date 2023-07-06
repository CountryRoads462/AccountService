package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    UserRepository userRepo;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();

        User user = userRepo.findByUsername(username).get();

        if (user.getFailedAttempt() > 0) {
            user.resetFailedAttemptCounter();
            userRepo.save(user);
        }
    }

}