package no.nav.sbl.dialogarena.aktorid.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AktorServiceTest {

    @Mock
    private AktoerPortType aktoerPortType;
    @InjectMocks
    private AktorService aktorService = new AktorService();


    @Test
    public void testHentAktorId() throws Exception {
        when(aktoerPortType.hentAktoerIdForIdent(any(HentAktoerIdForIdentRequest.class))).thenReturn(createResponse());
        String aktorId = aktorService.getAktorId("fnr");
        assertThat(aktorId, is(equalTo("29078469165474")));
    }

    @Test(expected = SystemException.class)
    public void testAktorIdWithBadRequest() throws Exception {
        when(aktoerPortType.hentAktoerIdForIdent(badRequest())).thenReturn(createResponse());
        aktorService.getAktorId("fnr");
    }

    private HentAktoerIdForIdentRequest badRequest() {
        return new HentAktoerIdForIdentRequest();
    }

    private HentAktoerIdForIdentResponse createResponse() {
        HentAktoerIdForIdentResponse hentAktoerIdForIdentResponse = new HentAktoerIdForIdentResponse();
        hentAktoerIdForIdentResponse.setAktoerId("29078469165474");
        return hentAktoerIdForIdentResponse;
    }
}
