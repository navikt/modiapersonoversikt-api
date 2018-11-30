package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Optional;

import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;

public class GrunninfoServiceImpl implements GrunninfoService {

    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;
    @Inject
    private LDAPService ldapService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private OrganisasjonEnhetV2Service organisasjonEnhetService;
    @Inject
    private AnsattService ansattService;

    public GrunnInfo.Bruker hentBrukerInfo(String fnr) {
        try {
            HentKjerneinformasjonRequest request = new HentKjerneinformasjonRequest(fnr);
            request.setBegrunnet(true);
            Personfakta personfakta = personKjerneinfoServiceBi.hentKjerneinformasjon(request).getPerson().getPersonfakta();

            return new GrunnInfo.Bruker(fnr)
                    .withPersonnavn(personfakta.getPersonnavn().getFornavn(), personfakta.getPersonnavn().getEtternavn())
                    .withEnhet(hentEnhet(personfakta))
                    .withGeografiskTilknytning(personfakta.getGeografiskTilknytning() != null ? personfakta.getGeografiskTilknytning().getValue() : "")
                    .withDiskresjonskode(personfakta.getDiskresjonskode() != null ? personfakta.getDiskresjonskode().getKodeRef() : "")
                    .withKjonn(personfakta.getKjonn() != null ? personfakta.getKjonn().getKodeRef() : "");
        } catch (Exception e) {
            return new GrunnInfo.Bruker(fnr, "", "", "", "", "", "");
        }
    }

    public GrunnInfo.SaksbehandlerNavn hentSaksbehandlerNavn() {
        Person saksbehandler = ldapService.hentSaksbehandler(getSubjectHandler().getUid());
        return new GrunnInfo.SaksbehandlerNavn(
                saksbehandler.fornavn,
                saksbehandler.etternavn
        );
    }

    public GrunnInfo.Saksbehandler hentSaksbehandlerInfo() {
        Person saksbehandler = ldapService.hentSaksbehandler(getSubjectHandler().getUid());
        String valgtEnhet = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();

        final Optional<AnsattEnhet> ansattEnhet = organisasjonEnhetService.hentEnhetGittEnhetId(valgtEnhet, OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.UFILTRERT);
        return new GrunnInfo.Saksbehandler(
                ansattEnhet.map(enhet -> enhet.enhetNavn).orElse(""),
                saksbehandler.fornavn,
                saksbehandler.etternavn
        );
    }

    private String hentEnhet(Personfakta personfakta) {
        if (personfakta != null && personfakta.getAnsvarligEnhet() != null
                && personfakta.getAnsvarligEnhet().getOrganisasjonsenhet() != null
                && StringUtils.isNotEmpty(personfakta.getAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementNavn())) {
            return personfakta.getAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementNavn();
        } else {
            return "";
        }
    }
}
