package no.nav.sykmeldingsperioder.consumer.common;

import no.nav.kjerneinfo.common.utils.DateUtils;
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykArbeidsforhold;
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykInntektsperiode;
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykRefusjonstype;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

/**
 * Diverse metoder for opprettelse av felles mock-objekter
 */
public class CommonMockFactory {

	public static FimsykArbeidsforhold createArbeidsforholdSykepenger(String kontonummer, String arbeidsgivernavn, LocalDate refusjonTom, FimsykRefusjonstype refusjonstype, LocalDate refusjonFom, Double inntekt,
																	  FimsykInntektsperiode inntektperiode) {
		FimsykArbeidsforhold fimArbeidsforhold = new FimsykArbeidsforhold();
		fimArbeidsforhold.setArbeidsgiverKontonr(kontonummer);
		fimArbeidsforhold.setArbeidsgiverNavn(arbeidsgivernavn);
		fimArbeidsforhold.setRefusjonTom(DateUtils.convertDateToXmlGregorianCalendar(refusjonTom.toDate()));
		fimArbeidsforhold.setRefusjonstype(refusjonstype);
		fimArbeidsforhold.setSykepengerFom(DateUtils.convertDateToXmlGregorianCalendar(refusjonFom.toDate()));
		fimArbeidsforhold.setInntektForPerioden(BigDecimal.valueOf(inntekt));
		fimArbeidsforhold.setInntektsperiode(inntektperiode);
		return fimArbeidsforhold;
	}

}
