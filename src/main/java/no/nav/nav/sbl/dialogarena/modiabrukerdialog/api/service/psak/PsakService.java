package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;

import java.util.List;

public interface PsakService {

    public List<Sak> hentSakerFor(String fnr);
}
