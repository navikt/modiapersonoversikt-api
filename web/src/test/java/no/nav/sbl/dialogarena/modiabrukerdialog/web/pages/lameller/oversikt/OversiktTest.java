package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationTestContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.CacheConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.common.MDCOperations.MDC_CALL_ID;
import static no.nav.modig.common.MDCOperations.generateCallId;
import static no.nav.modig.common.MDCOperations.putToMDC;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class, CacheConfig.class, WicketTesterConfig.class})
public class OversiktTest extends TestSecurityBaseClass {

    @Inject
    private FluentWicketTester<?> fluentWicketTester;

    @Before
    public void setupMDC() {
        putToMDC(MDC_CALL_ID, generateCallId());
    }

    @Test
    public void skalOppretteOversikt() {
        fluentWicketTester.goToPageWith(new Oversikt("id", "fnr"))
                .should().containComponent(withId("lenker").and(ofType(LenkeWidget.class)))
                .should().containComponent(withId("sykepenger").and(ofType(SykepengerWidget.class)));
    }

}
