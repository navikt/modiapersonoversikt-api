package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import static no.nav.modig.lang.collections.IterUtils.on;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSvar;

import org.apache.commons.collections15.Transformer;

public class MeldingService implements Serializable {

    @Inject
    HenvendelsePortType henvendelseWebservice;

    @Inject
    private SporsmalOgSvarPortType webservice;

    public String stillSporsmal(String fritekst, String overskrift, String tema, String aktorId) {
        return webservice.opprettSporsmal(new WSSporsmal().withFritekst(fritekst).withTema(tema).withOverskrift(overskrift), aktorId);
    }

    public List<Melding> hentAlleMeldinger(String aktorId) {
    	Transformer<WSHenvendelse, Melding> somMelding = new Transformer<WSHenvendelse, Melding>() {
			public Melding transform(WSHenvendelse input) {
				Melding melding = new Melding()
					.withId(input.getBehandlingsId())
					.withFritekst(input.getBeskrivelse())
					.withOpprettet(input.getSistEndretDato())
					.withOverskrift(input.getOverskrift())
					.withTema(input.getTema());
				if (input instanceof no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMelding) {
					no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMelding wsMelding = (no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMelding) input;
					melding.withType(Meldingstype.valueOf(wsMelding.getType().name()));
					melding.withTraadId(wsMelding.getTraadId());					
				} else {
					melding.withType(Meldingstype.DOKUMENTINNSENDING);
					melding.withTraadId("0");
				}
				return melding;
			}
		};
        return on(henvendelseWebservice.hentHenvendelseListe(aktorId)).map(somMelding).collect();
    }

    public SporsmalOgSvar plukkMelding(String aktorId) {
        WSSporsmalOgSvar wsSporsmalOgSvar = webservice.plukkMeldingForBesvaring(aktorId);
        return wsSporsmalOgSvar == null ? null : TIL_SPORSMALOGSVAR.transform(wsSporsmalOgSvar);
    }

    public void besvar(WSSvar svar) {
        webservice.besvarSporsmal(svar);
    }

    private static final Transformer<WSMelding, Melding> TIL_MELDING = new Transformer<WSMelding, Melding>() {
        @Override
        public Melding transform(WSMelding wsMelding) {
            return new Melding()
                    .withId(wsMelding.getId())
                    .withTraadId(wsMelding.getTraadId())
                    .withOpprettet(wsMelding.getOpprettet())
                    .withType(Meldingstype.valueOf(wsMelding.getType().toString()))
                    .withTema(wsMelding.getTema())
                    .withOverskrift(wsMelding.getOverskrift())
                    .withFritekst(wsMelding.getFritekst());
        }
    };

    private static final Transformer<WSSporsmalOgSvar, SporsmalOgSvar> TIL_SPORSMALOGSVAR = new Transformer<WSSporsmalOgSvar, SporsmalOgSvar>() {
        @Override
        public SporsmalOgSvar transform(WSSporsmalOgSvar wsSporsmalOgSvar) {
            Melding sporsmal = TIL_MELDING.transform(wsSporsmalOgSvar.getSporsmal());
            Melding svar = TIL_MELDING.transform(wsSporsmalOgSvar.getSvar());
            return new SporsmalOgSvar().withSporsmal(sporsmal).withSvar(svar);
        }
    };

}
