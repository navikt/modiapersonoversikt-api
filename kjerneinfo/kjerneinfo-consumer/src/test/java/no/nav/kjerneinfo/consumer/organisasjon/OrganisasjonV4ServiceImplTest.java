package no.nav.kjerneinfo.consumer.organisasjon;

import no.nav.kjerneinfo.domain.organisasjon.Organisasjon;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentNoekkelinfoOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSSammensattNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSUstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentNoekkelinfoOrganisasjonRequest;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentNoekkelinfoOrganisasjonResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrganisasjonV4ServiceImplTest {

    private static final String ORGNAVN = "Syverkiosken AS";
    private static final String ORGNUMMER = "123456789";

    private OrganisasjonV4ServiceImpl organisasjonV4ServiceImpl;
    private OrganisasjonV4 mock;

    @Before
    public void setUp() {
        mock = mock(OrganisasjonV4.class);
        organisasjonV4ServiceImpl = new OrganisasjonV4ServiceImpl(mock);
    }

    @Test
    public void hentNoekkelinfoMedGyldigOrgnummerReturnererNavn() throws Exception {
        when(mock.hentNoekkelinfoOrganisasjon(any(WSHentNoekkelinfoOrganisasjonRequest.class)))
                .thenReturn(new WSHentNoekkelinfoOrganisasjonResponse()
                        .withNavn(new WSUstrukturertNavn().withNavnelinje(ORGNAVN)));

        Optional<Organisasjon> organisasjon = organisasjonV4ServiceImpl.hentNoekkelinfo(ORGNUMMER);

        assertThat(organisasjon.isPresent(), is(true));
        assertThat(organisasjon.get().getNavn(), is(ORGNAVN));
    }

    @Test
    public void hentNoekkelinfoMedUgyldigOrgnummerGirEmpty() throws Exception {
        when(mock.hentNoekkelinfoOrganisasjon(any(WSHentNoekkelinfoOrganisasjonRequest.class)))
                .thenThrow(new HentNoekkelinfoOrganisasjonOrganisasjonIkkeFunnet());

        assertThat(organisasjonV4ServiceImpl.hentNoekkelinfo(ORGNUMMER).isPresent(), is(false));
    }

    @Test(expected = ClassCastException.class)
    public void hentNoekkelinfoMedFeilNavneklasseKaster() throws Exception {
        when(mock.hentNoekkelinfoOrganisasjon(any(WSHentNoekkelinfoOrganisasjonRequest.class)))
                .thenReturn(new WSHentNoekkelinfoOrganisasjonResponse()
                        .withNavn(new WSSammensattNavn() {}));

        organisasjonV4ServiceImpl.hentNoekkelinfo(ORGNUMMER);
    }

}
