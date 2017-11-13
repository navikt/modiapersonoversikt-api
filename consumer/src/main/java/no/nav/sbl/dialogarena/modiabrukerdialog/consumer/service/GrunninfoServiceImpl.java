package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg2.OrganisasjonEnhetService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class GrunninfoServiceImpl implements GrunninfoService {

    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;
    @Inject
    private LDAPService ldapService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private OrganisasjonEnhetService organisasjonEnhetService;
    @Inject
    private AnsattService ansattService;

    public GrunnInfo.Bruker hentBrukerInfo(String fnr) {
        try {
            HentKjerneinformasjonRequest request = new HentKjerneinformasjonRequest(fnr);
            request.setBegrunnet(true);
            Personfakta personfakta = personKjerneinfoServiceBi.hentKjerneinformasjon(request).getPerson().getPersonfakta();

            return new GrunnInfo.Bruker(fnr)
                    .withPersonnavn(personfakta.getPersonnavn().getFornavn(), personfakta.getPersonnavn().getEtternavn())
                    .withEnhet(hentEnhet(personfakta));
        } catch (Exception e) {
            return new GrunnInfo.Bruker(fnr, "", "", "");
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

        final Optional<AnsattEnhet> ansattEnhet = organisasjonEnhetService.hentEnhetGittEnhetId(valgtEnhet);
        return new GrunnInfo.Saksbehandler(
                ansattEnhet.isSome() ? ansattEnhet.get().enhetNavn : "",
                saksbehandler.fornavn,
                saksbehandler.etternavn
        );
    }

    private String hentEnhet(Personfakta personfakta) {
        if (personfakta != null && personfakta.getHarAnsvarligEnhet() != null
                && personfakta.getHarAnsvarligEnhet().getOrganisasjonsenhet() != null
                && StringUtils.isNotEmpty(personfakta.getHarAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementNavn())) {
            return personfakta.getHarAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementNavn();
        } else {
            return "";
        }
    }
}
