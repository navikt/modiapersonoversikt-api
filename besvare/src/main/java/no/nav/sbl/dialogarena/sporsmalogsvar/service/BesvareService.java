package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mottaksbehandling.ISvar;
import no.nav.sbl.dialogarena.mottaksbehandling.Mottaksbehandling;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.SporsmalOgSvar;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;

import java.io.Serializable;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.TIL_HENVENDELSE;

public class BesvareService implements Serializable {
    private Mottaksbehandling mottaksbehandling;

    public BesvareService(final Mottaksbehandling mottaksbehandling) {
        this.mottaksbehandling = mottaksbehandling;
    }

    public void besvareSporsmal(Svar svar) {
        Record<ISvar> svaret = new Record<ISvar>()
                .with(ISvar.behandlingsId, svar.behandlingId)
                .with(ISvar.fritekst, svar.fritekst)
                .with(ISvar.sensitiv, svar.sensitiv)
                .with(ISvar.tema, svar.tema);
        mottaksbehandling.besvarSporsmal(svaret);
    }

    public Optional<BesvareSporsmalDetaljer> hentDetaljer(String fnr, String oppgaveId) {
        Optional<Record<SporsmalOgSvar>> hentet = mottaksbehandling.hentSporsmalOgSvar(oppgaveId);
        if (!hentet.isSome()) {
            return none();
        }
        Record<SporsmalOgSvar> sporsmalOgSvar = hentet.get();

        BesvareSporsmalDetaljer besvareSporsmalDetaljer = new BesvareSporsmalDetaljer();
        besvareSporsmalDetaljer.tema = sporsmalOgSvar.get(SporsmalOgSvar.tema).toString();
        besvareSporsmalDetaljer.svar = new Svar(sporsmalOgSvar.get(SporsmalOgSvar.behandlingsid), sporsmalOgSvar.get(SporsmalOgSvar.tema).toString(), sporsmalOgSvar.get(SporsmalOgSvar.svar), sporsmalOgSvar.get(SporsmalOgSvar.sensitiv));
        besvareSporsmalDetaljer.sporsmal = new Sporsmal(sporsmalOgSvar.get(SporsmalOgSvar.sporsmal), sporsmalOgSvar.get(SporsmalOgSvar.opprettet));
        besvareSporsmalDetaljer.tidligereDialog = on(mottaksbehandling.tidligereDialog(
                fnr,
                sporsmalOgSvar.get(SporsmalOgSvar.traad),
                sporsmalOgSvar.get(SporsmalOgSvar.sporsmaletsBehandlingsId)))
                .map(TIL_HENVENDELSE).collect();

        return optional(besvareSporsmalDetaljer);
    }
}
