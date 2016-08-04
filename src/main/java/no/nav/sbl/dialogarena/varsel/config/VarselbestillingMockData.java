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
                lagDokumentVarselbestilling()
        );
    }

    private static WSVarselbestilling lagVarselbestilling() {
        DateTime now = DateTime.now();

        return new WSVarselbestilling()
                .withVarseltypeId("MOTE")
                .withAktoerId(new WSAktoerId().withAktoerId("321654987"))
                .withPerson(new WSPerson().withIdent("***REMOVED***"))
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
                .withPerson(new WSPerson().withIdent("***REMOVED***"))
                .withBestilt(new XMLGregorianCalendarImpl(now.minusDays(10).minusMinutes(60).toGregorianCalendar()))
                .withSisteVarselutsendelse(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()))
                .withVarselListe(asList(
                        lagSMSVarsel(now.minusDays(10)),
                        lagEpostVarsel(now.minusDays(10)),
                        lagNAVVarsel(now.minusDays(10)),
                        lagSMSVarsel(now.minusMonths(2)),
                        lagEpostVarsel(now.minusMonths(2)),
                        lagNAVVarsel(now.minusMonths(2))));
    }

    private static WSVarsel lagSMSVarsel(DateTime time) {
        return new WSVarsel()
                .withKanal("SMS")
                .withSendt(null)
                .withDistribuert(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()))
                .withKontaktinfo("98765432")
                .withVarseltekst("Du har mottatt et varsel på sms");
    }

    private static WSVarsel lagEpostVarsel(DateTime time) {
        return new WSVarsel()
                .withKanal("EPOST")
                .withSendt(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()))
                .withDistribuert(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()))
                .withKontaktinfo("test@testesen.com")
                .withVarseltekst("Du har mottatt et varsel på epost");
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
