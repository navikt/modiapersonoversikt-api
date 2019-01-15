package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.mfn;

import no.nav.tjenester.person.oppslag.v1.domain.Persondokument;

public interface PersonOppslagService {
    public Persondokument hentPersonDokument(String fnr);
}
