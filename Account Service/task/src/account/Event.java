package account;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "security_events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate date;

    @JsonProperty("action")
    private String eventName;

    @JsonProperty("subject")
    private String username;

    private String object;

    private String path;

    public Event() {
    }

    public Event(EventName eventName, String username, String object, String path) {
        this.date = LocalDate.now();
        this.eventName = eventName.name();
        this.username = username;
        this.object = object;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(EventName eventName) {
        this.eventName = eventName.name();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
