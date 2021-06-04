package no.nav.modiapersonoversikt.integration.sykmeldingsperioder.domain;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArbeidsforholdTest {
    public static final String ARBEIDSGIVERNAVN = "Arbeidsgiver navn";
    public static final String ARBEIDSGIVERSKONTONUMMER = "12345678945";
    public static final LocalDate SYKEPENGERFOM = new LocalDate(2013, 2, 1);
    public static final LocalDate REFUSJONTOM = new LocalDate(2014, 2, 1);
    public static final String ARBEIDSFORHOLD_REFUSJONTYPE_KODE = "GRA";
    public static final String ARBEIDSFORHOLD_REFUSJONTYPE_TERM = "Generell refusjon A";
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_KODE = "IP";
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_TERM = "Inntektsperiode 1";
    public static final Double ARBEIDSFORHOLD_INNTEKT = 3652.20;
    public static final Kodeverkstype REFUSJONTYPE = new Kodeverkstype(ARBEIDSFORHOLD_REFUSJONTYPE_KODE, ARBEIDSFORHOLD_REFUSJONTYPE_TERM);
    public static final Kodeverkstype INNTEKTPERIODE = new Kodeverkstype(ARBEIDSFORHOLD_INNTEKTPERIODE_KODE, ARBEIDSFORHOLD_INNTEKTPERIODE_TERM);

    @Test
    public void testBean() {

        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        arbeidsforhold.setArbeidsgiverNavn(ARBEIDSGIVERNAVN);
        arbeidsforhold.setArbeidsgiverKontonr(ARBEIDSGIVERSKONTONUMMER);
        arbeidsforhold.setSykepengerFom(SYKEPENGERFOM);
        arbeidsforhold.setRefusjonTom(REFUSJONTOM);
        arbeidsforhold.setRefusjonstype(REFUSJONTYPE);
        arbeidsforhold.setInntektForPerioden(ARBEIDSFORHOLD_INNTEKT);
        arbeidsforhold.setInntektsperiode(INNTEKTPERIODE);

        assertEquals(ARBEIDSGIVERNAVN, arbeidsforhold.getArbeidsgiverNavn());
        assertEquals(ARBEIDSGIVERSKONTONUMMER, arbeidsforhold.getArbeidsgiverKontonr());
        assertEquals(SYKEPENGERFOM, arbeidsforhold.getSykepengerFom());
        assertEquals(REFUSJONTOM, arbeidsforhold.getRefusjonTom());
        assertEquals(REFUSJONTYPE, arbeidsforhold.getRefusjonstype());
        assertEquals(ARBEIDSFORHOLD_INNTEKT, arbeidsforhold.getInntektForPerioden());
        assertEquals(INNTEKTPERIODE, arbeidsforhold.getInntektsperiode());
    }
}
