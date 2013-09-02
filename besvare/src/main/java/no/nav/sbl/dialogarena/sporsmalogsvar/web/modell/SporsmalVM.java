package no.nav.sbl.dialogarena.sporsmalogsvar.web.modell;

import java.io.Serializable;
import org.joda.time.LocalDate;

public class SporsmalVM implements Serializable {

    public String behandlingsId, fritekst, overskrift;
    public LocalDate opprettetDato;
}
