package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceAttribute;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.slf4j.LoggerFactory.getLogger;

public class PlukkOppgaveService {

    private static final Logger LOG = getLogger(PlukkOppgaveService.class);

    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;

    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;

    @Resource(name = "pep")
    private EnforcementPoint pep;

    public Optional<Oppgave> plukkOppgave(String temagruppe) {
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

    public boolean oppgaveErFerdigstillt(String oppgaveid) {
        return oppgaveBehandlingService.oppgaveErFerdigstillt(oppgaveid);
    }

    private Optional<Oppgave> leggTilbakeOgPlukkNyOppgave(Oppgave oppgave, String temagruppe) {
        oppgaveBehandlingService.systemLeggTilbakeOppgaveIGsak(oppgave.oppgaveId);
        return plukkOppgave(temagruppe);
    }

    private boolean saksbehandlerHarTilgangTilBruker(Oppgave oppgave) {
        try {
            Personfakta personfakta = personKjerneinfoServiceBi.hentKjerneinformasjon(new HentKjerneinformasjonRequest(oppgave.fnr)).getPerson().getPersonfakta();

            String brukersDiskresjonskode = defaultString(personfakta.getDiskresjonskode());
            String brukersEnhet = defaultString(personfakta.getHarAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId());

            boolean harDiskresjonskodeTilgang = pep.hasAccess(forRequest(resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code", brukersDiskresjonskode)));
            boolean harLesTilgang = pep.hasAccess(forRequest(actionId("les"), resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", brukersEnhet)));

            if(harDiskresjonskodeTilgang && harLesTilgang) {
                return true;
            } else {
                boolean harLesMedBegrunnelseTilgang = pep.hasAccess(forRequest(actionId("lesMedBegrunnelse"), resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", brukersEnhet)));
                return (harDiskresjonskodeTilgang && harLesMedBegrunnelseTilgang);
            }
        } catch (AuthorizationException e) {
            return false;
        }
    }
}
