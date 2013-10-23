package no.nav.sbl.dialogarena.aktorid.service;

import java.util.HashMap;

public class AktorService {

    private HashMap<String, String> aktorIdMap = new HashMap<>();

    public AktorService() {
        aktorIdMap.put("01010091736", "69078469165827");
        aktorIdMap.put("06047848871", "29078469165474");
        aktorIdMap.put("23054549733", "79078469165571");
        aktorIdMap.put("15066849497", "19078469165809");
        aktorIdMap.put("06025800174", "69078469165205");
        aktorIdMap.put("01010090195", "Ukjent");
    }

    public String getAktorId(String fnr) {
        if (aktorIdMap.containsKey(fnr)) {
            return aktorIdMap.get(fnr);
        } else {
            return "";
        }
    }

}
