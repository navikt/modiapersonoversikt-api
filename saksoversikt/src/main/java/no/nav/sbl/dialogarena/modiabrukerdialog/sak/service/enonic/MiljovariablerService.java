package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.enonic;

import java.util.HashMap;
import java.util.Map;

public class MiljovariablerService {

    public Map<String, String> hentMiljovariabler() {
        HashMap<String, String> miljovariabler = new HashMap<>();
        miljovariabler.put("temasider.viktigavitelenke", System.getProperty("TEMASIDER_VIKTIGAVITELENKE"));
        miljovariabler.put("behandlingsstatus.synlig.antallDager", System.getProperty("BEHANDLINGSSTATUS_SYNLIG_ANTALLDAGER"));
        return miljovariabler;
    }
}
