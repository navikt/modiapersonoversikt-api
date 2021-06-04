package no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.to.informasjon;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;

import java.io.Serializable;
import java.util.Map;

public class Kode extends Kodeverkselement implements Serializable {
    // Termens språk brukes som nøkkel

    private Map<String, Term> term;

    public Term getTermForSpraak(String spraak) {
        return term.get(spraak);
    }

    public Map<String, Term> getTerm() {
        return term;
    }

    public void setTerm(Map<String, Term> term) {
        this.term = term;
    }

    public boolean erGyldig() {
        for (Periode periode : gyldighetsperiode) {
            if (periode.erGyldig()) {
                return true;
            }
        }
        return false;
    }
}
