package account;

import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private final GroupRepository groupRepo;

    @Autowired
    public DataLoader(GroupRepository groupRepo) {
        this.groupRepo = groupRepo;
        createRoles();
    }

    private void createRoles() {
        try {
            groupRepo.save(new Group("ROLE_ADMINISTRATOR", "Admin Group"));
            groupRepo.save(new Group("ROLE_USER", "User Group"));
            groupRepo.save(new Group("ROLE_ACCOUNTANT", "Accountant Group"));
            groupRepo.save(new Group("ROLE_AUDITOR", "Auditor Group"));
        } catch (Exception ignored) {
        }
    }
}
