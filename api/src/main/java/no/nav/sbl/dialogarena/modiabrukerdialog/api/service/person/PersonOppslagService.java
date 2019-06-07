package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.person;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag.PersonOppslagResponse;

public interface PersonOppslagService {
    public PersonOppslagResponse hentPersonDokument(String fnr);
}
