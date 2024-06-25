package no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.to;

import no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.domain.YtelseskontraktRequest;
import org.joda.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;

public class YtelseskontraktRequestTest {
    @Test
    public void testEquals() {

        YtelseskontraktRequest request1 = new YtelseskontraktRequest();
        YtelseskontraktRequest request2 = null;

        assertNotEquals(request1, request2);

        request2 = new YtelseskontraktRequest();
        assertEquals(request1, request2);

        request1.setFodselsnummer("123");
        request2.setFodselsnummer("122");
        assertNotEquals(request1, request2);

        request2.setFodselsnummer("123");
        assertEquals(request1, request2);

        // Cases for "from"
        LocalDate now = LocalDate.now();
        request2.setFrom(now);
        assertNotEquals(request1, request2);

        request2.setFrom(null);
        request1.setFrom(now);
        assertNotEquals(request1, request2);

        request2.setFrom(now.minusDays(1));
        assertNotEquals(request1, request2);
        request2.setFrom(now);
        assertEquals(request1, request2);

        request2 = request1;
        assertEquals(request1, request2);

        request1 = new YtelseskontraktRequest();
        assertNotEquals(request1, request2);

        // Cases for "to"
        request1.setFodselsnummer(request2.getFodselsnummer());
        request1.setFrom(request2.getFrom());
        request2.setTo(now);
        assertNotEquals(request1, request2);

        request1.setTo(now.minusDays(1));
        assertNotEquals(request1, request2);

        request1.setTo(now);
        assertEquals(request1, request2);
    }

    @Test
    public void testHashCode() {
        YtelseskontraktRequest request1 = new YtelseskontraktRequest();
        YtelseskontraktRequest request2 = request1;
        assertEquals(request1.hashCode(), request2.hashCode());

        request2 = new YtelseskontraktRequest();
        request1.setFodselsnummer("123");
        request2.setFodselsnummer("321");
        assertNotEquals(request1.hashCode(), request2.hashCode());

        request2.setFodselsnummer("123");
        LocalDate now = LocalDate.now();
        request1.setFrom(now);
        assertNotEquals(request1.hashCode(), request2.hashCode());

        request2.setFrom(now.minusDays(1));
        assertNotEquals(request1.hashCode(), request2.hashCode());
        request2.setFrom(now);
        assertEquals(request1.hashCode(), request2.hashCode());

        request1.setTo(now);
        assertNotEquals(request1.hashCode(), request2.hashCode());
        request2.setTo(now.minusDays(1));
        assertNotEquals(request1.hashCode(), request2.hashCode());
        request2.setTo(now);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
