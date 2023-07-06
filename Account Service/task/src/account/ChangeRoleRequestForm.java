package account;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.parameters.P;

public class ChangeRoleRequestForm {

    @NotEmpty
    private String user;

    private String role;

    @Pattern(regexp = "(" +
            "GRANT|" +
            "REMOVE" +
            ")")
    private String operation;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
