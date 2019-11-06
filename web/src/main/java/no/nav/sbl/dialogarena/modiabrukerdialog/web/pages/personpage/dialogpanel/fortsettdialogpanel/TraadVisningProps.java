package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TraadVisningProps extends HashMap<String, Object> implements Serializable {

    public TraadVisningProps(final List<Melding> traad) {
        put("traad", traad.stream().map(this::meldingsvinsingsprops).collect(Collectors.toList()));
    }

    private Map<String, Object> meldingsvinsingsprops(Melding melding){
        HashMap<String, Object> meldingsProps = new HashMap<>();
        meldingsProps.put("temagruppeNavn",melding.temagruppeNavn);
        meldingsProps.put("visningsDatoTekst", DateUtils.toString(melding.ferdigstiltDato));
        meldingsProps.put("fritekst", getFritekster(melding));
        meldingsProps.put("erDokumentMelding", melding.erDokumentMelding);
        meldingsProps.put("id", melding.id);
        meldingsProps.put("statusTekst", melding.statusTekst);
        meldingsProps.put("navIdent", melding.navIdent);
        meldingsProps.put("skrevetAv", melding.getSkrevetAv());
        meldingsProps.put("fnrBruker", melding.fnrBruker);
        meldingsProps.put("meldingstype", melding.meldingstype);
        meldingsProps.put("opprettetDato", melding.opprettetDato);
        return meldingsProps;
    }

    private String getFritekster(Melding melding) {
        List<String> fritekster = melding.getFriteksterMedEldsteForst().stream()
                .map(Fritekst::getFritekst)
                .collect(Collectors.toList());
        return String.join("\n\n", fritekster);
    }

}
