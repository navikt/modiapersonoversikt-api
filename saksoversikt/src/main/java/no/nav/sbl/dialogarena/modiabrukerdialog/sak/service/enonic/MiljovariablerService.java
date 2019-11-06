package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.enonic;

import java.util.HashMap;
import java.util.Map;

public class MiljovariablerService {

    public Map<String, String> hentMiljovariabler() {
        HashMap<String, String> miljovariabler = new HashMap<>();
        miljovariabler.put("TEMASIDER_VIKTIGAVITELENKE", System.getProperty("TEMASIDER_VIKTIGAVITELENKE"));
        miljovariabler.put("BEHANDLINGSSTATUS_SYNLIG_ANTALLDAGER", System.getProperty("BEHANDLINGSSTATUS_SYNLIG_ANTALLDAGER"));
        return miljovariabler;
    }
}
