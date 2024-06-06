package no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.mock;

import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimYtelseskontrakt;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class YtelseskontraktMockFactoryTest {

    @Test
    public void checkFiltering() {
        List<FimYtelseskontrakt> onlyDagpengekontrakt = YtelseskontraktMockFactory.createYtelsesKontrakter("22222222222", DateTime.now().toDate(), DateTime.now().toDate());
        assertThat(onlyDagpengekontrakt.size(), equalTo(1));

        List<FimYtelseskontrakt> all = YtelseskontraktMockFactory.createYtelsesKontrakter("22222222222", DateTime.parse("1899-01-01").toDate(), DateTime.parse("2199-01-01").toDate());
        assertThat(all.size(), equalTo(3));
    }
}
