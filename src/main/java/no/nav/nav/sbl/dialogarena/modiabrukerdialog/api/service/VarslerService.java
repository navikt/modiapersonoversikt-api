package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Varsel;

import java.util.List;

public interface VarslerService {
    List<Varsel> hentAlleVarsler(String fnr);
}
