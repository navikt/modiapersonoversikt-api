package no.nav.modiapersonoversikt.commondomain.sak;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class TjenesteResultatWrapper {
    public Optional<Object> result;
    public Feilmelding feilmelding;
    public Integer statuskode;
    public Map<Object, Object> ekstraFeilInfo;

    public TjenesteResultatWrapper(Object pdfSomBytes) {
        this.result = ofNullable(pdfSomBytes);
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding) {
        this(feilmelding, null, new HashMap<>());
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding, Integer statuskode) {
        this(feilmelding, statuskode, new HashMap<>());
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding, Map<Object, Object> ekstraFeilInfo ) {
        this(feilmelding, null, ekstraFeilInfo);
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding, Integer statuskode, Map<Object, Object> ekstraFeilInfo) {
        this.feilmelding = feilmelding;
        this.ekstraFeilInfo = ekstraFeilInfo;
        this.statuskode = statuskode;
        result = empty();
    }
}
