package account;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@RestController
public class UserSettingsController {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    BreachedPasswordsRepository breachedPasswordsRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    EventRepository eventRepo;

    @PostMapping(path = "/api/auth/changepass")
    public LinkedHashMap<String, String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePassRequestForm changePassRequestForm
    ) {
        String newPassword = changePassRequestForm.getNewPassword();

        if (newPassword.length() < 12) {
            throw new WrongPasswordSizeException();
        }

        if (breachedPasswordsRepo.existsByPassword(changePassRequestForm.getNewPassword())) {
            throw new BreachedPasswordException();
        }

        String oldPassword = userDetails.getPassword();

        if (passwordEncoder.matches(newPassword, oldPassword)) {
            throw new PasswordExistException();
        }

        String username = userDetails.getUsername();
        User user = userRepo.findByUsername(username).get();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        eventRepo.save(new Event(
                EventName.CHANGE_PASSWORD,
                username,
                username,
                "/api/auth/changepass"
        ));

        LinkedHashMap<String, String> responseForm = new LinkedHashMap<>();
        responseForm.put("email", user.getUsername());
        responseForm.put("status", "The password has been updated successfully");

        return responseForm;
    }
}