package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

class LeggTilbakeDelvisSvarProps implements Serializable {

    private final String behandlingsId;
    private final Melding sporsmal;

    LeggTilbakeDelvisSvarProps(Melding sporsmal, String behandlingsId) {
        this.behandlingsId = behandlingsId;
        this.sporsmal = sporsmal;
    }

    Map<String, Object> lagProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("henvendelseId", behandlingsId);
        props.put("sporsmal", sporsmal.fritekst);
        props.put("traadId", sporsmal.traadId);
        props.put("fodselsnummer", sporsmal.fnrBruker);
        props.put("svarDelvisCallbackId", LeggTilbakeDelvisSvarPanel.SVAR_DELVIS_CALLBACK_ID);
        props.put("svarDelvisAvbrytId", LeggTilbakeDelvisSvarPanel.DELVIS_SVAR_AVBRYT);
        props.put("oppgaveId", sporsmal.oppgaveId);
        props.put("temagruppe", sporsmal.temagruppe);
        return props;
    }

}
