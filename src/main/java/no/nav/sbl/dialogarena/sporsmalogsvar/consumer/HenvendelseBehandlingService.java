package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.StandardKodeverk;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_OPPMOTE;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_TELEFON;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL_SKRIFTLIG;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_OPPMOTE;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_SKRIFTLIG;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_TELEFON;
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
    @Inject
    private StandardKodeverk standardKodeverk;

    public List<Melding> hentMeldinger(final String fnr) {
        List<String> typer = asList(
                SPORSMAL_SKRIFTLIG.name(),
                SVAR_SKRIFTLIG.name(),
                SVAR_OPPMOTE.name(),
                SVAR_TELEFON.name(),
                REFERAT_OPPMOTE.name(),
                REFERAT_TELEFON.name());

        return on(henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(typer)).getAny())
                .map(TIL_MELDING)
                .map(journalfortTemaTilTemanavn)
                .filter(kontorsperreTilgang)
                .map(journalfortTemaTilgang)
                .collect();
    }

    public void oppdaterJournalfortInformasjonIHenvendelse(Sak sak, String journalpostId, Melding melding) {
        behandleHenvendelsePortType.oppdaterJournalfortInformasjon(melding.id,
                new XMLJournalfortInformasjon()
                        .withJournalfortTema(sak.temaKode)
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

    private final Predicate<Melding> kontorsperreTilgang = new Predicate<Melding>() {
        @Override
        public boolean evaluate(Melding melding) {
            PolicyRequest kontorsperrePolicyRequest = forRequest(
                    actionId("kontorsperre"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", defaultString(melding.kontorsperretEnhet)));

            return isBlank(melding.kontorsperretEnhet) || pep.hasAccess(kontorsperrePolicyRequest);
        }
    };

    private final Transformer<Melding, Melding> journalfortTemaTilgang = new Transformer<Melding, Melding>() {
        @Override
        public Melding transform(Melding melding) {
            PolicyRequest temagruppePolicyRequest = forRequest(
                    actionId("temagruppe"),
                    resourceId(""),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(melding.journalfortTema)));

            if (!isBlank(melding.journalfortTema) && !pep.hasAccess(temagruppePolicyRequest)) {
                melding.fritekst = "";
            }

            return melding;
        }
    };

    private final Transformer<Melding, Melding> journalfortTemaTilTemanavn = new Transformer<Melding, Melding>() {
        @Override
        public Melding transform(Melding melding) {
            if (melding.journalfortTema != null) {
                String temaNavn = standardKodeverk.getArkivtemaNavn(melding.journalfortTema);
                melding.journalfortTemanavn = temaNavn != null ? temaNavn : melding.journalfortTema;
            }
            return melding;
        }
    };

}
