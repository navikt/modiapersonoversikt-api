package no.nav.sbl.dialogarena.sak.service.enonic;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.getProperty;

public class MiljovariablerService {

    public Map<String, String> hentMiljovariabler() {
        HashMap<String, String> miljovariabler = new HashMap<>();
        miljovariabler.put("temasider.viktigavitelenke", getProperty("temasider.viktigavitelenke"));
        return miljovariabler;
    }
}
