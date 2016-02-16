package no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt;

import no.nav.sbl.dialogarena.common.records.Key;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling;

public class Tema {

    public static final Key<String> TEMAKODE = new Key<>("TEMAKODE");
    public static final Key<Record<? extends GenerellBehandling>> SISTOPPDATERTEBEHANDLING = new Key<>("SISTOPPDATERTEBEHANDLING");

}
