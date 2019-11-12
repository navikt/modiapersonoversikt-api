package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import org.apache.wicket.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;

@ExtendWith(SpringExtension.class)
public class OppgaveTilknytningPanelTest extends WicketPageTest {

    private final GrunnInfo grunnInfo = new GrunnInfo(new GrunnInfo.Bruker("", "", "", "", "", "", "", ""), new GrunnInfo.Saksbehandler("", "", ""));

    @Test
    public void viserOppgaveTilknytningPopup() throws Exception {
        wicket.goToPageWith(new OppgaveTilknytningPanel("id", Model.of(new HenvendelseVM()), grunnInfo))
                .should().containComponent(thatIsInvisible().withId("oppgaveTilknytningPopup"))
                .click().link(withId("aapnePopup"))
                .should().containComponent(thatIsVisible().withId("oppgaveTilknytningPopup"));
    }

    @Test
    public void skjulerOppgaveTilknytningPopup() {
        wicket.goToPageWith(new OppgaveTilknytningPanel("id", Model.of(new HenvendelseVM()), grunnInfo))
                .should().containComponent(thatIsInvisible().withId("oppgaveTilknytningPopup"))
                .click().link(withId("aapnePopup"))
                .should().containComponent(thatIsVisible().withId("oppgaveTilknytningPopup"))
                .click().link(withId("lukkPopup"))
                .should().containComponent(thatIsInvisible().withId("oppgaveTilknytningPopup"));
    }
}