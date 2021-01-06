package no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping.to;

import no.nav.sykmeldingsperioder.domain.pleiepenger.Pleiepengerrettighet;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class PleiepengerListeResponseTest {

    private final static String BARN1_FNR = "10109000241";
    private static final String BARN2_FNR = "17100012345";

    @Test
    public void pleiepengerListeResponseLagesMedTomListe() {
        PleiepengerListeResponse response = new PleiepengerListeResponse(new ArrayList<>());

        List<Pleiepengerrettighet> pleipengerrettighetListe =  response.getPleieepengerettighetListe();

        assertThat(pleipengerrettighetListe, is(notNullValue()));
    }

    @Test
    public void getPleieepengerettighetListeReturnererListe() {
        PleiepengerListeResponse response = new PleiepengerListeResponse(Collections.singletonList(new Pleiepengerrettighet()));

        List<Pleiepengerrettighet> pleipengerrettighetListe =  response.getPleieepengerettighetListe();

        assertThat(pleipengerrettighetListe.size(), is(1));
    }

    @Test
    public void hentPleiepengerRettighetForBarnHvisBarnetIkkeErIListe() {
        PleiepengerListeResponse response = new PleiepengerListeResponse(Collections.singletonList(new Pleiepengerrettighet()
                .withBarnet(BARN2_FNR)));

        Optional<Pleiepengerrettighet> pleiepengerrettighet = response.getPleiepengerRettighet(BARN1_FNR);

        assertThat(pleiepengerrettighet.isPresent(), is(false));
    }

    @Test
    public void hentPleiepengerRettighetForBarnHvisBarnetErIListe() {
        PleiepengerListeResponse response = new PleiepengerListeResponse(Collections.singletonList(new Pleiepengerrettighet()
                .withBarnet(BARN2_FNR)));

        Optional<Pleiepengerrettighet> pleiepengerrettighet = response.getPleiepengerRettighet(BARN2_FNR);

        assertThat(pleiepengerrettighet.isPresent(), is(true));
    }

}
