package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon;

public interface OrganisasjonEnhetKontaktinformasjonService {

    OrganisasjonEnhetKontaktinformasjon hentKontaktinformasjon(String enhetId);

}
