package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.person;

import no.nav.tjenester.person.oppslag.v1.domain.Persondokument;

public interface PersonOppslagService {
    public Persondokument hentPersonDokument(String fnr);
}
