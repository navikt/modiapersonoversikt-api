package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;

import javax.servlet.http.HttpServletRequest;

public interface GrunninfoService {

    public default GrunnInfo hentGrunninfo(HttpServletRequest request, String fnr) {
        return new GrunnInfo(hentBrukerInfo(fnr), hentSaksbehandlerInfo(request));
    }

    public GrunnInfo.Bruker hentBrukerInfo(String fnr);

    public GrunnInfo.Saksbehandler hentSaksbehandlerInfo(HttpServletRequest request);

    public GrunnInfo.SaksbehandlerNavn hentSaksbehandlerNavn();
}
