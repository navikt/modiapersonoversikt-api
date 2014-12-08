package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class AktoerPortTypeMock {

    /**
     *
     *
     * NB! HUSK Å SETTE FØDSELSNUMMERKOBLING I PersonKjerneinfoServiceBiMock.java
     *
     * FORMAT: AKTØRID //FNR
     *
     */

//    public static final String AKTOER_ID_MOCK = "1000042149824"; // 15065933818
//    public static final String AKTOER_ID_MOCK = "***REMOVED***"; // 09076841419
//    public static final String AKTOER_ID_MOCK = "1000049440885"; // 18108338799
//    public static final String AKTOER_ID_MOCK = "1000009921291"; // 31018322148
    public static final String AKTOER_ID_MOCK = "***REMOVED***"; // ***REMOVED*** (ikke NPE)


    @Bean
    public AktoerPortType getAktoerPortTypeMock() {
        AktoerPortType mock = mock(AktoerPortType.class);
        try {
            HentAktoerIdForIdentResponse response = new HentAktoerIdForIdentResponse();
            response.setAktoerId(AKTOER_ID_MOCK);
            when(mock.hentAktoerIdForIdent(any(HentAktoerIdForIdentRequest.class))).thenReturn(response);
        } catch (HentAktoerIdForIdentPersonIkkeFunnet hentAktoerIdForIdentPersonIkkeFunnet) {
            throw new ApplicationException("Mock klarte ikke å returnere aktørid", hentAktoerIdForIdentPersonIkkeFunnet);
        }
        return mock;
    }
}