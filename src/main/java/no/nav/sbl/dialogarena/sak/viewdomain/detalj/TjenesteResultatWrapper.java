package no.nav.sbl.dialogarena.sak.viewdomain.detalj;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class TjenesteResultatWrapper {

    public enum Feilmelding {
        UKJENT_FEIL("feilmelding.ukjent"),
        DOKUMENT_IKKE_FUNNET("feilmelding.dokumentikkefunnet"),
        DOKUMENT_IKKE_TILGJENGELIG("feilmelding.dokumentikketilgjengelig"),
        DOKUMENT_SLETTET("feilmelding.dokumentslettet"),
        SIKKERHETSBEGRENSNING("feilmelding.sikkerhetsbegrensning"),
        KORRUPT_PDF("feilmelding.korruptpdf");

        public final String enonicKey;

        Feilmelding(String enonicKey) {
            this.enonicKey = enonicKey;
        }
    }

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
