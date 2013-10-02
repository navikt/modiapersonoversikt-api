package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import no.nav.sbl.dialogarena.sporsmalogsvar.records.Key;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.sporsmalogsvar.records.Key.key;

// CHECKSTYLE:OFF
public interface Melding {

    Key<String>         id              = key("id"),
                        traadId         = key("traadId"),
                        tema            = key("tema"),
                        fritekst        = key("fritekst"),
                        avsenderId      = key("avsenderId");
    Key<DateTime>       opprettetDato   = key("opprettetDato"),
                        lestDato        = key("lestDato");
    Key<Status>         status          = key("status");
    Key<Meldingstype>   type            = key("type");
}
