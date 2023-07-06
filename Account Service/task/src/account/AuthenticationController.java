package account;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

@RestController
public class AuthenticationController {

    @Autowired
    BreachedPasswordsRepository breachedPasswordsRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    GroupRepository groupRepo;

    @Autowired
    EventRepository eventRepo;

    @PostMapping(path = {
            "api/auth/signup/",
            "api/auth/signup"
    })
    @ResponseBody
    public UserResponseTransfer registerUser(
            @Valid @RequestBody User user,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String password = user.getPassword();
        if (breachedPasswordsRepo.existsByPassword(password)) {
            throw new BreachedPasswordException();
        }

        if (password.length() < 12) {
            throw new WrongPasswordSizeException();
        }

        String username = user.getUsername().toLowerCase(Locale.ROOT);
        user.setUsername(username);

        if (userRepo.existsByUsername(user.getUsername())) {
            throw new UserExistException();
        }

        user.setPassword(encoder.encode(user.getPassword()));

        updateUserGroup(user);
        userRepo.save(user);

        String subject = userDetails == null ? "Anonymous" : userDetails.getUsername();
        eventRepo.save(new Event(
                EventName.CREATE_USER,
                subject,
                username,
                "/api/auth/signup"
        ));


        return UserConverter.convert(user);
    }

    private void updateUserGroup(User user) {
        Group group = groupRepo.findByCode(
            userRepo.count() == 0 ? "ROLE_ADMINISTRATOR" : "ROLE_USER"
        ).get();
        user.addUserGroup(group);
    }

}