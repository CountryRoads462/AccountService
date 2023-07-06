package account;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class AdminFunctionalityController {

    @Autowired
    EventRepository eventRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    GroupRepository groupRepo;

    @GetMapping(path = "/api/admin/user/")
    public TreeSet<UserResponseTransfer> getUsers() {
        TreeSet<UserResponseTransfer> users = new TreeSet<>(new UserResponseTransferComparatorById());

        userRepo.findAll()
                .forEach(user -> {
                    users.add(UserConverter.convert(user));
                });

        return users;
    }

    @Transactional
    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping(path = {
            "api/admin/user/{username}",
            "api/admin/user/",
            "api/admin/user"
    })
    public LinkedHashMap<String, String> deleteUser(
            @PathVariable(required = false) String username,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String subject = userDetails.getUsername();
        LinkedHashMap<String, String> deleteUserResponseForm = new LinkedHashMap<>();

        if (userRepo.existsByUsername(username)) {
            if (userRepo.findByUsername(username)
                    .get()
                    .getUserGroups()
                    .stream()
                    .map(Group::getCode)
                    .anyMatch(code -> code.equals("ROLE_ADMINISTRATOR"))) {
                throw new CustomException(
                        "Can't remove ADMINISTRATOR role!",
                        HttpStatus.BAD_REQUEST
                );
            }

            userRepo.deleteByUsername(username);

            eventRepo.save(new Event(
                    EventName.DELETE_USER,
                    subject,
                    username,
                    "/api/admin/user"
            ));

            deleteUserResponseForm.put("user", username);
            deleteUserResponseForm.put("status", "Deleted successfully!");

            return deleteUserResponseForm;

        } else {
            throw new CustomException(
                    "User not found!",
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @PutMapping(path = "/api/admin/user/role")
    public UserResponseTransfer changeRole(
            @Valid @RequestBody ChangeRoleRequestForm changeRoleRequestForm,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String subject = userDetails.getUsername();

        String role = "ROLE_" + changeRoleRequestForm.getRole();
        String username = changeRoleRequestForm.getUser().toLowerCase(Locale.ROOT);

        User user = userRepo.findByUsername(username).orElse(null);

        if (user == null) {
            throw new CustomException(
                "User not found!",
                HttpStatus.NOT_FOUND
            );
        }

        Group group = groupRepo.findByCode(role).orElse(null);

        if (group == null) {
            throw new CustomException(
                    "Role not found!",
                    HttpStatus.NOT_FOUND
            );
        }

        if (changeRoleRequestForm.getOperation().equals("GRANT")) {
            Set<String> allRoles = new HashSet<>();
            allRoles.add(role);
            allRoles.addAll(user.getUserGroups().stream()
                    .map(Group::getCode)
                    .toList());

            if (allRoles.contains("ROLE_ADMINISTRATOR") &&
                    (allRoles.contains("ROLE_USER") ||
                            allRoles.contains("ROLE_ACCOUNTANT") ||
                            allRoles.contains("ROLE_AUDITOR"))
            ) {
                throw new CustomException(
                        "The user cannot combine administrative and business roles!",
                        HttpStatus.BAD_REQUEST
                );
            }

            user.addUserGroup(group);
            userRepo.save(user);

            eventRepo.save(new Event(
                    EventName.GRANT_ROLE,
                    subject,
                    String.format("Grant role %s to %s",
                            changeRoleRequestForm.getRole(),
                            username),
                    "/api/admin/user/role"
            ));

        } else {
            if (group.getCode().equals("ROLE_ADMINISTRATOR")) {
                throw new CustomException(
                        "Can't remove ADMINISTRATOR role!",
                        HttpStatus.BAD_REQUEST
                );
            }

            if (user.hasUserGroup(group)) {
                if (user.getNumberOfGroups() == 1) {
                    throw new CustomException(
                            "The user must have at least one role!",
                            HttpStatus.BAD_REQUEST
                    );
                }

                user.removeUserGroup(group);
                userRepo.save(user);

                eventRepo.save(new Event(
                        EventName.REMOVE_ROLE,
                        subject,
                        String.format("Remove role %s from %s",
                                changeRoleRequestForm.getRole(),
                                username),
                        "/api/admin/user/role"
                ));

            } else {
                throw new CustomException(
                        "The user does not have a role!",
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        return UserConverter.convert(user);
    }

    @PutMapping(path = "/api/admin/user/access")
    public Map<String, String> lockUnlockUsers(
            @RequestBody LockUnlockUsersRequestForm lockUnlockUsersRequestForm,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String subject = userDetails.getUsername();

        String operation = lockUnlockUsersRequestForm.getOperation();
        String username = lockUnlockUsersRequestForm.getUser().toLowerCase(Locale.ROOT);

        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (user.getUserGroups().stream()
                .map(Group::getCode)
                .anyMatch(code -> code.equals("ROLE_ADMINISTRATOR"))) {
            throw new CustomException(
                    "Can't lock the ADMINISTRATOR!",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (user.isAccountNonLocked()) {
            user.setAccountNonLocked(false);

            eventRepo.save(new Event(
                    EventName.LOCK_USER,
                    subject,
                    String.format("Lock user %s",
                            username),
                    "/api/admin/user/access"
            ));

        } else {
            user.setAccountNonLocked(true);
            user.resetFailedAttemptCounter();

            eventRepo.save(new Event(
                    EventName.UNLOCK_USER,
                    subject,
                    String.format("Unlock user %s",
                            username),
                    "/api/admin/user/access"
            ));
        }

        userRepo.save(user);



        return Map.of("status", String.format("User %s %s!",
                username,
                operation.toLowerCase(Locale.ROOT) + "ed"
        ));
    }

}
