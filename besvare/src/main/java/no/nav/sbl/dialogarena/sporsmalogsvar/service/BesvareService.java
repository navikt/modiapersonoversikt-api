package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.modig.lang.collections.iter.PreparedIterable;
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
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.BEHANDLINGSID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.SENSITIV;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TRAAD_ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.tilWsSvar;

public class BesvareService implements Serializable {

    private static final List<String> SPORSMAL_OG_SVAR = asList("SPORSMAL", "SVAR");

    private final BesvareHenvendelsePortType mottaksbehandling;
    private final HenvendelsePortType henvendelsesbehandling;

    public BesvareService(BesvareHenvendelsePortType mottaksbehandling, HenvendelsePortType henvendelsesbehandling) {
        this.mottaksbehandling = mottaksbehandling;
        this.henvendelsesbehandling = henvendelsesbehandling;
    }

    public void besvareSporsmal(Traad traad) {
        traad.ferdigSvar();
        mottaksbehandling.besvarSporsmal(tilWsSvar(traad.getTema(), traad.erSensitiv()).transform(traad.getSisteMelding()));
    }

    public Optional<Traad> hentTraad(String fnr, String oppgaveId) {
        Traad traad = null;
        for (WSSporsmalOgSvar sporsmalOgSvar : optional(mottaksbehandling.hentSporsmalOgSvar(oppgaveId))) {
            traad = new Traad(sporsmalOgSvar.getSporsmal().getTema(), sporsmalOgSvar.getSvar().getBehandlingsId());

            PreparedIterable<WSHenvendelse> henvendelser = on(henvendelsesbehandling.hentHenvendelseListe(fnr, SPORSMAL_OG_SVAR))
                    .filter(where(TRAAD_ID, equalTo(sporsmalOgSvar.getSporsmal().getTraad())));
            traad.leggTil(henvendelser.map(TIL_MELDING));

            for (String sisteBehandlingId : optional(traad.getSisteMelding()).map(Melding.BEHANDLING_ID)) {
                traad.setSensitiv(henvendelser.filter(where(BEHANDLINGSID, equalTo(sisteBehandlingId))).head().map(SENSITIV).getOrElse(false));
            }
        }
        return optional(traad);

    }

}
