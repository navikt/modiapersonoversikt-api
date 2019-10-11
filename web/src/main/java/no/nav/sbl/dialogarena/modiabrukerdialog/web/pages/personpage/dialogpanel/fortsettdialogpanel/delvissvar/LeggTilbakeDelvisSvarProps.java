package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.TraadVisningProps;
import no.nav.sbl.dialogarena.time.Datoformat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class LeggTilbakeDelvisSvarProps extends HashMap<String, Object> implements Serializable {

    LeggTilbakeDelvisSvarProps(String henvendelseId, Map<Temagruppe, String> temagruppeMapping, final List<Melding> traad, SkrivestotteProps skrivestotteProps, boolean flereOppgaverIgjen) {
        Melding sporsmal = traad.get(0);
        String temagruppe = temagruppeMapping.get(Temagruppe.valueOf(sporsmal.temagruppe));
        temagruppeMapping.remove(Temagruppe.valueOf(sporsmal.temagruppe));

        put("skrivestotteprops", skrivestotteProps);
        put("henvendelseId", henvendelseId);
        put("sporsmal", sporsmal.getFritekst());
        put("traadId", sporsmal.traadId);
        put("fodselsnummer", sporsmal.fnrBruker);
        put("svarDelvisCallbackId", LeggTilbakeDelvisSvarPanel.SVAR_DELVIS_CALLBACK_ID);
        put("avbrytCallbackId", LeggTilbakeDelvisSvarPanel.AVBRYT_CALLBACK_ID);
        put("startNyDialogId", LeggTilbakeDelvisSvarPanel.START_NY_DIALOG_CALLBACK_ID);
        put("oppgaveId", sporsmal.oppgaveId);
        put("temagruppe", temagruppe);
        put("opprettetDato", Datoformat.kortMedTid(sporsmal.ferdigstiltDato));
        put("temagruppeMapping", temagruppeMapping);
        putAll(new TraadVisningProps(traad));
        if (flereOppgaverIgjen) {
            put("startNesteDialogId", LeggTilbakeDelvisSvarPanel.START_NESTE_DIALOG_CALLBACK_ID);
        }
    }

}
