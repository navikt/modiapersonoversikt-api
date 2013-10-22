package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume;

import no.nav.sbl.dialogarena.mottaksbehandling.ISvar;
import no.nav.sbl.dialogarena.mottaksbehandling.Mottaksbehandling;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.SporsmalOgSvar;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;

import no.nav.sbl.dialogarena.sporsmalogsvar.Melding;
import no.nav.modig.lang.collections.iter.PreparedIterable;
import no.nav.modig.lang.option.Optional;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;

import java.io.Serializable;
import java.util.List;

import static no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Transform.BEHANDLINGSID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Transform.SENSITIV;
import static no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Transform.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Transform.TRAAD_ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Transform.tilWsSvar;
import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;

public class Traader implements Serializable {

    private static final List<String> SPORSMAL_OG_SVAR = asList("SPORSMAL", "SVAR");

    private final Mottaksbehandling mottaksbehandling;
    private final HenvendelsePortType henvendelsesbehandling;

    public Traader(Mottaksbehandling mottaksbehandling, HenvendelsePortType henvendelsesbehandling) {
        this.mottaksbehandling = mottaksbehandling;
        this.henvendelsesbehandling = henvendelsesbehandling;
    }

    public void besvareSporsmal(Traad traad) {
        traad.ferdigSvar();
        Melding svar = traad.getSisteMelding();
        Record<ISvar> svaret = new Record<ISvar>()
                .with(ISvar.behandlingsId, svar.behandlingId)
                .with(ISvar.fritekst, svar.fritekst)
                .with(ISvar.sensitiv, traad.erSensitiv)
                .with(ISvar.tema, traad.getTema());
        mottaksbehandling.besvarSporsmal(svaret);
    }

    public Optional<Traad> hentTraad(String fnr, String oppgaveId) {
        Traad traad = null;
        for (Record<SporsmalOgSvar> sporsmalOgSvar : mottaksbehandling.hentSporsmalOgSvar(oppgaveId)) {
            traad = new Traad(sporsmalOgSvar.get(SporsmalOgSvar.tema), sporsmalOgSvar.get(SporsmalOgSvar.behandlingsid));

            PreparedIterable<WSHenvendelse> henvendelser = on(henvendelsesbehandling.hentHenvendelseListe(fnr, SPORSMAL_OG_SVAR))
                    .filter(where(TRAAD_ID, equalTo(sporsmalOgSvar.get(SporsmalOgSvar.traad))));
            traad.leggTil(henvendelser.map(TIL_MELDING));

            for (String sisteBehandlingId : optional(traad.getSisteMelding()).map(Melding.BEHANDLING_ID)) {
                traad.erSensitiv = henvendelser.filter(where(BEHANDLINGSID, equalTo(sisteBehandlingId))).head().map(SENSITIV).getOrElse(false);
            }
        }
        return optional(traad);

    }

}
