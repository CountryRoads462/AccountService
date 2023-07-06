package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SecurityController {

    @Autowired
    EventRepository eventRepo;

    @Secured("ROLE_AUDITOR")
    @GetMapping(path = "/api/security/events/")
    public List<Event> getEvents() {
        return eventRepo.findAllOrderById();
    }

}
