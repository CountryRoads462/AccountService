package account;

import jakarta.persistence.*;

@Entity
@Table(name = "breached_passwords")
public class BreachedPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
