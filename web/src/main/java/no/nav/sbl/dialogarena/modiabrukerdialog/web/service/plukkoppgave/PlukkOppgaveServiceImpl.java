package no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;

import javax.annotation.Resource;
import javax.inject.Inject;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceAttribute;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;

public class PlukkOppgaveServiceImpl implements PlukkOppgaveService {

    public static final String ATTRIBUTT_ID_ANSVARLIG_ENHET = "urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet";
    public static final String ATTRIBUTT_ID_DISKRESJONSKODE = "urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code";
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;

    @Resource(name = "pep")
    private EnforcementPoint pep;

    @Override
    public Optional<Oppgave> plukkOppgave(Temagruppe temagruppe) {
        Optional<Oppgave> oppgave = oppgaveBehandlingService.plukkOppgaveFraGsak(temagruppe);
        if (oppgave.isSome()) {
            if (saksbehandlerHarTilgangTilBruker(oppgave.get())) {
                return oppgave;
            } else {
                return leggTilbakeOgPlukkNyOppgave(oppgave.get(), temagruppe);
            }
        } else {
            return none();
        }
    }

    @Override
    public boolean oppgaveErFerdigstilt(String oppgaveid) {
        return oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveid);
    }

    private Optional<Oppgave> leggTilbakeOgPlukkNyOppgave(Oppgave oppgave, Temagruppe temagruppe) {
        oppgaveBehandlingService.systemLeggTilbakeOppgaveIGsak(oppgave.oppgaveId, temagruppe);
        return plukkOppgave(temagruppe);
    }

    private boolean saksbehandlerHarTilgangTilBruker(Oppgave oppgave) {
        try {
            HentKjerneinformasjonRequest kjerneinfoRequest = new HentKjerneinformasjonRequest(oppgave.fnr);
            kjerneinfoRequest.setBegrunnet(true);

            Personfakta personfakta = personKjerneinfoServiceBi.hentKjerneinformasjon(kjerneinfoRequest).getPerson().getPersonfakta();

            String brukersDiskresjonskode = personfakta.getDiskresjonskode() == null ? "" : personfakta.getDiskresjonskode().getValue();
            String brukersEnhet = getBrukersEnhet(personfakta).orElse("");

            return pep.hasAccess(forRequest(resourceAttribute(ATTRIBUTT_ID_DISKRESJONSKODE, brukersDiskresjonskode)))
                    && pep.hasAccess(forRequest(actionId("les"), resourceAttribute(ATTRIBUTT_ID_ANSVARLIG_ENHET, brukersEnhet)));
        } catch (AuthorizationException e) {
            return false;
        }
    }

    private java.util.Optional<String> getBrukersEnhet(Personfakta personfakta) {
        return java.util.Optional.ofNullable(personfakta.getAnsvarligEnhet())
                .map(AnsvarligEnhet::getOrganisasjonsenhet)
                .map(Organisasjonsenhet::getOrganisasjonselementId);
    }
}
