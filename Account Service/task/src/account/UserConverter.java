package account;

import java.util.TreeSet;

public class UserConverter {

    public static UserResponseTransfer convert(User user) {
        UserResponseTransfer userResponseTransfer = new UserResponseTransfer();
        userResponseTransfer.setId(user.getId());
        userResponseTransfer.setName(user.getName());
        userResponseTransfer.setEmail(user.getUsername());
        userResponseTransfer.setLastname(user.getLastname());

        TreeSet<String> roles = new TreeSet<>();
        user.getUserGroups().stream()
                .map(Group::getCode)
                .forEach(roles::add);

        userResponseTransfer.setRoles(roles);

        return userResponseTransfer;
    }

}
