package no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.mock;

import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimYtelseskontrakt;
import no.nav.modiapersonoversikt.utils.DateUtils;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class YtelseskontraktMockFactoryTest {

    @Test
    public void checkFiltering() {
        List<FimYtelseskontrakt> onlyDagpengekontrakt = YtelseskontraktMockFactory.createYtelsesKontrakter("22222222222", DateUtils.toDate(OffsetDateTime.now()), DateUtils.toDate(OffsetDateTime.now()));
        assertThat(onlyDagpengekontrakt.size(), equalTo(1));

        List<FimYtelseskontrakt> all = YtelseskontraktMockFactory.createYtelsesKontrakter("22222222222", DateUtils.toDate(LocalDate.parse("1899-01-01")), DateUtils.toDate(LocalDate.parse("2199-01-01")));
        assertThat(all.size(), equalTo(3));
    }
}
