package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.enonic;

import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class MiljovariablerService {

    public Map<String, String> hentMiljovariabler() {
        HashMap<String, String> miljovariabler = new HashMap<>();
        miljovariabler.put("temasider.viktigavitelenke", getRequiredProperty("temasider.viktigavitelenke"));
        miljovariabler.put("behandlingsstatus.synlig.antallDager", getRequiredProperty("behandlingsstatus.synlig.antallDager"));
        return miljovariabler;
    }
}
