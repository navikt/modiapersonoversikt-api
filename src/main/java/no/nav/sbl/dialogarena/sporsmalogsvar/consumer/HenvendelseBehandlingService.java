package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import org.apache.commons.collections15.Predicate;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceAttribute;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.subjectAttribute;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.ID;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class HenvendelseBehandlingService {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    protected BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Inject
    private PersonKjerneinfoServiceBi kjerneinfo;
    @Inject
    @Named("pep")
    private EnforcementPoint pep;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    public List<Melding> hentMeldinger(final String fnr) {
        List<String> typer = asList(SPORSMAL.name(), SVAR.name(), REFERAT.name());

        return on(henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(typer)).getAny())
                .map(TIL_MELDING)
                .filter(new TilgangskontrollPredicate())
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

    public void merkSomKontorsperret(String fnr, TraadVM valgtTraad) {
        String enhet = getEnhet(fnr);
        List<String> ider = on(valgtTraad.getMeldinger()).map(ID).collect();

        behandleHenvendelsePortType.oppdaterKontorsperre(enhet, ider);
    }

    public void merkSomFeilsendt(TraadVM valgtTraad) {
        List<String> behandlingsIdListe = on(valgtTraad.getMeldinger()).map(ID).collect();
        behandleHenvendelsePortType.oppdaterTilKassering(behandlingsIdListe);
    }

    private String getEnhet(String fnr) {
        Person person = kjerneinfo.hentKjerneinformasjon(new HentKjerneinformasjonRequest(fnr)).getPerson();
        return person.getPersonfakta().getHarAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId();
    }

    private class TilgangskontrollPredicate implements Predicate<Melding> {
        @Override
        public boolean evaluate(Melding melding) {
            PolicyRequest kontorsperrePolicyRequest = forRequest(
                    actionId("kontorsperre"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", defaultString(melding.kontorsperretEnhet)));

            PolicyRequest temagruppePolicyRequest = forRequest(
                    actionId("temagruppe"),
                    resourceId(""),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:temagruppe", defaultString(melding.journalfortTema)));

            return (isBlank(melding.kontorsperretEnhet) || pep.hasAccess(kontorsperrePolicyRequest))
                    && (isBlank(melding.journalfortTema) || pep.hasAccess(temagruppePolicyRequest));
        }
    }
}
