package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.saker.Sak;

import java.util.List;

public interface PsakService {

    List<Sak> hentSakerFor(String fnr);
}