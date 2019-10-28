package no.nav.sbl.dialogarena.varsel.config;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSAktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarselbestilling;
import org.joda.time.DateTime;

import java.util.List;

import static java.util.Arrays.asList;

class VarselbestillingMockData {

    static List<WSVarselbestilling> lagVarselbestillingListe() {
        return asList(
                lagVarselbestilling(),
                lagDokumentVarselbestilling(),
                lagGammelVarselbestilling()
        );
    }

    private static WSVarselbestilling lagGammelVarselbestilling() {
        DateTime ifjor = DateTime.now().minusYears(1);

        return new WSVarselbestilling()
                .withVarseltypeId("MOTE")
                .withAktoerId(new WSAktoerId().withAktoerId("321654987"))
                .withPerson(new WSPerson().withIdent("12345612345"))
                .withBestilt(new XMLGregorianCalendarImpl(ifjor.toGregorianCalendar()))
                .withSisteVarselutsendelse(new XMLGregorianCalendarImpl(ifjor.toGregorianCalendar()))
                .withVarselListe(asList(
                        lagSMSVarsel(ifjor),
                        lagEpostVarsel(ifjor),
                        lagNAVVarsel(ifjor)
                ));
    }


    private static WSVarselbestilling lagVarselbestilling() {
        DateTime now = DateTime.now();

        return new WSVarselbestilling()
                .withVarseltypeId("MOTE")
                .withAktoerId(new WSAktoerId().withAktoerId("321654987"))
                .withPerson(new WSPerson().withIdent("12345612345"))
                .withBestilt(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()))
                .withSisteVarselutsendelse(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()))
                .withVarselListe(asList(
                        lagSMSVarsel(now),
                        lagEpostVarsel(now),
                        lagNAVVarsel(now)
                ));
    }

    private static WSVarselbestilling lagDokumentVarselbestilling() {
        DateTime now = DateTime.now();

        return new WSVarselbestilling()
                .withVarseltypeId("DOKUMENT")
                .withAktoerId(new WSAktoerId().withAktoerId("321654987"))
                .withPerson(new WSPerson().withIdent("12345612345"))
                .withBestilt(new XMLGregorianCalendarImpl(now.minusDays(10).minusMinutes(60).toGregorianCalendar()))
                .withSisteVarselutsendelse(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()))
                .withReVarselingsintervall(7)
                .withVarselListe(asList(
                        lagSMSVarsel(now.minusDays(10)),
                        lagEpostVarsel(now.minusDays(10)),
                        lagNAVVarsel(now.minusDays(10)),
                        lagSMSVarsel(now.minusMonths(2)).withReVarsel(true),
                        lagEpostVarsel(now.minusMonths(2)).withReVarsel(true),
                        lagNAVVarsel(now.minusMonths(2)).withReVarsel(true)));
    }

    private static WSVarsel lagSMSVarsel(DateTime time) {
        return new WSVarsel()
                .withKanal("SMS")
                .withSendt(null)
                .withDistribuert(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()))
                .withKontaktinfo("95959595")
                .withVarseltekst("Du har mottatt et varsel på sms");
    }

    private static WSVarsel lagEpostVarsel(DateTime time) {
        return new WSVarsel()
                .withKanal("EPOST")
                .withSendt(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()))
                .withDistribuert(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()))
                .withKontaktinfo("test@testesen.com")
                .withVarseltekst("Du har mottatt et varsel på epost")
                .withVarseltittel("Emne: Du har mottatt et varsel på epost");
    }


    private static WSVarsel lagNAVVarsel(DateTime time) {
        return new WSVarsel()
                .withKanal("NAV.NO")
                .withSendt(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()))
                .withDistribuert(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()))
                .withKontaktinfo("Test testesen")
                .withVarseltittel("Varselemne")
                .withVarseltekst("Du har mottatt et varsel på nav.no");
    }
}
