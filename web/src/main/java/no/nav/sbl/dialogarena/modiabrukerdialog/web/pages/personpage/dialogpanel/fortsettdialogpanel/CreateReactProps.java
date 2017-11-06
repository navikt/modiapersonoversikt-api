package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateReactProps {

    public static Map<String, Object> lagVisTidligereMeldingsDetaljerProps(List<Melding> traad) {
        return new HashMap<String, Object>(){{
            put("traad", traad.stream().map( ( melding ->
                    new HashMap<String,Object>() {
                        {
                            put("temagruppeNavn", melding.temagruppeNavn);
                            put("visningsDatoTekst", DateUtils.toString( melding.erDokumentMelding ?
                                    melding.ferdigstiltDato : melding.opprettetDato));
                            put("fritekst", melding.fritekst);
                            put("erDokumentMelding", melding.erDokumentMelding);
                            put("id", melding.id);
                            put("statusTekst", melding.statusTekst);
                            put("navIdent", melding.navIdent);
                            put("skrevetAv", melding.skrevetAv);
                            put("fnrBruker", melding.fnrBruker);
                            put("meldingstype", melding.meldingstype);
                        }
                    })).collect(Collectors.toList()));
        }};
    }
}