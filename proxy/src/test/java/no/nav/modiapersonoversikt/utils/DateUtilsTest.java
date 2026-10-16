package no.nav.modiapersonoversikt.utils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {
    @Test
    public void localDateConversionsPreserveDateInSystemZone() {
        LocalDate date = LocalDate.of(2024, 1, 15);

        assertEquals(date, DateUtils.toLocalDate(DateUtils.toDate(date)));
    }

    @Test
    public void offsetDateTimeConversionPreservesInstant() {
        OffsetDateTime dateTime = OffsetDateTime.parse("2024-01-15T10:30:00+02:00");

        assertEquals(Date.from(dateTime.toInstant()), DateUtils.toDate(dateTime));
    }
}
