package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

import java.io.Serializable;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg.Aarsak.ANNEN;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg.Aarsak.INHABIL;

public class AarsakVM implements Serializable {

    private Aarsak valg;
    private String tekst;

    public AarsakVM(Aarsak aarsak) {
        this.valg = aarsak;
    }

    public Aarsak getValg() {
        return valg;
    }

    public void setValg(Aarsak valg) {
        this.valg = valg;
    }

    public Aarsak getInhabil() {
        return INHABIL;
    }

    public void setInhabil(Aarsak inhabil) {
        setValg(inhabil);
    }

    public Aarsak getAnnen() {
        return ANNEN;
    }

    public void setAnnen(Aarsak annen) {
        setValg(annen);
    }

    public String getAnnenAarsakTekst() {
        return tekst;
    }

    public void setAnnenAarsakTekst(String tekst) {
        this.tekst = tekst;
    }

    public String getAarsakForTilbakeleggelse() {
        if (valg == ANNEN) {
            return getAnnenAarsakTekst();
        }
        return valg.toString();
    }

}
