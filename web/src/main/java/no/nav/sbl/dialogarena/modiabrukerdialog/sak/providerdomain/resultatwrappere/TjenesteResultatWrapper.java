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
    public Integer statuskode;
    public Map ekstraFeilInfo;

    public TjenesteResultatWrapper(Object pdfSomBytes) {
        this.result = ofNullable(pdfSomBytes);
    }

    public TjenesteResultatWrapper(Object pdfSomBytes, Integer statuskode) {
        this.result = ofNullable(pdfSomBytes);
        this.statuskode = statuskode;
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding) {
        this(feilmelding, null, new HashMap<>());
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding, Integer statuskode) {
        this(feilmelding, statuskode, new HashMap<>());
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding, Map ekstraFeilInfo ) {
        this(feilmelding, null, ekstraFeilInfo);
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding, Integer statuskode, Map ekstraFeilInfo) {
        this.feilmelding = feilmelding;
        this.ekstraFeilInfo = ekstraFeilInfo;
        this.statuskode = statuskode;
        result = empty();
    }
}
