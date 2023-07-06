package account;

import java.time.LocalDate;

public class LocalDatePeriodParser {

    public static LocalDate parse(String period) {
        String[] monthAndYear = period.split("-");
        int month = Integer.parseInt(monthAndYear[0]);
        int year = Integer.parseInt(monthAndYear[1]);
        return LocalDate.of(year, month, 1);
    }
}
