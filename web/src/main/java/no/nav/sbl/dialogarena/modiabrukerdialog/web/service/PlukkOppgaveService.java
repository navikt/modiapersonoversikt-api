package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.OppgaveBehandlingService;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;

import javax.annotation.Resource;
import javax.inject.Inject;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceAttribute;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class PlukkOppgaveService {

    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;

    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;

    @Resource(name = "pep")
    private EnforcementPoint pep;

    public Optional<WSOppgave> plukkOppgave(String temagruppe) {
        Optional<WSOppgave> wsOppgave = oppgaveBehandlingService.plukkOppgaveFraGsak(temagruppe);
        if (wsOppgave.isSome()) {
            if (saksbehandlerHarTilgangTilBrukersDiskresjonskode(wsOppgave.get())) {
                return wsOppgave;
            } else {
                return leggTilbakeOgPlukkNyOppgave(wsOppgave.get(), temagruppe);
            }
        } else {
            return none();
        }
    }

    private Optional<WSOppgave> leggTilbakeOgPlukkNyOppgave(WSOppgave wsOppgave, String temagruppe) {
        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                optional(wsOppgave.getOppgaveId()),
                "Saksbehandler har ikke tilgang til brukers diskresjonskode",
                null);
        return plukkOppgave(temagruppe);
    }

    private boolean saksbehandlerHarTilgangTilBrukersDiskresjonskode(WSOppgave wsOppgave) {
        String brukersDiskresjonskode =
                personKjerneinfoServiceBi.hentKjerneinformasjon(new HentKjerneinformasjonRequest(wsOppgave.getGjelder().getBrukerId()))
                        .getPerson().getPersonfakta().getDiskresjonskode();

        return isBlank(brukersDiskresjonskode) || pep.hasAccess(forRequest(resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code", brukersDiskresjonskode)));
    }
}
