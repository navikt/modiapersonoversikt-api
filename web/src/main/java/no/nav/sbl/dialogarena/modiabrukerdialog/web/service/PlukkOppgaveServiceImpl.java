package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;

import javax.annotation.Resource;
import javax.inject.Inject;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceAttribute;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class PlukkOppgaveServiceImpl implements PlukkOppgaveService {

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

            String brukersDiskresjonskode = defaultString(personfakta.getDiskresjonskode());
            String brukersEnhet = defaultString(personfakta.getHarAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId());

            return pep.hasAccess(forRequest(resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code", brukersDiskresjonskode)))
                    && pep.hasAccess(forRequest(actionId("les"), resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", brukersEnhet)));
        } catch (AuthorizationException e) {
            return false;
        }
    }
}
