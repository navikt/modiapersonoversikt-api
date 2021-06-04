package no.nav.modiapersonoversikt.legacy.varsel.service;

import no.nav.modiapersonoversikt.legacy.varsel.domain.Varsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.ws.soap.SOAPFaultException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VarslerServiceImplTest {

    @Mock
    private BrukervarselV1 brukervarsel;

    @InjectMocks
    VarslerServiceImpl impl;

    @Test
    public void skalIkkeTryneHeleVerdenOmDetSkjerSoapFaults() throws HentVarselForBrukerUgyldigInput {
        when(brukervarsel.hentVarselForBruker(any(WSHentVarselForBrukerRequest.class))).thenThrow(SOAPFaultException.class);
        Optional<List<Varsel>> varsler = impl.hentAlleVarsler("10108000398");

        assertTrue(varsler.isEmpty());
    }
}
