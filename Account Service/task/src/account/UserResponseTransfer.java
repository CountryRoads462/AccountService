package account;

import java.util.TreeSet;

public class UserResponseTransfer {

    private long id;
    private String name;
    private String lastname;
    private String email;
    private TreeSet<String> roles;

    public UserResponseTransfer() {
    }

    public UserResponseTransfer(long id, String name, String lastname, String email, TreeSet<String> roles) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TreeSet<String> getRoles() {
        return roles;
    }

    public void setRoles(TreeSet<String> roles) {
        this.roles = roles;
    }

}