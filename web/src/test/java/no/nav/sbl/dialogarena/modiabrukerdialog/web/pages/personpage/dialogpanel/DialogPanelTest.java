package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.Personnavn;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PersonPageMockContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo.Bruker.FALLBACK_FORNAVN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {PersonPageMockContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class DialogPanelTest extends WicketPageTest {

    public static final String FNR = "fnr";

    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;

    @Test
    public void starterPanelUtenFeil() {
        wicket.goToPageWith(new DialogPanel("id", FNR));
    }

    @Test
    public void skalViseFallbackFornavnDersomFornavnIkkeKanHentes() {
        HentKjerneinformasjonResponse response = new HentKjerneinformasjonResponse();
        response.setPerson(new Person());
        when(personKjerneinfoServiceBi.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(response);
        DialogPanel dialogPanel = new DialogPanel("id", FNR);

        GrunnInfo grunnInfo = (GrunnInfo) Whitebox.getInternalState(dialogPanel, "grunnInfo");

        assertThat(grunnInfo.bruker.fornavn.equals(FALLBACK_FORNAVN), is(true));
    }

    @Test
    public void skalViseFornavnDersomFornavnKanHentes() {
        String fornavn = "Fornavn";
        HentKjerneinformasjonResponse response = createHentKjerneinformasjonResponse(fornavn);
        when(personKjerneinfoServiceBi.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(response);
        DialogPanel dialogPanel = new DialogPanel("id", FNR);

        GrunnInfo grunnInfo = (GrunnInfo) Whitebox.getInternalState(dialogPanel, "grunnInfo");

        assertThat(grunnInfo.bruker.fornavn.equals(fornavn), is(true));
    }

    private HentKjerneinformasjonResponse createHentKjerneinformasjonResponse(String fornavn) {
        Personnavn personnavn = new Personnavn();
        personnavn.setFornavn(fornavn);

        Personfakta personfakta = new Personfakta();
        personfakta.setPersonnavn(personnavn);

        Person person = new Person();
        person.setPersonfakta(personfakta);

        HentKjerneinformasjonResponse response = new HentKjerneinformasjonResponse();
        response.setPerson(person);

        return response;
    }
}