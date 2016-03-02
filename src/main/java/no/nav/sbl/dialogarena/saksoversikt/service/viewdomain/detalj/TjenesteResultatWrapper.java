package no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class TjenesteResultatWrapper {
    public Optional<Object> result;
    public Feilmelding feilmelding;

    public TjenesteResultatWrapper(Object pdfSomBytes) {
        this.result = ofNullable(pdfSomBytes);
    }

    public TjenesteResultatWrapper(Feilmelding feilmelding) {
        this.feilmelding = feilmelding;
        result = empty();
    }
}
