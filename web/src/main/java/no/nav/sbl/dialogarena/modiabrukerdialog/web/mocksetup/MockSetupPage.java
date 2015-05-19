package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.CmsSkrivestotte;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteSok;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;
import java.util.List;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms.CmsEndpointConfig.CMS_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms.CmsSkrivestotteConfig.CMS_SKRIVESTOTTE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.aktor.AktorEndpointConfig.AKTOER_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.arena.arbeidogaktivitet.ArbeidOgAktivitetEndpointConfig.ARENA_ARBEIDOGATKIVITET_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker.GsakSakV1EndpointConfig.GSAK_SAK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.ruting.GsakRutingEndpointConfig.GSAK_RUTING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.henvendelsesoknader.HenvendelseSoknaderEndpointConfig.HENVENDELSESOKNADER_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.NORG_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.sakogbehandling.SakOgBehandlingEndpointConfig.SAKOGBEHANDLING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.utbetaling.UtbetalingEndpointConfig.UTBETALING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.HenvendelseEndpointConfig.HENVENDELSE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.kodeverk.KodeverkV2EndpointConfig.KODEVERK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak.GsakOppgaveV3EndpointConfig.GSAK_V3_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock.ANTALLSAKER_PROPERTY;

public class MockSetupPage extends BasePage {

    private List<MockSetupModel> mockSetupModeller;
    private Model<String> antallSaker = new Model<>(getProperty(ANTALLSAKER_PROPERTY, "0"));

    @Inject
    private CmsSkrivestotte cmsSkrivestotte;
    @Inject
    private SkrivestotteSok skrivestotteSok;


    public MockSetupPage() {
        mockSetupModeller = lagModeller();

        add(new ContextImage("modiaLogo", "img/modiaLogo.svg"));
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
                item.add(
                        new Label("radiolabel", item.getModelObject().getServiceName()),
                        new AjaxCheckBox("mockvalg", new PropertyModel<Boolean>(item.getModelObject(), "useMock")) {
                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                setProperty(item.getModelObject().getKey(), item.getModelObject().getMockProperty());
                            }
                        }.setOutputMarkupId(true)
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
                new MockSetupModel("Gsak ruting", GSAK_RUTING_KEY),
                new MockSetupModel("Arena Arbeid og Aktivitet", ARENA_ARBEIDOGATKIVITET_KEY),
                new MockSetupModel("SakOgBehandling", SAKOGBEHANDLING_KEY),
                new MockSetupModel("AktoerId", AKTOER_KEY),
                new MockSetupModel("HenvendelseSoknader", HENVENDELSESOKNADER_KEY),
                new MockSetupModel("NORG", NORG_KEY),
                new MockSetupModel("CMS Skrivestøtte", CMS_SKRIVESTOTTE_KEY),
                new MockSetupModel("CMS", CMS_KEY)
        );
    }

}