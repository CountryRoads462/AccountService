package account;

import java.time.LocalDate;
import java.util.Locale;

public class ChangeTheSalaryResponseForm {

    private String name;
    private String lastname;
    private String period;
    private String salary;

    public ChangeTheSalaryResponseForm() {
    }

    public ChangeTheSalaryResponseForm(String name, String lastname, LocalDate period, long salary) {
        this.name = name;
        this.lastname = lastname;

        String monthName = period.getMonth().name().toLowerCase();
        monthName = monthName.replaceFirst("^.", String.valueOf(monthName.charAt(0)).toUpperCase(Locale.ROOT));
        this.period = String.format("%s-%d", monthName, period.getYear());

        long dollars = salary / 100;
        long cents = salary % 100;
        this.salary = String.format("%d dollar(s) %d cent(s)", dollars, cents);
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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
