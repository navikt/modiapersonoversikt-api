package no.nav.sbl.dialogarena.aktorid.service;

import java.util.HashMap;
import java.util.Map;

import static no.nav.modig.lang.option.Optional.optional;

public class AktorService {

    private Map<String, String> aktorIdMap = new HashMap<>();

    public AktorService() {
        aktorIdMap.put("01010091736", "69078469165827");
        aktorIdMap.put("06047848871", "29078469165474");
        aktorIdMap.put("23054549733", "79078469165571");
        aktorIdMap.put("15066849497", "19078469165809");
        aktorIdMap.put("06025800174", "69078469165205");
        aktorIdMap.put("01010090195", "Ukjent");
    }

    public String getAktorId(String fnr) {
        return optional(aktorIdMap.get(fnr)).getOrElse("");
    }

}
