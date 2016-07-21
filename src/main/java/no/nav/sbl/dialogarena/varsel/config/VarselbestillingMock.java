package no.nav.sbl.dialogarena.varsel.config;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class VarselbestillingMock extends Varselbestilling {

    public VarselbestillingMock() {
        DateTime now = DateTime.now();
        AktoerId nyAktoerId = new AktoerId();
        nyAktoerId.setAktoerId("321654987");

        Person nyPerson = new Person();
        nyPerson.setIdent("***REMOVED***");

        varseltypeId = "MOTE";
        aktoerId = nyAktoerId;
        person = nyPerson;
        bestilt = new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar());
        varselListe = lagVarselliste();
        sisteVarselutsendelse = new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar());

    }

    private List<Varsel> lagVarselliste() {
        DateTime now = DateTime.now();
        List<Varsel> varselliste = new ArrayList<>();

        varselliste.add(lagSMSVarsel());
        varselliste.add(lagEpostVarsel(now.minusMillis(60), now.minusMinutes(60)));
        varselliste.add(lagNAVVarsel());

        return varselliste;
    }

    private Varsel lagSMSVarsel() {
        DateTime now = DateTime.now();
        Varsel varsel = new Varsel();
        varsel.setKanal("SMS");
        varsel.setSendt(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()));
        varsel.setDistribuert(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()));
        varsel.setKontaktinfo("98765432");
        varsel.setVarseltekst("Du har mottatt et varsel på sms");

        return varsel;
    }

    private Varsel lagEpostVarsel(DateTime sendt, DateTime distribuert) {
        Varsel varsel = new Varsel();
        varsel.setKanal("EPOST");
        varsel.setSendt(new XMLGregorianCalendarImpl(sendt.toGregorianCalendar()));
        varsel.setDistribuert(new XMLGregorianCalendarImpl(distribuert.toGregorianCalendar()));
        varsel.setKontaktinfo("test@testesen.com");
        varsel.setVarseltekst("Du har mottatt et varsel på epost");

        return varsel;
    }


    private Varsel lagNAVVarsel() {
        DateTime now = DateTime.now();
        Varsel varsel = new Varsel();
        varsel.setKanal("NAV.NO");
        varsel.setSendt(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()));
        varsel.setDistribuert(new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar()));
        varsel.setKontaktinfo("Test testesen");
        varsel.setVarseltittel("Varselemne");
        varsel.setVarseltekst("Du har mottatt et varsel på nav.no");

        return varsel;
    }
}
