package no.nav.sbl.dialogarena.varsel.service;

import no.nav.melding.domene.brukerdialog.varsler.v1.VarslerPorttype;
import no.nav.melding.domene.brukerdialog.varsler.v1.meldinger.WSHentVarslerRequest;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.ws.soap.SOAPFaultException;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VarslerServiceImplTest {

    @Mock
    VarslerPorttype ws;

    @InjectMocks
    VarslerServiceImpl impl;

    @Test
    public void skalIkkeTryneHeleVerdenOmDetSkjerSoapFaults() {
        when(ws.hentVarsler(any(WSHentVarslerRequest.class))).thenThrow(SOAPFaultException.class);
        List<Varsel> varsler = impl.hentAlleVarsler("10108000398");

        assertNull(varsler);
    }
}