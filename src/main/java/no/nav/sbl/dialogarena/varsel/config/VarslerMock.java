package no.nav.sbl.dialogarena.varsel.config;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import no.nav.melding.domene.brukerdialog.varsler.v1.VarslerPorttype;
import no.nav.melding.domene.brukerdialog.varsler.v1.meldinger.*;
import org.joda.time.DateTime;

public class VarslerMock implements VarslerPorttype {

    @Override
    public WSHentVarslerResponse hentVarsler(WSHentVarslerRequest wsHentVarslerRequest) {
        DateTime now = DateTime.now();

        return new WSHentVarslerResponse()
            .withVarselListe(new WSVarselListe().withVarsel(

                new WSVarsel()
                    .withVarseltype("SPORSMAL")
                    .withMottattidspunkt(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()))
                    .withStatus("Ferdig")
                    .withMeldingListe(new WSMeldingListe().withMelding(
                        lagWsMelding("SMS", "En sms melding", "12345678", "", "OK", now.minusDays(1), "", ""),
                        lagWsMelding("EPOST", "En EPOST melding", "test@testeres.com", "", "OK", now.minusDays(1), "epostemne", ""),
                        lagWsMelding("NAV.NO", "En NAVNO melding", "", "", "OK", now.minusDays(1), "", "url")
                    )),
                new WSVarsel()
                    .withVarseltype("SVAR")
                    .withMottattidspunkt(new XMLGregorianCalendarImpl(now.minusDays(2).minusHours(1).toGregorianCalendar()))
                    .withStatus("Feilet")
                    .withMeldingListe(new WSMeldingListe().withMelding(
                        lagWsMelding("SMS", "En feilet sms", "", "Ingen telefon nummer", "ERROR", now.minusDays(2), "", ""),
                        lagWsMelding("EPOST", "En EPOST sms", "", "Ingen epost adr", "ERROR", now.minusDays(2), "epostemne", ""),
                        lagWsMelding("NAV.NO", "OK", "", "", "OK", now.minusDays(2), "", "url")
                    )),
                new WSVarsel()
                    .withVarseltype("MOTE")
                    .withMottattidspunkt(new XMLGregorianCalendarImpl(now.minusDays(2).minusHours(1).toGregorianCalendar()))
                    .withStatus("Ferdig")
                    .withMeldingListe(new WSMeldingListe().withMelding(
                        lagWsMelding("SMS", "En sms melding", "12345678", "", "OK", now.minusDays(1), "", ""),
                        lagWsMelding("EPOST", "En EPOST melding", "test@testeres.com", "", "500", now.minusDays(1), "epostemne", ""),
                        lagWsMelding("NAV.NO", "En NAVNO melding", "", "", "OK", now.minusDays(1), "", "url")
                    ))));
    }

    @Override
    public void ping() {

    }

    private static WSMelding lagWsMelding(String kanal, String innhold, String mottakerInformasjon, String feilbeskrivelse, String statusKode, DateTime date, String epostemne, String url) {
        if (date == null) {
            date = DateTime.now();
        }
        return new WSMelding()
            .withKanal(kanal)
            .withInnhold(innhold)
            .withMottakerinformasjon(mottakerInformasjon)
            .withFeilbeskrivelse(feilbeskrivelse)
            .withUtsendingstidspunkt(new XMLGregorianCalendarImpl(date.toGregorianCalendar()))
            .withStatuskode(statusKode)
            .withEpostemne(epostemne)
            .withUrl(url);
    }
}