package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;

import java.util.Collection;

public interface PsakService {

    public Collection<? extends Sak> hentSakerFor(String fnr);
}
