package no.nav.sbl.dialogarena.besvare.consumer;

import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSvar;
import org.apache.commons.collections15.Transformer;

import static no.nav.modig.lang.collections.IterUtils.on;

public class MeldingService implements Serializable {

    @Inject
    private SporsmalOgSvarPortType webservice;

    public List<Melding> hentAlleMeldinger(String aktorId) {
        return on(webservice.hentMeldingListe(aktorId)).map(TIL_MELDING).collect();
    }

    public SporsmalOgSvar plukkMelding() {
        return TIL_SPORSMALOGSVAR.transform(webservice.plukkMeldingForBesvaring());
    }

    public void besvar(Svar svar) {
        webservice.besvarSporsmal(TIL_WSSVAR.transform(svar));
    }

    private static final Transformer<WSMelding, Melding> TIL_MELDING = new Transformer<WSMelding, Melding>() {
        @Override
        public Melding transform(WSMelding wsMelding) {
            return new Melding()
                    .withId(wsMelding.getId())
                    .withTraadId(wsMelding.getTraadId())
                    .withOpprettet(wsMelding.getOpprettet().toLocalDate())
                    .withType(Meldingstype.valueOf(wsMelding.getType().toString()))
                    .withTema(wsMelding.getTema())
                    .withOverskrift(wsMelding.getOverskrift())
                    .withFritekst(wsMelding.getFritekst());
        }
    };

    private static final Transformer<WSSporsmalOgSvar, SporsmalOgSvar> TIL_SPORSMALOGSVAR = new Transformer<WSSporsmalOgSvar, SporsmalOgSvar>() {
        @Override
        public SporsmalOgSvar transform(WSSporsmalOgSvar wsSporsmalOgSvar) {
            return new SporsmalOgSvar().withSporsmal(TIL_MELDING.transform(wsSporsmalOgSvar.getSporsmal())).withSvar(TIL_MELDING.transform(wsSporsmalOgSvar.getSvar()));
        }
    };

    private static final Transformer<Svar, WSSvar> TIL_WSSVAR = new Transformer<Svar, WSSvar>() {
        @Override
        public WSSvar transform(Svar svar) {
            return new WSSvar().withBehandlingsId(svar.id).withTema(svar.tema).withOverskrift(svar.overskrift).withFritekst(svar.fritekst).withSensitiv(svar.sensitiv);
        }
    };

}
