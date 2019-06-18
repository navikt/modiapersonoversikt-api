package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.CmsSkrivestotte;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteSok;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.List;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static no.nav.kjerneinfo.consumer.fim.behandleperson.config.BehandlePersonEndpointConfig.TPS_BEHANDLEPERSON_V1_MOCK_KEY;
import static no.nav.kjerneinfo.consumer.organisasjon.OrganisasjonV4ConsumerConfig.ORGANISASJON_V4_MOCK_KEY;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.VarslingEndpointConfig.VARSLING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms.CmsSkrivestotteConfig.CMS_SKRIVESTOTTE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark.JoarkEndpointConfig.JOARK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.aktor.AktorEndpointConfig.AKTOER_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.arbeidsfordeling.ArbeidsfordelingV1EndpointConfig.NORG2_ARBEIDSFORDELING_V1_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.arena.arbeidogaktivitet.ArbeidOgAktivitetEndpointConfig.ARENA_ARBEIDOGATKIVITET_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.GsakTildelOppgaveV1EndpointConfig.GSAK_TILDEL_OPPGAVE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker.GsakSakV1EndpointConfig.GSAK_SAK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.henvendelsesoknader.HenvendelseSoknaderEndpointConfig.HENVENDELSESOKNADER_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.NORG_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.pensjonsak.PensjonSakEndpointConfig.PENSJONSAK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.sakogbehandling.SakOgBehandlingEndpointConfig.SAKOGBEHANDLING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.utbetaling.UtbetalingEndpointConfig.UTBETALING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.HenvendelseEndpointConfig.HENVENDELSE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.kodeverk.KodeverkV2EndpointConfig.KODEVERK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhet.OrganisasjonEnhetV2EndpointConfig.NORG2_ORGANISASJON_ENHET_V2_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon.OrganisasjonEnhetKontaktinformasjonV1EndpointConfig.NORG2_ORGANISASJON_ENHET_KONTAKTINFORMASJON_V1_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak.GsakOppgaveV3EndpointConfig.GSAK_V3_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock.ANTALLSAKER_PROPERTY;
import static no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerConsumerConfig.PLEIEPENGER_V1_MOCK_KEY;

public class MockSetupPage extends BasePage {

    private List<MockSetupModel> mockSetupModeller;
    private Model<String> antallSaker = new Model<>(getProperty(ANTALLSAKER_PROPERTY, "0"));

    @Inject
    private CmsSkrivestotte cmsSkrivestotte;
    @Inject
    private SkrivestotteSok skrivestotteSok;


    public MockSetupPage(PageParameters pageParameters) {
        super(pageParameters);
        mockSetupModeller = lagModeller();

        add(new ContextImage("modiaLogo", "svg/NAV-Modia-logo.svg"));
        Form form = new Form("velgMockForm") {
            @Override
            protected void onSubmit() {
                setProperty(ANTALLSAKER_PROPERTY, antallSaker.getObject());
                setResponsePage(HentPersonPage.class);
            }
        };

        form.add(
                createMockCheckBoxer(),
                new TextField<>("antallSaker", antallSaker),
                new AjaxLink("reindekserSkrivestotte") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        skrivestotteSok.indekser(cmsSkrivestotte.hentSkrivestotteTekster());
                    }
                });

        add(form);
    }

    private ListView<MockSetupModel> createMockCheckBoxer() {
        return new ListView<MockSetupModel>("radioliste", mockSetupModeller) {
            @Override
            protected void populateItem(final ListItem<MockSetupModel> item) {
                item.setOutputMarkupId(true);
                PropertyModel<Boolean> useMock = new PropertyModel<>(item.getModelObject(), "useMock");
                PropertyModel<Boolean> throwException = new PropertyModel<>(item.getModelObject(), "throwException");

                WebMarkupContainer avbruddvalgWrapper = new WebMarkupContainer("avbruddvalg-wrapper");
                avbruddvalgWrapper.add(visibleIf(useMock));
                avbruddvalgWrapper.add(new AjaxCheckBox("avbruddvalg", throwException) {
                    @Override
                    protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                        setProperty(item.getModelObject().getKey() + ".simulate.error", item.getModelObject().getThrowException());
                    }
                });

                item.add(
                        new Label("radiolabel", item.getModelObject().getServiceName()),
                        new AjaxCheckBox("mockvalg", useMock) {
                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                setProperty(item.getModelObject().getKey(), item.getModelObject().getMockProperty());
                                target.add(item);
                            }
                        }.setOutputMarkupId(true),
                        avbruddvalgWrapper
                );
            }
        };
    }

    private List<MockSetupModel> lagModeller() {
        return asList(
                new MockSetupModel("Utbetaling", UTBETALING_KEY),
                new MockSetupModel("Kodeverk", KODEVERK_KEY),
                new MockSetupModel("Kjerneinfo", KJERNEINFO_KEY),
                new MockSetupModel("Henvendelse", HENVENDELSE_KEY),
                new MockSetupModel("Gsak", GSAK_V3_KEY),
                new MockSetupModel("Gsak sak", GSAK_SAK_KEY),
                new MockSetupModel("Gsak tildel oppgave", GSAK_TILDEL_OPPGAVE_KEY),
                new MockSetupModel("Arena Arbeid og Aktivitet", ARENA_ARBEIDOGATKIVITET_KEY),
                new MockSetupModel("SakOgBehandling", SAKOGBEHANDLING_KEY),
                new MockSetupModel("AktoerId", AKTOER_KEY),
                new MockSetupModel("HenvendelseSoknader", HENVENDELSESOKNADER_KEY),
                new MockSetupModel("Joark", JOARK_KEY),
                new MockSetupModel("Joark InnsynJournalV2Service", JOARK_KEY),
                new MockSetupModel("NORG", NORG_KEY),
                new MockSetupModel("NORG2 OrganisasjonEnhetV2", NORG2_ORGANISASJON_ENHET_V2_KEY),
                new MockSetupModel("NORG2 OrganisasjonEnhetKontaktinformasjonV1", NORG2_ORGANISASJON_ENHET_KONTAKTINFORMASJON_V1_KEY),
                new MockSetupModel("NORG2 ArbeidsfordelingV1", NORG2_ARBEIDSFORDELING_V1_KEY),
                new MockSetupModel("CMS Skrivestøtte", CMS_SKRIVESTOTTE_KEY),
                new MockSetupModel("PensjonSak", PENSJONSAK_KEY),
                new MockSetupModel("Pleiepenger V1", PLEIEPENGER_V1_MOCK_KEY),
                new MockSetupModel("Organisasjon V4", ORGANISASJON_V4_MOCK_KEY),
                new MockSetupModel("Varsler", VARSLING_KEY),
                new MockSetupModel("BehandlePersonV1", TPS_BEHANDLEPERSON_V1_MOCK_KEY)
        );
    }

}