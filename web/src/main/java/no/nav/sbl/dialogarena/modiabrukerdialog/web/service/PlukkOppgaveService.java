package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.OppgaveBehandlingService;

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

    public Optional<Oppgave> plukkOppgave(String temagruppe) {
        Optional<Oppgave> oppgave = oppgaveBehandlingService.plukkOppgaveFraGsak(temagruppe);
        if (oppgave.isSome()) {
            if (saksbehandlerHarTilgangTilBrukersDiskresjonskode(oppgave.get())) {
                return oppgave;
            } else {
                return leggTilbakeOgPlukkNyOppgave(oppgave.get(), temagruppe);
            }
        } else {
            return none();
        }
    }

    private Optional<Oppgave> leggTilbakeOgPlukkNyOppgave(Oppgave oppgave, String temagruppe) {
        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                optional(oppgave.oppgaveId),
                "Saksbehandler har ikke tilgang til brukers diskresjonskode",
                null);
        return plukkOppgave(temagruppe);
    }

    private boolean saksbehandlerHarTilgangTilBrukersDiskresjonskode(Oppgave oppgave) {
        String brukersDiskresjonskode =
                personKjerneinfoServiceBi.hentKjerneinformasjon(new HentKjerneinformasjonRequest(oppgave.fnr))
                        .getPerson().getPersonfakta().getDiskresjonskode();

        return isBlank(brukersDiskresjonskode) || pep.hasAccess(forRequest(resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code", brukersDiskresjonskode)));
    }
}
