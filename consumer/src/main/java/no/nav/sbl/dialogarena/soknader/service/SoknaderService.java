package no.nav.sbl.dialogarena.soknader.service;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;

import java.io.Serializable;
import java.util.List;

public interface SoknaderService extends Serializable {

    String SOKNAD = "soknader";

    List<Soknad> getSoknader(String fnr);
}
