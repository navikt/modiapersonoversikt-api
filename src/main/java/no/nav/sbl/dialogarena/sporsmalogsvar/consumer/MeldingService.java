package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;

public class MeldingService {

    @Inject
    private HenvendelsePortType henvendelsePortType;

    public List<Melding> hentMeldinger(String fnr) {
        List<String> typer = Arrays.asList(SPORSMAL.name(), SVAR.name(), REFERAT.name());
        return on(henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(typer)).getAny()).map(TIL_MELDING).collect();
    }
}
