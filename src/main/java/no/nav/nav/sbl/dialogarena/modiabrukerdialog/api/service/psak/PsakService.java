package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;

import java.util.List;

public interface PsakService {

    List<Sak> hentSakerFor(String fnr);
}
