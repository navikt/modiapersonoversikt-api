package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.modig.security.tilgangskontroll.URN;
import no.nav.modig.security.tilgangskontroll.policy.attributes.values.StringValue;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.modig.security.tilgangskontroll.policy.request.attributes.ActionAttribute;
import no.nav.modig.security.tilgangskontroll.policy.request.attributes.ResourceAttribute;
import no.nav.modig.security.tilgangskontroll.policy.request.attributes.SubjectAttribute;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;

public class HenvendelseBehandlingService {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    protected BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Inject
    private PersonKjerneinfoServiceBi kjerneinfo;
    @Inject
    @Named("pep")
    private EnforcementPoint enforcementPoint;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    public List<Melding> hentMeldinger(final String fnr) {
        List<String> typer = Arrays.asList(SPORSMAL.name(), SVAR.name(), REFERAT.name());


        return on(henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(typer)).getAny())
                .map(TIL_MELDING)
                .filter(lagPepFilter(fnr))
                .collect();
    }

    public void oppdaterJournalfortInformasjonIHenvendelse(Sak sak, String journalpostId, Melding melding) {
        behandleHenvendelsePortType.oppdaterJournalfortInformasjon(melding.id,
                new XMLJournalfortInformasjon()
                        .withJournalfortTema(sak.tema)
                        .withJournalfortDato(DateTime.now())
                        .withJournalpostId(journalpostId)
                        .withJournalfortSaksId(sak.saksId)
                        .withJournalforerNavIdent(getSubjectHandler().getUid())
        );
    }

    public void merkSomKontorsperret(String fnr, TraadVM traadVM) {
        String enhet = getEnhet(fnr);
        List<String> ider = on(traadVM.getMeldinger()).map(new Transformer<MeldingVM, String>() {
            @Override
            public String transform(MeldingVM meldingVM) {
                return meldingVM.melding.id;
            }
        }).collect();

        behandleHenvendelsePortType.oppdaterKontorsperre(enhet, ider);
    }

    private String getEnhet(String fnr) {
        Person person = kjerneinfo.hentKjerneinformasjon(new HentKjerneinformasjonRequest(fnr)).getPerson();
        return person.getPersonfakta().getHarAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId();
    }
    private Predicate<Melding> lagPepFilter(final String fnr) {
        return new Predicate<Melding>() {
            private PolicyRequest req = new PolicyRequest()
                    .copyAndAppend(new ActionAttribute(new URN("urn:oasis:names:tc:xacml:1.0:action:action-id"), new StringValue("kontorsperre")))
                    .copyAndAppend(new SubjectAttribute(new URN("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet"), new StringValue(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())))
                    .copyAndAppend(new ResourceAttribute(new URN("urn:oasis:names:tc:xacml:1.0:resource:resource-id"), new StringValue(fnr)));
            @Override
            public boolean evaluate(Melding melding) {
                if (melding.kontorsperretEnhet == null || melding.kontorsperretEnhet.isEmpty())return true;

                return enforcementPoint.hasAccess(req.copyAndAppend(new ResourceAttribute(
                        new URN("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet"),
                        new StringValue(melding.kontorsperretEnhet)))
                );
            }
        };
    }
}
