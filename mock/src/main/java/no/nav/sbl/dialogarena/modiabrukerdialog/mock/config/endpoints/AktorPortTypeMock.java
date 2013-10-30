package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AktorPortTypeMock {

    @Bean
    public AktoerPortType aktorPortType() {
        return new AktoerPortType() {

            private Map<String, String> aktorIdMap = new HashMap<>();

            {
                aktorIdMap.put("01010091736", "69078469165827");
                aktorIdMap.put("06047848871", "29078469165474");
                aktorIdMap.put("23054549733", "79078469165571");
                aktorIdMap.put("15066849497", "19078469165809");
                aktorIdMap.put("06025800174", "69078469165205");
                aktorIdMap.put("01010090195", "Ukjent");
            }

            @Override
            public HentAktoerIdForIdentResponse hentAktoerIdForIdent(HentAktoerIdForIdentRequest hentAktoerIdForIdentRequest) throws HentAktoerIdForIdentPersonIkkeFunnet {
                HentAktoerIdForIdentResponse response = new HentAktoerIdForIdentResponse();
                if(aktorIdMap.containsKey(hentAktoerIdForIdentRequest.getIdent())){
                    response.setAktoerId(aktorIdMap.get(hentAktoerIdForIdentRequest.getIdent()));
                }else{
                    //default
                    response.setAktoerId("29078469165474");
                }
                return response;

            }

            @Override
            public void ping() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

}
