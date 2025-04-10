package no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt;

import no.nav.modiapersonoversikt.config.CacheTest;
import no.nav.modiapersonoversikt.arena.oppfolgingskontrakt.OppfolgingskontraktResponse;
import no.nav.modiapersonoversikt.arena.oppfolgingskontrakt.SYFOPunkt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class OppfoelgingCacheTest extends CacheTest {

    private static final String OPPFOELGING_CACHE = "oppfolgingskontraktCache";
    private static final String FODSELSNUMMER_AREMARK = "10108000398";
    private static final String FODSELSNUMMER_TROGSTAD = "06128074978";

    OppfoelgingCacheTest() {
        super(OPPFOELGING_CACHE);
    }

    @Autowired
    private OppfolgingskontraktService oppfolgingskontraktService;

    @BeforeEach
    void setUpMock() {
        OppfolgingskontraktService unwrapped = unwrapProxy(oppfolgingskontraktService);
        reset(unwrapped);

        OppfolgingskontraktResponse response1 = new OppfolgingskontraktResponse();
        response1.setSyfoPunkter(List.of(new SYFOPunkt()));
        OppfolgingskontraktResponse response2 = new OppfolgingskontraktResponse();
        response2.setSyfoPunkter(Arrays.asList(new SYFOPunkt(), new SYFOPunkt()));

        when(unwrapped.hentOppfolgingskontrakter(any(OppfolgingskontraktRequest.class)))
                .thenReturn(response1, response2);
    }

    @Test
    void cacheSetupMedRiktigKeyGenerator() {
        OppfolgingskontraktRequest request = new OppfolgingskontraktRequest();
        request.setFodselsnummer(FODSELSNUMMER_AREMARK);
        oppfolgingskontraktService.hentOppfolgingskontrakter(request);

        assertThat(getNativeCache().estimatedSize(), is(1L));
        assertThat(getKey(), is(generatedByUserKeyGenerator()));
    }

    @Test
    void toKallTilHentOppfoelgingskontraktListeMedSammeIdentGirBareEttTjenestekall() throws Exception {
        OppfolgingskontraktRequest request1 = new OppfolgingskontraktRequest();
        request1.setFodselsnummer(FODSELSNUMMER_AREMARK);
        OppfolgingskontraktRequest request2 = new OppfolgingskontraktRequest();
        request2.setFodselsnummer(FODSELSNUMMER_AREMARK);

        OppfolgingskontraktResponse response1 = oppfolgingskontraktService.hentOppfolgingskontrakter(request1);
        OppfolgingskontraktResponse response2 = oppfolgingskontraktService.hentOppfolgingskontrakter(request2);

        verify(unwrapProxy(oppfolgingskontraktService), times(1)).hentOppfolgingskontrakter(any());

        assertThat(response1.getSyfoPunkter(), is(response2.getSyfoPunkter()));
    }

    @Test
    void toKallTilHentOppfoelgingskontraktListeMedForskjelligeIdenterGirToTjenestekall() throws Exception {
        OppfolgingskontraktRequest request1 = new OppfolgingskontraktRequest();
        request1.setFodselsnummer(FODSELSNUMMER_AREMARK);
        OppfolgingskontraktRequest request2 = new OppfolgingskontraktRequest();
        request2.setFodselsnummer(FODSELSNUMMER_TROGSTAD);

        OppfolgingskontraktResponse response1 = oppfolgingskontraktService.hentOppfolgingskontrakter(request1);
        OppfolgingskontraktResponse response2 = oppfolgingskontraktService.hentOppfolgingskontrakter(request2);

        verify(unwrapProxy(oppfolgingskontraktService), times(2)).hentOppfolgingskontrakter(any());

        assertThat(response1.getSyfoPunkter(), is(not(response2.getSyfoPunkter())));
    }
}
