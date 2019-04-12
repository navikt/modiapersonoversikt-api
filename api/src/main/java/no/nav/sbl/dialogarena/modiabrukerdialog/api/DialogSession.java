package no.nav.sbl.dialogarena.modiabrukerdialog.api;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.*;

public class DialogSession implements Serializable {

    private static final String DIALOGSESSION = "dialogsession";

    private Temagruppe temagruppe;
    private List<Oppgave> plukkedeOppgaver = emptyList();
    private boolean oppgaverBlePlukket;
    private Oppgave oppgaveSomBesvares;
    private Oppgave oppgaveFraUrl;

    public static DialogSession read(Component component) {
        return read(component.getSession());
    }

    public static DialogSession read(Session session) {
        DialogSession dialogSession = (DialogSession) session.getAttribute(DIALOGSESSION);
        if (dialogSession != null) {
            return dialogSession;
        }
        dialogSession = new DialogSession();
        session.setAttribute(DIALOGSESSION, dialogSession);
        return dialogSession;
    }

    public DialogSession withPlukkedeOppgaver(List<Oppgave> oppgaver) {
        this.plukkedeOppgaver = oppgaver;
        if (!oppgaver.isEmpty()) {
            this.oppgaveSomBesvares = oppgaver.get(0);
            this.oppgaverBlePlukket = true;
        }
        return this;
    }

    public DialogSession withValgtTemagruppe(Temagruppe temagruppe) {
        this.temagruppe = temagruppe;
        return this;
    }

    public List<Oppgave> getPlukkedeOppgaver() {
        return plukkedeOppgaver;
    }

    public boolean oppgaverBlePlukket() {
        return this.oppgaverBlePlukket;
    }

    public DialogSession withOppgaverBlePlukket(boolean oppgaverBlePlukket) {
        this.oppgaverBlePlukket = oppgaverBlePlukket;
        return this;
    }

    public DialogSession withURLParametre(PageParameters pageParameters) {
        oppgaveFraUrl = new Oppgave(
                pageParameters.get(OPPGAVEID).toString(),
                pageParameters.get(FNR).toString(),
                pageParameters.get(HENVENDELSEID).toString()
        );
        if (!pageParameters.get(BESVARES).isEmpty()) {
            oppgaveSomBesvares = oppgaveFraUrl;
        }
        return this;
    }

    public void clearOppgaveSomBesvaresOgOppgaveFraUrl() {
        oppgaveFraUrl = null;
        oppgaveSomBesvares = null;
        oppgaverBlePlukket = false;
        plukkedeOppgaver = emptyList();
    }

    public Optional<Oppgave> getOppgaveSomBesvares() {
        return ofNullable(oppgaveSomBesvares);
    }

    public DialogSession withOppgaveSomBesvares(Oppgave oppgave) {
        oppgaveSomBesvares = oppgave;
        return this;
    }

    public Temagruppe getTemagruppe() {
        return temagruppe;
    }

    public Oppgave getOppgaveFraUrl() {
        return oppgaveFraUrl;
    }

    public boolean erKnyttetTilOppgave() {
        return oppgaverBlePlukket || oppgaveFraUrl != null || !plukkedeOppgaver.isEmpty();
    }

}
