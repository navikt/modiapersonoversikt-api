package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;


import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.PensjonSaker;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakForMangeForekomster;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakUgyldigInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PensjonSakerTest {

    private static final String FNR = "fnr";

    @Mock
    private PsakService psakService;

    @Mock
    private GsakKodeverk gsakKodeverk;

    @Mock
    private StandardKodeverk standardKodeverk;

    @InjectMocks
    private PensjonSaker pensjonSaker;


    @BeforeEach
    void setUp()  {
        initMocks(this);
    }

    @Test
    void transformererResponseTilSakslistePensjon() {
        Sak pensjon = new Sak();
        pensjon.temaKode = "PENS";
        Sak ufore = new Sak();
        ufore.temaKode = "UFO";
        List<Sak> pensjonssaker = asList(pensjon, ufore);
        when(psakService.hentSakerFor(FNR)).thenReturn(pensjonssaker);
        List<Sak> saksliste = pensjonSaker.hentPensjonSaker(FNR);

        assertThat(saksliste.size(), is(2));
        assertThat(saksliste.get(0).temaNavn, is("PENS"));
        assertThat(saksliste.get(1).temaNavn, is("UFO"));
    }
}
