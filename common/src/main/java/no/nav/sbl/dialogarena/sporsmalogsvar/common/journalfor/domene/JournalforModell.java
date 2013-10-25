package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene;

import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSak;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerRequest;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerResponse;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.Collections;

import static no.nav.modig.lang.collections.IterUtils.on;

public class JournalforModell extends LoadableDetachableModel<Journalforing> {
    private Traad traad;
    private final String fnr;

    private final BesvareHenvendelsePortType besvareHenvendelsePortType;

    public JournalforModell(Traad traad, String fnr, BesvareHenvendelsePortType besvareHenvendelsePortType) {
        this.traad = traad;
        this.fnr = fnr;
        this.besvareHenvendelsePortType = besvareHenvendelsePortType;
    }

    @Override
    protected Journalforing load() {
        HentSakerResponse hentSakerResponse = besvareHenvendelsePortType.hentSaker(new HentSakerRequest().withBrukerId(fnr));
        return new Journalforing(traad, on(hentSakerResponse.getSaker()).map(TIL_SAK));
    }

    private static final Transformer<WSSak, Sak> TIL_SAK = new Transformer<WSSak, Sak>() {
        @Override
        public Sak transform(WSSak wsSak) {
            return new Sak(wsSak.getSakId(), wsSak.isGenerell() ? "Generell" : "Ikke generell", "Gsak eller Pesys", "UFO", wsSak.getOpprettetDato());
        }
    };

    public void nullstill() {
        setObject(new Journalforing(traad, Collections.<Sak>emptyList()));
    }

}
