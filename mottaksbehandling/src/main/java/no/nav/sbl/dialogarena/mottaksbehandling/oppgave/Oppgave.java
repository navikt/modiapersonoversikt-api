package no.nav.sbl.dialogarena.mottaksbehandling.oppgave;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Key;

import javax.xml.datatype.XMLGregorianCalendar;

// Små bokstaver i konstanter...
// CHECKSTYLE:OFF
public interface Oppgave {
    Key<String>
        id = new Key<>("id"),
        behandlingsid = new Key<>("behandlingsid"),
        fodselsnummer = new Key<>("fodselsnummer");

    Key<Optional<String>>
        saksbehandlerid = new Key<>("saksbehandlerid"),
        beskrivelse = new Key<>("beskrivelse");

    Key<Boolean> ferdigstilt = new Key<>("ferdigstilt");
    Key<Tema> tema = new Key<>("tema");
    Key<XMLGregorianCalendar> aktivFra = new Key<>("aktivFra");
    Key<Integer> versjon = new Key<>("versjon");
}
//CHECKSTYLE:ON