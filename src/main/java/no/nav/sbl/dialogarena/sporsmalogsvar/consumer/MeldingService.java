package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.ArrayList;
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

    public void journalforTraad(TraadVM valgtTraad, Sak sak) {
        //TODO: implementer gsakintergrasjon
    }

    public List<Sak> hentSakerForBruker(String fnr){
        //TODO: implementer gsakintergrasjon
        Sak sak1 = new Sak();
        sak1.opprettetDato = DateTime.now().minusDays(1);
        sak1.saksId = "71972389639";
        sak1.tema = "Dagpenger";
        sak1.fagsak = "Arena";

        Sak sak2 = new Sak();
        sak2.opprettetDato = DateTime.now().minusDays(4);
        sak2.saksId = "71972359639";
        sak2.tema = "Arbeidsavklaring";
        sak2.fagsak = "V2";

        Sak sak3 = new Sak();
        sak3.opprettetDato = DateTime.now().minusDays(4);
        sak3.saksId = "71972356639";
        sak3.tema = "Individstønad";
        sak3.fagsak = "Infotrygd";

        return new ArrayList<>(Arrays.asList(sak1, sak2, sak3));
    }

}
