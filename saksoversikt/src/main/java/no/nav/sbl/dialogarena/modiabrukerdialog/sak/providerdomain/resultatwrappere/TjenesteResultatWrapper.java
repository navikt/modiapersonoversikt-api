package no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class TjenesteResultatWrapper {
    public Optional<Object> result;
    public Feilmelding feilmelding;
    public Map ekstraFeilInfo;

    public TjenesteResultatWrapper(Object pdfSomBytes) {
        this.result = ofNullable(pdfSomBytes);
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding) {
        this(feilmelding, new HashMap<>());
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding, Map ekstraFeilInfo) {
        this.feilmelding = feilmelding;
        this.ekstraFeilInfo = ekstraFeilInfo;
        result = empty();
    }
}
