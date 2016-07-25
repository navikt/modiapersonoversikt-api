package no.nav.sbl.dialogarena.varsel.config;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class VarselbestillingMockData {

    public static List<Varselbestilling> lagVarselbestillingListe() {
        List<Varselbestilling> varselbestillingsliste = new ArrayList<>();
        varselbestillingsliste.add(lagVarselbestilling());
        varselbestillingsliste.add(lagDokumentVarselbestilling());
        return varselbestillingsliste;
    }

    private static Varselbestilling lagVarselbestilling() {
        DateTime now = DateTime.now();
        AktoerId nyAktoerId = new AktoerId();
        nyAktoerId.setAktoerId("321654987");

        Person nyPerson = new Person();
        nyPerson.setIdent("***REMOVED***");

        List<Varsel> varselliste = new ArrayList<>();

        varselliste.add(lagSMSVarsel(DateTime.now()));
        varselliste.add(lagEpostVarsel(DateTime.now()));
        varselliste.add(lagNAVVarsel(DateTime.now()));

        Varselbestilling varselbestilling = new VarselbestillingMock(varselliste);

        varselbestilling.setVarseltypeId("MOTE");
        varselbestilling.setAktoerId(nyAktoerId);
        varselbestilling.setPerson(nyPerson);
        varselbestilling.setBestilt(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()));
        varselbestilling.setSisteVarselutsendelse(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()));

        return varselbestilling;
    }

    private static Varselbestilling lagDokumentVarselbestilling() {
        DateTime now = DateTime.now();
        AktoerId nyAktoerId = new AktoerId();
        nyAktoerId.setAktoerId("321654987");

        Person nyPerson = new Person();
        nyPerson.setIdent("***REMOVED***");

        List<Varsel> varselliste = new ArrayList<>();
        varselliste.add(lagSMSVarsel(DateTime.now().minusDays(10)));
        varselliste.add(lagEpostVarsel(DateTime.now().minusDays(10)));
        varselliste.add(lagNAVVarsel(DateTime.now().minusDays(10)));

        varselliste.add(lagSMSVarsel(DateTime.now().minusMonths(2)));
        varselliste.add(lagEpostVarsel(DateTime.now().minusMonths(2)));
        varselliste.add(lagNAVVarsel(DateTime.now().minusMonths(2)));


        Varselbestilling varselbestilling = new VarselbestillingMock(varselliste);

        varselbestilling.setVarseltypeId("DOKUMENT");
        varselbestilling.setAktoerId(nyAktoerId);
        varselbestilling.setPerson(nyPerson);
        varselbestilling.setBestilt(new XMLGregorianCalendarImpl(now.minusDays(10).minusMinutes(60).toGregorianCalendar()));
        varselbestilling.setSisteVarselutsendelse(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()));

        return varselbestilling;
    }

    private static Varsel lagSMSVarsel(DateTime time) {
        Varsel varsel = new Varsel();
        varsel.setKanal("SMS");
        varsel.setSendt(null);
        varsel.setDistribuert(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()));
        varsel.setKontaktinfo("98765432");
        varsel.setVarseltekst("Du har mottatt et varsel på sms");

        return varsel;
    }

    private static Varsel lagEpostVarsel(DateTime time) {
        Varsel varsel = new Varsel();
        varsel.setKanal("EPOST");
        varsel.setSendt(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()));
        varsel.setDistribuert(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()));
        varsel.setKontaktinfo("test@testesen.com");
        varsel.setVarseltekst("Du har mottatt et varsel på epost");

        return varsel;
    }


    private static Varsel lagNAVVarsel(DateTime time) {
        Varsel varsel = new Varsel();
        varsel.setKanal("NAV.NO");
        varsel.setSendt(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()));
        varsel.setDistribuert(new XMLGregorianCalendarImpl(time.minusMinutes(60).toGregorianCalendar()));
        varsel.setKontaktinfo("Test testesen");
        varsel.setVarseltittel("Varselemne");
        varsel.setVarseltekst("Du har mottatt et varsel på nav.no");

        return varsel;
    }
}
