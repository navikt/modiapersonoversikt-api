package no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping;

import no.nav.kjerneinfo.common.utils.DateUtils;
import no.nav.sykmeldingsperioder.domain.pleiepenger.Periode;
import no.nav.sykmeldingsperioder.domain.pleiepenger.Vedtak;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSVedtak;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class VedtaksMapperTest {

    private static final int PROSENT = 66;
    private static final XMLGregorianCalendar FOM_DATO =
            DateUtils.convertDateTimeToXmlGregorianCalendar(LocalDateTime.parse("2014-05-14"));
    private static final XMLGregorianCalendar TOM_DATO =
            DateUtils.convertDateTimeToXmlGregorianCalendar(LocalDateTime.parse("2015-04-14"));
    private static final XMLGregorianCalendar ANVIST_UTBETALING =
            DateUtils.convertDateTimeToXmlGregorianCalendar(LocalDateTime.parse("2016-04-14"));
    private static final BigDecimal BRUTTO_BELOP = new BigDecimal("5067.55");
    private static final BigDecimal DAGSATS = new BigDecimal("314.15");
    private static final Integer PLEIEPENGEGRAD = 66;

    private VedtaksMapper mapper;


    @Before
    public void setup() {
        mapper = new VedtaksMapper();
    }

    @Test
    public void periodeMappesMedRikitigeDatoer() {
        WSPeriode wsPeriode = new WSPeriode().withFom(FOM_DATO).withTom(TOM_DATO);

        Periode periode = mapper.map(wsPeriode);

        assertSameDate(periode.fraOgMed, FOM_DATO);
        assertSameDate(periode.tilOgMed, TOM_DATO);
    }

    @Test
    public void vedtakMappesMedVedtak() {
        WSVedtak wsVedtak = mockVedtakMedKunPakrevdeFelt();

        Vedtak vedtak = mapper.map(wsVedtak);

        assertThat(vedtak.getPeriode(), is(notNullValue()));
    }

    @Test
    public void vedtakMappesMedKompensasjonsgrad() {
        WSVedtak wsVedtak = mockVedtakMedKunPakrevdeFelt()
                .withKompensasjonsgrad(PROSENT);

        Vedtak vedtak = mapper.map(wsVedtak);

        assertThat(vedtak.getKompensasjonsgrad(), is(PROSENT));
    }

    @Test
    public void vedtakMappesUtenKompensasjonsgrad() {
        WSVedtak wsVedtak = mockVedtakMedKunPakrevdeFelt();

        Vedtak vedtak = mapper.map(wsVedtak);

        assertThat(vedtak.getKompensasjonsgrad(), is(nullValue()));
    }

    @Test
    public void vedtakMappesMedPleiepengegrad() {
        WSVedtak wsVedtak= mockVedtakMedKunPakrevdeFelt()
                .withPleiepengegrad(PLEIEPENGEGRAD);

        Vedtak vedtak = mapper.map(wsVedtak);

        assertThat(vedtak.getPleiepengegrad(), is(PLEIEPENGEGRAD));
    }

    @Test
    public void vedtakMappesMedUtbetalingsgrad() {
        WSVedtak wsVedtak = mockVedtakMedKunPakrevdeFelt();

        Vedtak vedtak = mapper.map(wsVedtak);

        assertThat(vedtak.getUtbetalingsgrad(), is(PROSENT));
    }

    @Test
    public void vedtakMappesMedAnvistUtbetaling() {
        WSVedtak wsVedtak = mockVedtakMedKunPakrevdeFelt();

        Vedtak vedtak = mapper.map(wsVedtak);

        assertSameDate(vedtak.getAnvistUtbetaling(), ANVIST_UTBETALING);
    }

    @Test
    public void vedtakMappesMedBruttoBelop() {
        WSVedtak wsVedtak = mockVedtakMedKunPakrevdeFelt();

        Vedtak vedtak = mapper.map(wsVedtak);

        assertThat(vedtak.getBruttoBelop(), is(BRUTTO_BELOP));
    }

    @Test
    public void vedtakMappesMedDagsats() {
        WSVedtak wsVedtak = mockVedtakMedKunPakrevdeFelt();

        Vedtak vedtak = mapper.map(wsVedtak);

        assertThat(vedtak.getDagsats(), is(DAGSATS));
    }

    public static WSVedtak mockVedtakMedKunPakrevdeFelt() {
        return new WSVedtak()
                .withVedtak(new WSPeriode().withFom(FOM_DATO).withTom(TOM_DATO))
                .withUtbetalingsgrad(new BigDecimal(PROSENT))
                .withAnvistUtbetaling(ANVIST_UTBETALING)
                .withBruttobeloep(BRUTTO_BELOP)
                .withDagsats(DAGSATS);
    }

    private void assertSameDate(LocalDate localDate, XMLGregorianCalendar xmlGregorianCalendar) {
        assertThat(localDate.getDayOfMonth(), is(xmlGregorianCalendar.getDay()));
        assertThat(localDate.getMonthValue(), is(xmlGregorianCalendar.getMonth()));
        assertThat(localDate.getYear(), is(xmlGregorianCalendar.getYear()));
    }

}
