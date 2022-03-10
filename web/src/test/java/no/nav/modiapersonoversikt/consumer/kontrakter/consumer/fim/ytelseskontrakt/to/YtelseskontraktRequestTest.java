package no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.ytelseskontrakt.to;

import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class YtelseskontraktRequestTest {
    @Test
    public void testEquals() {

        YtelseskontraktRequest request1 = new YtelseskontraktRequest();
        YtelseskontraktRequest request2 = null;

        assertFalse(request1.equals(request2));

        request2 = new YtelseskontraktRequest();
        assertTrue(request1.equals(request2));

        request1.setFodselsnummer("123");
        request2.setFodselsnummer("122");
        assertFalse(request1.equals(request2));

        request2.setFodselsnummer("123");
        assertTrue(request1.equals(request2));

        // Cases for "from"
        LocalDate now = LocalDate.now();
        request2.setFrom(now);
        assertFalse(request1.equals(request2));

        request2.setFrom(null);
        request1.setFrom(now);
        assertFalse(request1.equals(request2));

        request2.setFrom(now.minusDays(1));
        assertFalse(request1.equals(request2));
        request2.setFrom(now);
        assertTrue(request1.equals(request2));

        request2 = request1;
        assertTrue(request1.equals(request2));

        request1 = new YtelseskontraktRequest();
        assertFalse(request1.equals(request2));

        // Cases for "to"
        request1.setFodselsnummer(request2.getFodselsnummer());
        request1.setFrom(request2.getFrom());
        request2.setTo(now);
        assertFalse(request1.equals(request2));

        request1.setTo(now.minusDays(1));
        assertFalse(request1.equals(request2));

        request1.setTo(now);
        assertTrue(request1.equals(request2));
    }

    @Test
    public void testHashCode() {
        YtelseskontraktRequest request1 = new YtelseskontraktRequest();
        YtelseskontraktRequest request2 = request1;
        assertTrue(request1.hashCode() == request2.hashCode());

        request2 = new YtelseskontraktRequest();
        request1.setFodselsnummer("123");
        request2.setFodselsnummer("321");
        assertFalse(request1.hashCode() == request2.hashCode());

        request2.setFodselsnummer("123");
        LocalDate now = LocalDate.now();
        request1.setFrom(now);
        assertFalse(request1.hashCode() == request2.hashCode());

        request2.setFrom(now.minusDays(1));
        assertFalse(request1.hashCode() == request2.hashCode());
        request2.setFrom(now);
        assertTrue(request1.hashCode() == request2.hashCode());

        request1.setTo(now);
        assertFalse(request1.hashCode() == request2.hashCode());
        request2.setTo(now.minusDays(1));
        assertFalse(request1.hashCode() == request2.hashCode());
        request2.setTo(now);
        assertTrue(request1.hashCode() == request2.hashCode());
    }
}
