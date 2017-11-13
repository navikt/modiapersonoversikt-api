package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;

public interface GrunninfoService {

    public default GrunnInfo hentGrunninfo(String fnr) {
        return new GrunnInfo(hentBrukerInfo(fnr), hentSaksbehandlerInfo());
    }

    public GrunnInfo.Bruker hentBrukerInfo(String fnr);

    public GrunnInfo.Saksbehandler hentSaksbehandlerInfo();

    public GrunnInfo.SaksbehandlerNavn hentSaksbehandlerNavn();
}
