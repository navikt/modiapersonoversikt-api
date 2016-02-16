package no.nav.sbl.dialogarena.saksoversikt.service.service;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.StatiskeLenker.DITTNAV_URL;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.StatiskeLenker.NAV_NO_ETTERSENDING;

public class MiljovariablerService {

    public Map<String, String> hentMiljovariabler() {
        HashMap<String, String> miljovariabler = new HashMap<>();

        miljovariabler.put("dittnav.url", DITTNAV_URL);
        miljovariabler.put("temasider.viktigavitelenke", getProperty("temasider.viktigavitelenke"));
        miljovariabler.put("ettersending.url", NAV_NO_ETTERSENDING);
        miljovariabler.put("behandlingsstatus.synlig.antallDager", getProperty("behandlingsstatus.synlig.antallDager"));

        return miljovariabler;
    }
}
