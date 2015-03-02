package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.StandardKodeverk;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceAttribute;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.subjectAttribute;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.FEILSENDT;
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

    public List<Melding> hentMeldinger(String fnr) {
        return hentMeldinger(fnr, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
    }

    public List<Melding> hentMeldinger(String fnr, String valgtEnhet) {
        List<String> typer = asList(
                SPORSMAL_SKRIFTLIG.name(),
                SVAR_SKRIFTLIG.name(),
                SVAR_OPPMOTE.name(),
                SVAR_TELEFON.name(),
                REFERAT_OPPMOTE.name(),
                REFERAT_TELEFON.name(),
                SPORSMAL_MODIA_UTGAAENDE.name(),
                SVAR_SBL_INNGAAENDE.name());

        return on(henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(typer)).getAny())
                .map(TIL_MELDING)
                .map(journalfortTemaTilTemanavn)
                .filter(kontorsperreTilgang(valgtEnhet))
                .map(journalfortTemaTilgang(valgtEnhet))
                .collect();
    }

    public void merkSomKontorsperret(String fnr, TraadVM valgtTraad) {
        String enhet = getEnhet(fnr);
        List<String> ider = on(valgtTraad.getMeldinger()).map(ID).collect();

        behandleHenvendelsePortType.oppdaterKontorsperre(enhet, ider);
    }

    public void merkSomFeilsendt(TraadVM valgtTraad) {
        List<String> behandlingsIdListe = on(valgtTraad.getMeldinger()).filter(where(FEILSENDT, equalTo(false))).map(ID).collect();
        if (!behandlingsIdListe.isEmpty()) {
            behandleHenvendelsePortType.oppdaterTilKassering(behandlingsIdListe);
        }
    }

    public void merkSomBidrag(TraadVM valgtTraad) {
        behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(valgtTraad.getEldsteMelding().melding.traadId, "BID");
    }

    private String getEnhet(String fnr) {
        HentKjerneinformasjonRequest kjerneinfoRequest = new HentKjerneinformasjonRequest(fnr);
        kjerneinfoRequest.setBegrunnet(true);
        Person person = kjerneinfo.hentKjerneinformasjon(kjerneinfoRequest).getPerson();
        return person.getPersonfakta().getHarAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId();
    }

    private Predicate<Melding> kontorsperreTilgang(final String valgtEnhet) {
        return new Predicate<Melding>() {
            @Override
            public boolean evaluate(Melding melding) {
                PolicyRequest kontorsperrePolicyRequest = forRequest(
                        actionId("kontorsperre"),
                        resourceId(""),
                        subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(valgtEnhet)),
                        resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", defaultString(melding.kontorsperretEnhet)));

                return isBlank(melding.kontorsperretEnhet) || pep.hasAccess(kontorsperrePolicyRequest);
            }
        };
    }

    private Transformer<Melding, Melding> journalfortTemaTilgang(final String valgtEnhet) {
        return new Transformer<Melding, Melding>() {
            @Override
            public Melding transform(Melding melding) {
                PolicyRequest temagruppePolicyRequest = forRequest(
                        actionId("temagruppe"),
                        resourceId(""),
                        subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(valgtEnhet)),
                        resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(melding.journalfortTema)));

                if (!isBlank(melding.journalfortTema) && !pep.hasAccess(temagruppePolicyRequest)) {
                    melding.fritekst = "";
                    melding.ingenTilgangJournalfort = true;
                }

                return melding;
            }
        };
    }

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
