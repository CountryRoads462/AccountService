package account;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class LockUnlockUsersRequestForm {

    @NotEmpty
    private String user;

    @Pattern(regexp = "(" +
            "LOCK|" +
            "UNLOCK" +
            ")")
    private String operation;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
