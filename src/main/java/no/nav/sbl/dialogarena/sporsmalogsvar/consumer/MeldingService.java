package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.meldinger.HentMeldingListe;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;

public class MeldingService {

    @Inject
    private HenvendelseMeldingerPortType henvendelseMeldingerPortType;

    public List<Melding> hentMeldinger(String fnr) {
        return on(henvendelseMeldingerPortType.hentMeldingListe(new HentMeldingListe().withFodselsnummer(fnr)).getMelding()).map(TIL_MELDING).collect();
    }
}
