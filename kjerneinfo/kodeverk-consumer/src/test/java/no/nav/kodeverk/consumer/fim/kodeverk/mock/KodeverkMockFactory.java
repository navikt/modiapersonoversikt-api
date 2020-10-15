package no.nav.kodeverk.consumer.fim.kodeverk.mock;

import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.finnkodeverkliste.Kodeverk;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;


public class KodeverkMockFactory {

    public Kodeverk getMockFinnKodeverk(String navn, String eier, String versjonsnummer, int offsetDager) {
        Kodeverk kodeverk = new Kodeverk();
        kodeverk.setNavn(navn);
        kodeverk.setEier(eier);
        kodeverk.setVersjonsnummer(versjonsnummer);
        kodeverk.setVersjoneringsdato(LocalDate.parse("2020-10-13").toDateMidnight().plusDays(offsetDager));
        return kodeverk;
    }

    public XMLKodeverk getMockHentKodeverk(String navn, String eier, String versjonsnummer, int offsetDager) {
        XMLEnkeltKodeverk kodeverk = new XMLEnkeltKodeverk();
        kodeverk.setNavn(navn);
        kodeverk.setEier(eier);
        kodeverk.setVersjonsnummer(versjonsnummer);
        kodeverk.setVersjoneringsdato(LocalDate.parse("2020-10-13").toDateMidnight().plusDays(offsetDager));
        return kodeverk;
    }

    public XMLKode getMockKode(String id) {
        XMLKode kode = new XMLKode();
        kode.setNavn(id);
        kode.getTerm().add(getMockTerm("tekstobjektnavn" + id, "uri:#ETESRGsdsW%GWdsE" + id, "FI_fi", "Tekstekst" + id));
        kode.getTerm().add(getMockTerm("tekstobjektnavn2" + id, "uri:#ETESRGsdsW%GWdsE2" + id, "FI_fi2", "Tekstekst2" + id));
        kode.getGyldighetsperiode().add(getMockPeriode());
        return kode;
    }

    private XMLTerm getMockTerm(String navn, String uri, String spraak, String tekst) {
        XMLTerm term = new XMLTerm();
        XMLTekstobjekt tekstobjekt = getMockTekstobjekt(navn, uri, spraak, tekst);
        term.setBeskrivelse(tekstobjekt);
        term.setSpraak(spraak);
        term.setNavn(tekst + spraak);
        term.getGyldighetsperiode().add(getMockPeriode());
        return term;
    }

    private XMLTekstobjekt getMockTekstobjekt(String navn, String uri, String spraak, String tekst) {
        XMLTekstobjekt  tekstobjekt = new XMLTekstobjekt ();
        tekstobjekt.setNavn(navn);
        tekstobjekt.setUri(uri);
        tekstobjekt.getTekst().add(getMockTekst(spraak, tekst));
        return tekstobjekt;
    }

    private XMLTekst getMockTekst(String spraak, String tekst) {
        XMLTekst t = new XMLTekst();
        t.setSpraak(spraak);
        t.setTekst(tekst + spraak);

        return t;
    }

    public XMLPeriode getMockPeriode() {
        XMLPeriode p = new XMLPeriode();
        p.setFom(LocalDate.parse("2020-10-13").toDateMidnight().minusMonths(1));
        p.setTom(LocalDate.parse("2020-10-13").toDateMidnight().plusMonths(5));
        return p;
    }

}
