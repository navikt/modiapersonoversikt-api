package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.modig.lang.option.Optional;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;

import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TRAAD_ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.tilWsSvar;

public class BesvareService implements Serializable {

    private static final List<String> SPORSMAL_OG_SVAR = asList("SPORSMAL", "SVAR");

    private final BesvareHenvendelsePortType besvareHenvendelsePortType;
    private final HenvendelsePortType henvendelsePortType;

    public BesvareService(BesvareHenvendelsePortType besvareHenvendelsePortType, HenvendelsePortType henvendelsePortType) {
        this.besvareHenvendelsePortType = besvareHenvendelsePortType;
        this.henvendelsePortType = henvendelsePortType;
    }

    public void besvareSporsmal(Traad traad) {
        traad.ferdigSvar();
        besvareHenvendelsePortType.besvarSporsmal(tilWsSvar(traad.tema, traad.sensitiv).transform(traad.getSisteMelding()));
    }

    public Optional<Traad> hentTraad(String fnr, String oppgaveId) {
        Traad traad = null;
        for (WSSporsmalOgSvar sporsmalOgSvar : optional(besvareHenvendelsePortType.hentSporsmalOgSvar(oppgaveId))) {
            traad = new Traad();
            traad.tema = sporsmalOgSvar.getSporsmal().getTema();

            traad.leggTil(on(henvendelsePortType.hentHenvendelseListe(fnr, SPORSMAL_OG_SVAR))
                    .filter(where(TRAAD_ID, equalTo(sporsmalOgSvar.getSporsmal().getTraad()))).map(TIL_MELDING));
        }
        return optional(traad);

    }

}
