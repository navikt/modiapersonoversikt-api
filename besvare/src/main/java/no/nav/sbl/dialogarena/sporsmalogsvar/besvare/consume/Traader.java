package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume;

import java.io.Serializable;
import no.nav.modig.lang.collections.iter.PreparedIterable;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.meldinger.HentMeldingListe;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Transform.BEHANDLINGSID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Transform.SENSITIV;
import static no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Transform.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Transform.TRAAD_ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Transform.tilWsSvar;

public class Traader implements Serializable {

    private final BesvareHenvendelsePortType mottaksbehandling;
    private final HenvendelseMeldingerPortType henvendelseMeldingerPortType;

    public Traader(BesvareHenvendelsePortType mottaksbehandling, HenvendelseMeldingerPortType henvendelseMeldingerPortType) {
        this.mottaksbehandling = mottaksbehandling;
        this.henvendelseMeldingerPortType = henvendelseMeldingerPortType;
    }

    public void besvareSporsmal(Traad traad) {
        traad.ferdigSvar();
        mottaksbehandling.besvarSporsmal(tilWsSvar(traad.getTema(), traad.erSensitiv).transform(traad.getSisteMelding()));
    }

    public Optional<Traad> hentTraad(String fnr, String oppgaveId) {
        Traad traad = null;
        for (WSSporsmalOgSvar sporsmalOgSvar : optional(mottaksbehandling.hentSporsmalOgSvar(oppgaveId))) {
            traad = new Traad(sporsmalOgSvar.getSporsmal().getTema(), sporsmalOgSvar.getSvar().getBehandlingsId());

            PreparedIterable<WSMelding> meldinger = on(henvendelseMeldingerPortType.hentMeldingListe(new HentMeldingListe().withFodselsnummer(fnr)).getMelding())
                    .filter(where(TRAAD_ID, equalTo(sporsmalOgSvar.getSporsmal().getTraad())));
            traad.leggTil(meldinger.map(TIL_MELDING));

            for (String sisteBehandlingId : optional(traad.getSisteMelding()).map(Melding.BEHANDLING_ID)) {
                traad.erSensitiv = meldinger.filter(where(BEHANDLINGSID, equalTo(sisteBehandlingId))).head().map(SENSITIV).getOrElse(false);
            }
        }
        return optional(traad);

    }

}
