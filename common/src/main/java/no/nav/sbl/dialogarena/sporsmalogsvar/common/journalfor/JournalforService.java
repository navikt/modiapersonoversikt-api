package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor;

import no.nav.sbl.dialogarena.sporsmalogsvar.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Journalforing;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Sak;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSak;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerRequest;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerResponse;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;

public class JournalforService implements Serializable {

    private final BesvareHenvendelsePortType besvareHenvendelse;

    public JournalforService(BesvareHenvendelsePortType besvareHenvendelse) {
        this.besvareHenvendelse = besvareHenvendelse;
    }

    public Journalforing opprettJournalforing(String fnr, Traad traad) {
        HentSakerResponse hentSakerResponse = besvareHenvendelse.hentSaker(new HentSakerRequest().withBrukerId(fnr));
        return new Journalforing(traad, on(hentSakerResponse.getSaker()).map(TIL_SAK));
    }

    public void journalfor(Journalforing journalforing) {
        besvareHenvendelse.journalforMeldinger(
                on(journalforing.traad.getDialog()).map(tilWsMelding(journalforing.valgtSak.saksId, journalforing.valgtSak.temakode, journalforing.isSensitiv())).collect());
        journalforing.traad.setJournalforingkvittering(optional(new Traad.Journalforingkvittering(DateTime.now(), journalforing.valgtSak.saksId, journalforing.valgtSak.temakode)));
    }


    private static final Transformer<WSSak, Sak> TIL_SAK = new Transformer<WSSak, Sak>() {
        @Override
        public Sak transform(WSSak wsSak) {
            return new Sak(
                    wsSak.getSakId(),
                    wsSak.isGenerell() ? "Generell" : "Ikke generell",
                    wsSak.isGenerell() ? "Gsak" : "Pesys",
                    wsSak.getTemakode(), wsSak.getOpprettetDato(), wsSak.getStatuskode());
        }
    };

    public static Transformer<Melding, WSMelding> tilWsMelding(final String saksId, final String temakode, final boolean sensitiv) {
        return new Transformer<Melding, WSMelding>() {
            @Override
            public WSMelding transform(Melding melding) {
                return new WSMelding()
                        .withSaksId(saksId)
                        .withBehandlingsId(melding.behandlingId)
                        .withMeldingstype(melding.avsender)
                        .withOpprettetDato(melding.sendtDato)
                        .withArkivtema(temakode)
                        .withFritekst(melding.fritekst)
                        .withSensitiv(sensitiv);
            }
        };
    }

}
