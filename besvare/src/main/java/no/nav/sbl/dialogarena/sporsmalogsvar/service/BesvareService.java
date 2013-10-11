package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import java.io.Serializable;
import java.util.List;
import no.nav.modig.lang.option.Optional;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.BEHANDLINGS_ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.NYESTE_FORST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TIL_HENVENDELSE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TIL_SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TIL_SVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TIL_WSSVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TRAAD_ID;

public class BesvareService implements Serializable {

    BesvareHenvendelsePortType besvareHenvendelsePortType;
    HenvendelsePortType henvendelsePortType;

    public BesvareService(BesvareHenvendelsePortType besvareHenvendelsePortType, HenvendelsePortType henvendelsePortType) {
        this.besvareHenvendelsePortType = besvareHenvendelsePortType;
        this.henvendelsePortType = henvendelsePortType;
    }

    public void besvareSporsmal(Svar svar) {
        besvareHenvendelsePortType.besvarSporsmal(TIL_WSSVAR.transform(svar));
    }

    public Optional<BesvareSporsmalDetaljer> hentDetaljer(String fnr, String oppgaveId) {
        WSSporsmalOgSvar wsSporsmalOgSvar = besvareHenvendelsePortType.hentSporsmalOgSvar(oppgaveId);
        if (wsSporsmalOgSvar == null) {
            return none();
        }
        List<WSHenvendelse> wsHenvendelser = on(henvendelsePortType.hentHenvendelseListe(fnr, asList("SPORSMAL", "SVAR"))).collect(NYESTE_FORST);

        BesvareSporsmalDetaljer besvareSporsmalDetaljer = new BesvareSporsmalDetaljer();
        besvareSporsmalDetaljer.tema = wsSporsmalOgSvar.getSporsmal().getTema();
        besvareSporsmalDetaljer.svar = TIL_SVAR.transform(wsSporsmalOgSvar.getSvar());
        besvareSporsmalDetaljer.sporsmal = TIL_SPORSMAL.transform(wsSporsmalOgSvar.getSporsmal());


        besvareSporsmalDetaljer.tidligereDialog = on(wsHenvendelser)
                .filter(where(TRAAD_ID, equalTo(wsSporsmalOgSvar.getSporsmal().getTraad())))
                .filter(where(BEHANDLINGS_ID, not(equalTo(wsSporsmalOgSvar.getSporsmal().getBehandlingsId()))))
                .map(TIL_HENVENDELSE).collect();

        return optional(besvareSporsmalDetaljer);

    }
}
