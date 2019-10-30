package no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding;

import javax.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class TjenesteResultatWrapper {
    public Optional<Object> result;
    public Feilmelding feilmelding;
    public Status statuskode;
    public Map ekstraFeilInfo;

    public TjenesteResultatWrapper(Object pdfSomBytes) {
        this.result = ofNullable(pdfSomBytes);
    }

    public TjenesteResultatWrapper(Object pdfSomBytes, Status statuskode) {
        this.result = ofNullable(pdfSomBytes);
        this.statuskode = statuskode;
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding) {
        this(feilmelding, null, new HashMap<>());
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding, Status statuskode) {
        this(feilmelding, statuskode, new HashMap<>());
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding, Map ekstraFeilInfo ) {
        this(feilmelding, null, ekstraFeilInfo);
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding, Status statuskode, Map ekstraFeilInfo) {
        this.feilmelding = feilmelding;
        this.ekstraFeilInfo = ekstraFeilInfo;
        this.statuskode = statuskode;
        result = empty();
    }
}
