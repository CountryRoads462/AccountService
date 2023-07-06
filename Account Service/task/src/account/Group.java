package account;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "principle_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String name;

    @ManyToMany(mappedBy = "userGroups", fetch = FetchType.EAGER)
    private Set<User> users;

    public Group() {
    }

    public Group(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (name == null ? 0 : name.hashCode());
        result = 31 * result + (code == null ? 0 : code.hashCode());
        result = 31 * result + (int) id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Group)) {
            return false;
        }

        Group group = (Group) obj;

        return id == group.id &&
                Objects.equals(code, group.code) &&
                Objects.equals(name, group.name);
    }
}
