package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.TraadVisningProps;
import no.nav.sbl.dialogarena.time.Datoformat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

class LeggTilbakeDelvisSvarProps extends HashMap<String, Object> implements Serializable {

    LeggTilbakeDelvisSvarProps(Melding sporsmal, String henvendelseId, Map<Temagruppe, String> temagruppeMapping, GrunnInfo grunnInfo, final List<Melding> traad, String saksbehandlerValgtEnhet) {
        String temagruppe = temagruppeMapping.get(Temagruppe.valueOf(sporsmal.temagruppe));
        temagruppeMapping.remove(Temagruppe.valueOf(sporsmal.temagruppe));


        if(saksbehandlerValgtEnhet.startsWith("41")){
            put("knagger", asList("ks"));
        }

        put("henvendelseId", henvendelseId);
        put("sporsmal", sporsmal.getFritekst());
        put("traadId", sporsmal.traadId);
        put("fodselsnummer", sporsmal.fnrBruker);
        put("svarDelvisCallbackId", LeggTilbakeDelvisSvarPanel.SVAR_DELVIS_CALLBACK_ID);
        put("avbrytCallbackId", LeggTilbakeDelvisSvarPanel.AVBRYT_CALLBACK_ID);
        put("oppgaveId", sporsmal.oppgaveId);
        put("temagruppe", temagruppe);
        put("opprettetDato", Datoformat.kortMedTid(sporsmal.opprettetDato));
        put("temagruppeMapping", temagruppeMapping);
        put("grunnInfo", grunnInfo);
        putAll(new TraadVisningProps(traad));
    }

}
