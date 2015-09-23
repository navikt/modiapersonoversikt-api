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
                                .withVarseltype("SMS")
                                .withMottattidspunkt(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()))
                                .withStatus("OK")
                                .withMeldingListe(new WSMeldingListe().withMelding(
                                        lagWsMelding("SMS", "En sms melding", "12345678", "", "OK", now.minusDays(1)),
                                        lagWsMelding("EPOST", "En EPOST melding", "test@testeres.com", "", "OK", now.minusDays(1)),
                                        lagWsMelding("NAV.NO", "En NAVNO melding", "", "", "OK", now.minusDays(1))
                                )),
                        new WSVarsel()
                                .withVarseltype("EPOST")
                                .withMottattidspunkt(new XMLGregorianCalendarImpl(now.minusDays(2).minusHours(1).toGregorianCalendar()))
                                .withStatus("FEIL")
                                .withMeldingListe(new WSMeldingListe().withMelding(
                                        lagWsMelding("SMS", "En feilet sms", "", "Ingen telefon nummer", "ERROR", now.minusDays(2)),
                                        lagWsMelding("EPOST", "En EPOST sms", "", "Ingen epost adr", "ERROR", now.minusDays(2)),
                                        lagWsMelding("NAV.NO", "OK", "", "", "OK", now.minusDays(2))
                                ))));
    }

    @Override
    public void ping() {

    }

    private static WSMelding lagWsMelding(String kanal, String innhold, String informasjon, String feilbeskrivelse, String statusKode, DateTime date) {
        if (date == null) {
            date = DateTime.now();
        }
        return new WSMelding()
                .withKanal(kanal)
                .withInnhold(innhold)
                .withMottakerinformasjon(informasjon)
                .withFeilbeskrivelse(feilbeskrivelse)
                .withUtsendingstidspunkt(new XMLGregorianCalendarImpl(date.toGregorianCalendar()))
                .withStatuskode(statusKode);
    }
}