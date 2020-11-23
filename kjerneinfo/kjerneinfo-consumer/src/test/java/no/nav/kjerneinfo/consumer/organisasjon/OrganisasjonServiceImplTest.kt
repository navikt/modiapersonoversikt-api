package no.nav.kjerneinfo.consumer.organisasjon;

import no.nav.kjerneinfo.domain.organisasjon.Organisasjon;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrganisasjonServiceImplTest {

    private static final String ORGNAVN = "Syverkiosken AS";
    private static final String ORGNUMMER = "123456789";

    private OrganisasjonService organisasjonService;
    private OrganisasjonV1RestClient mock;

    @Before
    public void setUp() {
        mock = mock(OrganisasjonV1RestClient.class);
        organisasjonService = new OrganisasjonServiceImpl(mock);
    }

    @Test
    public void hentNoekkelinfoMedGyldigOrgnummerReturnererNavn() {
        when(mock.hentKjernInfoFraRestClient(ORGNUMMER))
                .thenReturn(new OrganisasjonResponse(ORGNUMMER, new OrgNavn("", ORGNAVN)));

        Optional<Organisasjon> organisasjon = organisasjonService.hentNoekkelinfo(ORGNUMMER);

        assertThat(organisasjon.isPresent(), is(true));
        assertThat(organisasjon.get().getNavn(), is(ORGNAVN));
    }

    @Test
    public void hentNoekkelinfoMedUgyldigOrgnummerGirEmpty() {
        when(mock.hentKjernInfoFraRestClient(any()))
                .thenThrow(new Exception());

        assertThat(organisasjonService.hentNoekkelinfo(ORGNUMMER).isPresent(), is(false));
    }
}
