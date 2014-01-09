package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts;

import no.nav.brukerprofil.ping.BrukerprofilPing;
import no.nav.kjerneinfo.kontrakter.oppfolging.loader.OppfolgingsLoader;
import no.nav.kjerneinfo.kontrakter.ping.KontrakterPing;
import no.nav.kjerneinfo.kontrakter.ytelser.YtelseskontrakterLoader;
import no.nav.kjerneinfo.ping.KjerneinfoPing;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.security.tilgangskontroll.policy.pdp.DecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.personsok.consumer.fim.kodeverk.KodeverkManager;
import no.nav.personsok.consumer.fim.kodeverk.support.KodeverkServiceDelegate;
import no.nav.personsok.consumer.fim.mapping.FIMMapper;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.personsok.consumer.utils.ping.PersonsokPing;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.TestBeans;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;

import static java.lang.System.setProperty;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ArtifactsConfig.class, TestBeans.class})
public class ArtifactsConfigTest {

    //PersonsokContext
    @Inject
    private PersonsokPortType personsokPortType;
    @Inject
    @Named("selftestPersonsokPortType")
    private PersonsokPortType selftestPersonsokPortType;
    //Consumerconfig
    @Inject
    private PersonsokServiceBi personsokServiceBi;
    @Inject
    private PersonsokPing personsokPing;
    @Inject
    private FIMMapper fimMapper;
    @Inject
    private KodeverkManager kodeverkManager;
    @Inject
    private KodeverkServiceDelegate kodeverkServiceDelegate;
    // SecurityPolicyConfing
    @Inject
    @Named("pep")
    private EnforcementPoint enforcementPoint;
    @Inject
    @Named("pdp")
    private DecisionPoint decisionPoint;
    //BrukerprofilPanelConfig
    @Inject
    private BrukerprofilPing brukerprofilPing;
    //BrukerprofilTilgangskontrollPolicyConfig
    @Inject
    @Named("kjerneinfoPep")
    private EnforcementPoint kjerneinfoPep;
    @Inject
    @Named("kjerneinfoPdp")
    private DecisionPoint kjerneinfoPdp;
    //KjerneinfoPanelConfig
    @Inject
    private KjerneinfoPing kjerneinfoPing;
    @Inject
    private OppfolgingsLoader oppfolgingsLoader;
    @Inject
    private KontrakterPing kontrakterPing;
    @Inject
    private YtelseskontrakterLoader ytelseskontrakterLoader;
    //ConsumerConfig
    @Inject
    private YtelseskontraktServiceBi ytelseskontraktServiceBi;
    @Inject
    private OppfolgingskontraktServiceBi oppfolgingskontraktServiceBi;
    @Inject
    private SykepengerServiceBi sykepengerServiceBi;
    @Inject
    private ForeldrepengerServiceBi foreldrepengerServiceBi;

    @BeforeClass
    public static void setupStatic() {
        setFrom("test.properties");
        setupKeyAndTrustStore();
        setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void shouldInjectBeans() {
        assertThat(personsokPortType, is(notNullValue()));
        assertThat(selftestPersonsokPortType, is(notNullValue()));
        assertThat(personsokServiceBi, is(notNullValue()));
        assertThat(personsokPing, is(notNullValue()));
        assertThat(fimMapper, is(notNullValue()));
        assertThat(kodeverkManager, is(notNullValue()));
        assertThat(kodeverkServiceDelegate, is(notNullValue()));
        assertThat(enforcementPoint, is(notNullValue()));
        assertThat(decisionPoint, is(notNullValue()));
        assertThat(brukerprofilPing, is(notNullValue()));
        assertThat(kjerneinfoPep, is(notNullValue()));
        assertThat(kjerneinfoPdp, is(notNullValue()));
        assertThat(kjerneinfoPing, is(notNullValue()));
        assertThat(oppfolgingsLoader, is(notNullValue()));
        assertThat(kontrakterPing, is(notNullValue()));
        assertThat(ytelseskontrakterLoader, is(notNullValue()));
        assertThat(ytelseskontraktServiceBi, is(notNullValue()));
        assertThat(oppfolgingskontraktServiceBi, is(notNullValue()));
        assertThat(sykepengerServiceBi, is(notNullValue()));
        assertThat(foreldrepengerServiceBi, is(notNullValue()));

    }
}
