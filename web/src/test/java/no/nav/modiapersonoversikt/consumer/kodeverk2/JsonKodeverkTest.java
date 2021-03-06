package no.nav.modiapersonoversikt.consumer.kodeverk2;

import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Map;

import static no.nav.modiapersonoversikt.consumer.kodeverk2.Kodeverk.Nokkel;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonKodeverkTest {

    private Kodeverk kodeverk;
    private static final String TEST = "test";

    @Before
    public void setup() {
        kodeverk = new JsonKodeverk(getClass().getResourceAsStream("/kodeverk_test.json"));
    }

    @Test
    public void shouldThrowExceptionWhenParsingFails() {
        try {
            new JsonKodeverk(null);
        } catch (ApplicationException ae) {
            assertThat(ae.getMessage(), is("Klarte ikke å parse kodeverk-info"));
        }
    }

    @Test(expected = ApplicationException.class)
    public void shouldThrowExceptionWhenMissingValue() {
        InputStream resourceAsStream = getClass().getResourceAsStream("/kodeverk_feil.json");
        new JsonKodeverk(resourceAsStream);
    }

    @Test
    public void shouldReadEmptyFile() {
        InputStream resourceAsStream = getClass().getResourceAsStream("/kodeverk_tom.json");
        new JsonKodeverk(resourceAsStream);
    }

    @Test
    public void shouldGetVedleggById() {
        Map<Nokkel, String> koder = kodeverk.getKoder("vedleggsid");
        assertThat(koder, notNullValue());
    }

    @Test
    public void canGetExistingKodeverkByField() {
        assertThat(kodeverk.getKode(TEST, Nokkel.BESKRIVELSE), is(equalTo("beskrivelse")));
        assertThat(kodeverk.getKode(TEST, Nokkel.GOSYS_ID), is(equalTo("gosysId")));
        assertThat(kodeverk.getKode(TEST, Nokkel.TEMA), is(equalTo("tema")));
        assertThat(kodeverk.getKode(TEST, Nokkel.TITTEL), is(equalTo("tittel")));
        assertThat(kodeverk.getKode(TEST, Nokkel.TITTEL_EN), is(equalTo("tittelEnglish")));
        assertThat(kodeverk.getKode(TEST, Nokkel.TITTEL_NN), is(equalTo("tittelNynorsk")));
        assertThat(kodeverk.getKode(TEST, Nokkel.URL), is(equalTo("url")));
        assertThat(kodeverk.getKode(TEST, Nokkel.VEDLEGGSID), is(equalTo("vedleggsid")));
        assertThat(kodeverk.getKode(TEST, Nokkel.URLENGLISH), is(equalTo("urlEnglish")));
        assertThat(kodeverk.getKode(TEST, Nokkel.URLNEWNORWEGIAN), is(equalTo("urlNewnorwegian")));
        assertThat(kodeverk.getKode(TEST, Nokkel.URLPOLISH), is(equalTo("urlPolish")));
        assertThat(kodeverk.getKode(TEST, Nokkel.URLFRENCH), is(equalTo("urlFrench")));
        assertThat(kodeverk.getKode(TEST, Nokkel.URLSPANISH), is(equalTo("urlSpanish")));
        assertThat(kodeverk.getKode(TEST, Nokkel.URLGERMAN), is(equalTo("urlGerman")));
        assertThat(kodeverk.getKode(TEST, Nokkel.URLSAMISK), is(equalTo("urlSamisk")));
    }

    @Test
    public void canGetExistingKodeverkByMap() {
        Map<Nokkel, String> koder = kodeverk.getKoder(TEST);
        assertThat(koder.get(Nokkel.BESKRIVELSE), is(equalTo("beskrivelse")));
        assertThat(koder.get(Nokkel.GOSYS_ID), is(equalTo("gosysId")));
        assertThat(koder.get(Nokkel.TEMA), is(equalTo("tema")));
        assertThat(koder.get(Nokkel.TITTEL), is(equalTo("tittel")));
        assertThat(koder.get(Nokkel.TITTEL_EN), is(equalTo("tittelEnglish")));
        assertThat(koder.get(Nokkel.TITTEL_NN), is(equalTo("tittelNynorsk")));
        assertThat(koder.get(Nokkel.URL), is(equalTo("url")));
        assertThat(koder.get(Nokkel.VEDLEGGSID), is(equalTo("vedleggsid")));
        assertThat(koder.get(Nokkel.URLENGLISH), is(equalTo("urlEnglish")));
        assertThat(koder.get(Nokkel.URLNEWNORWEGIAN), is(equalTo("urlNewnorwegian")));
        assertThat(koder.get(Nokkel.URLPOLISH), is(equalTo("urlPolish")));
        assertThat(koder.get(Nokkel.URLFRENCH), is(equalTo("urlFrench")));
        assertThat(koder.get(Nokkel.URLSPANISH), is(equalTo("urlSpanish")));
        assertThat(koder.get(Nokkel.URLGERMAN), is(equalTo("urlGerman")));
        assertThat(koder.get(Nokkel.URLSAMISK), is(equalTo("urlSamisk")));

    }

    @Test(expected = UnsupportedOperationException.class)
    public void canNotAlterKodeverk() {
        Map<Nokkel, String> koder = kodeverk.getKoder(TEST);
        koder.put(Nokkel.BESKRIVELSE, "feilbeskrivelse");
    }

    @Test(expected = ApplicationException.class)
    public void unknownKodeverkMapThrowsException() {
        kodeverk.getKoder("unknown");
    }

    @Test(expected = ApplicationException.class)
    public void unknownKodeverkThrowsException() {
        kodeverk.getKode("unknown", Nokkel.URL);
    }

    @Test
    public void shouldRecognizeEgendefKode() {
        assertThat(kodeverk.isEgendefinert(Kodeverk.ANNET), is(true));
        assertThat(kodeverk.isEgendefinert("hei"), is(false));
    }

    @Test
    public void shouldGetTittel() {
        assertThat(kodeverk.getTittel(TEST), equalTo("tittel"));
    }
}
