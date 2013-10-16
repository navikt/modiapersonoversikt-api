package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.modig.lang.option.Optional;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;

import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.BEHANDLINGS_ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.NYESTE_FORST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TIL_HENVENDELSE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TIL_SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TIL_SVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TIL_WSSVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TRAAD_ID;

public class BesvareService implements Serializable {

    private static final List<String> SPORSMAL_OG_SVAR = asList("SPORSMAL", "SVAR");

    private final BesvareHenvendelsePortType besvareHenvendelsePortType;
    private final HenvendelsePortType henvendelsePortType;

    public BesvareService(BesvareHenvendelsePortType besvareHenvendelsePortType, HenvendelsePortType henvendelsePortType) {
        this.besvareHenvendelsePortType = besvareHenvendelsePortType;
        this.henvendelsePortType = henvendelsePortType;
    }

    public void besvareSporsmal(Svar svar) {
        besvareHenvendelsePortType.besvarSporsmal(TIL_WSSVAR.transform(svar));
    }

    public Optional<Traad> hentTraad(String fnr, String oppgaveId) {
        Traad traad = null;
        for (WSSporsmalOgSvar sporsmalOgSvar : optional(besvareHenvendelsePortType.hentSporsmalOgSvar(oppgaveId))) {
            traad = new Traad();
            traad.tema = sporsmalOgSvar.getSporsmal().getTema();
            traad.svar = TIL_SVAR.transform(sporsmalOgSvar.getSvar());
            traad.sporsmal = TIL_SPORSMAL.transform(sporsmalOgSvar.getSporsmal());

            List<WSHenvendelse> henvendelser = on(henvendelsePortType.hentHenvendelseListe(fnr, SPORSMAL_OG_SVAR)).collect(NYESTE_FORST);
            traad.tidligereDialog = on(henvendelser)
                .filter(where(TRAAD_ID, equalTo(sporsmalOgSvar.getSporsmal().getTraad())))
                .filter(where(BEHANDLINGS_ID, not(equalTo(sporsmalOgSvar.getSporsmal().getBehandlingsId()))))
                .map(TIL_HENVENDELSE).collect();
        }
        return optional(traad);

    }
}
