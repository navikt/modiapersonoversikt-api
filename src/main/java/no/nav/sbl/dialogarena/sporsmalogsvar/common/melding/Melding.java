package no.nav.sbl.dialogarena.sporsmalogsvar.common.melding;

import no.nav.sbl.dialogarena.common.records.Key;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.common.records.Key.key;

// CHECKSTYLE:OFF
public interface Melding {

    Key<String> id                      = key("id"),
                        traadId         = key("traadId"),
                        tema            = key("tema"),
                        fritekst        = key("fritekst"),
                        journalfortSaksid = key("journalfortSaksid"),
                        journalfortTema = key("journalfortTema");
    Key<DateTime>       opprettetDato   = key("opprettetDato"),
                        lestDato        = key("lestDato"),
                        journalfortDato = key("journalfortDato");
    Key<Status>         status          = key("status");
    Key<Meldingstype>   type            = key("type");
}