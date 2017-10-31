package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.time.Datoformat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

class LeggTilbakeDelvisSvarProps extends HashMap<String, Object> implements Serializable {

    LeggTilbakeDelvisSvarProps(Melding sporsmal, String henvendelseId, Map<Temagruppe, String> temagruppeMapping) {
        String temagruppe = temagruppeMapping.get(Temagruppe.valueOf(sporsmal.temagruppe));
        temagruppeMapping.remove(Temagruppe.valueOf(sporsmal.temagruppe));

        put("henvendelseId", henvendelseId);
        put("sporsmal", sporsmal.fritekst);
        put("traadId", sporsmal.traadId);
        put("fodselsnummer", sporsmal.fnrBruker);
        put("svarDelvisCallbackId", LeggTilbakeDelvisSvarPanel.SVAR_DELVIS_CALLBACK_ID);
        put("avbrytCallbackId", LeggTilbakeDelvisSvarPanel.AVBRYT_CALLBACK_ID);
        put("oppgaveId", sporsmal.oppgaveId);
        put("temagruppe", temagruppe);
        put("opprettetDato", Datoformat.kortMedTid(sporsmal.opprettetDato));
        put("temagruppeMapping", temagruppeMapping);
    }
}