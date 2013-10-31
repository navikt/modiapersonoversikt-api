package no.nav.sbl.dialogarena.sporsmalogsvar.common.melding;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.records.Key;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.records.Key.key;

// CHECKSTYLE:OFF
public interface Melding {

    Key<String>         id              = key("id"),
                        traadId         = key("traadId"),
                        tema            = key("tema"),
                        fritekst        = key("fritekst");
    Key<DateTime>       opprettetDato   = key("opprettetDato"),
                        lestDato        = key("lestDato");
    Key<Status>         status          = key("status");
    Key<Meldingstype>   type            = key("type");
}