package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.enonic;

import no.nav.sbl.util.EnvironmentUtils;

import java.util.HashMap;
import java.util.Map;

public class MiljovariablerService {

    public Map<String, String> hentMiljovariabler() {
        HashMap<String, String> miljovariabler = new HashMap<>();
        miljovariabler.put("temasider.viktigavitelenke", EnvironmentUtils.getRequiredProperty("TEMASIDER_VIKTIGAVITELENKE"));
        miljovariabler.put("behandlingsstatus.synlig.antallDager", EnvironmentUtils.getRequiredProperty("BEHANDLINGSSTATUS_SYNLIG_ANTALLDAGER"));
        return miljovariabler;
    }
}
