package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.time.Datoformat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.unmodifiableMap;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.*;

class LeggTilbakeDelvisSvarProps implements Serializable {

    private final String behandlingsId;
    private final Melding sporsmal;

    public static final Map<Temagruppe, String> TEMAGRUPPE_MAP;

    static {
        HashMap<Temagruppe, String> map = new HashMap<>();
        map.put(ARBD, "Arbeid");
        map.put(FMLI, "Familie");
        map.put(HJLPM, "Hjelpemidler");
        map.put(BIL, "Hjelpemidler Bil");
        map.put(ORT_HJE, "Helsetjenester og ortopediske hjelpemidler");
        map.put(OVRG, "Øvrig");
        map.put(PENS, "Pensjon");
        map.put(UFRT, "Uføretrygd");
        TEMAGRUPPE_MAP = unmodifiableMap(map);
    }

    LeggTilbakeDelvisSvarProps(Melding sporsmal, String behandlingsId) {
        this.behandlingsId = behandlingsId;
        this.sporsmal = sporsmal;
    }

    Map<Temagruppe,String> lagTemagruppe(){
        Map<Temagruppe, String> temagruppeMap = new TreeMap<>();
        for(Map.Entry<Temagruppe,String> temagruppe : TEMAGRUPPE_MAP.entrySet()){
            if(!temagruppe.getKey().equals(Temagruppe.valueOf(sporsmal.temagruppe))) {
                temagruppeMap.put(temagruppe.getKey(), temagruppe.getValue());
            }
        }
        return temagruppeMap;
    }

    Map<String, Object> lagProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("henvendelseId", behandlingsId);
        props.put("sporsmal", sporsmal.fritekst);
        props.put("traadId", sporsmal.traadId);
        props.put("fodselsnummer", sporsmal.fnrBruker);
        props.put("svarDelvisCallbackId", LeggTilbakeDelvisSvarPanel.SVAR_DELVIS_CALLBACK_ID);
        props.put("avbrytCallbackId", LeggTilbakeDelvisSvarPanel.AVBRYT_CALLBACK_ID);
        props.put("oppgaveId", sporsmal.oppgaveId);
        props.put("temagruppe", TEMAGRUPPE_MAP.get(Temagruppe.valueOf(sporsmal.temagruppe)));
        props.put("opprettetDato", Datoformat.kortMedTid(sporsmal.opprettetDato));
        props.put("valgTemagrupper", lagTemagruppe());
        return props;
    }

}
