package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import java.io.Serializable;
import java.util.List;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mottaksbehandling.ISvar;
import no.nav.sbl.dialogarena.mottaksbehandling.Mottaksbehandling;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.SporsmalOgSvar;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
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
    HenvendelsePortType henvendelsePortType;

    private final Mottaksbehandling mottaksbehandling;

    public BesvareService(final Mottaksbehandling mottaksbehandling, HenvendelsePortType henvendelsePortType) {
        this.henvendelsePortType = henvendelsePortType;
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
        Record<SporsmalOgSvar> sporsmalOgSvar = mottaksbehandling.hentSporsmalOgSvar(oppgaveId);
        if (sporsmalOgSvar == null) {
            return none();
        }
        List<WSHenvendelse> wsHenvendelser = on(henvendelsePortType.hentHenvendelseListe(fnr, asList("SPORSMAL", "SVAR"))).collect(NYESTE_FORST);

        BesvareSporsmalDetaljer besvareSporsmalDetaljer = new BesvareSporsmalDetaljer();
        besvareSporsmalDetaljer.tema = sporsmalOgSvar.get(SporsmalOgSvar.tema).toString();

        Svar svar = new Svar(sporsmalOgSvar.get(SporsmalOgSvar.behandlingsid), sporsmalOgSvar.get(SporsmalOgSvar.tema).toString(), sporsmalOgSvar.get(SporsmalOgSvar.svar), sporsmalOgSvar.get(SporsmalOgSvar.sensitiv));
        besvareSporsmalDetaljer.svar = svar;

        Sporsmal sporsmal = new Sporsmal(sporsmalOgSvar.get(SporsmalOgSvar.sporsmal), sporsmalOgSvar.get(SporsmalOgSvar.opprettet));
        besvareSporsmalDetaljer.sporsmal = sporsmal;


        besvareSporsmalDetaljer.tidligereDialog = on(wsHenvendelser)
                .filter(where(TRAAD_ID, equalTo(sporsmalOgSvar.get(SporsmalOgSvar.traad))))
                .filter(where(BEHANDLINGS_ID, not(equalTo(sporsmalOgSvar.get(SporsmalOgSvar.sporsmaletsBehandlingsId)))))
                .map(TIL_HENVENDELSE).collect();

        return optional(besvareSporsmalDetaljer);

    }
}
