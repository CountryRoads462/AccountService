package account;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String lastname;

    @Pattern(regexp = ".+@acme\\.com")
    @NotEmpty
    @Column(unique = true)
    @JsonProperty(value = "email")
    private String username;

    @NotEmpty
    private String password;

    private boolean accountNonLocked = true;

    private int failedAttempt = 0;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    },
            fetch = FetchType.EAGER
    )
    @JoinTable(name = "user_groups",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> userGroups = new HashSet<>();

    public void addUserGroup(Group group) {
        this.userGroups.add(group);
    }

    public void removeUserGroup(Group group) {
        this.userGroups.remove(group);
    }

    public boolean hasUserGroup(Group group) {
        return this.userGroups.contains(group);
    }

    public int getNumberOfGroups() {
        return userGroups.size();
    }

    public Set<Group> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<Group> userGroups) {
        this.userGroups = userGroups;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public int getFailedAttempt() {
        return failedAttempt;
    }

    public void increaseFailedAttempts() {
        this.failedAttempt++;
    }

    public void resetFailedAttemptCounter() {
        this.failedAttempt = 0;
    }
}