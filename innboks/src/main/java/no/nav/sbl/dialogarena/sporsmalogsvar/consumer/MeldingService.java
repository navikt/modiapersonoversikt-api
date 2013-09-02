package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import java.util.List;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMelding;
import org.apache.commons.collections15.Transformer;

import static no.nav.modig.lang.collections.IterUtils.on;

public interface MeldingService {

    List<Melding> hentAlleMeldinger(String aktorId);

    class Default implements MeldingService {

        private final HenvendelsePortType henvendelseWS;

        public Default(HenvendelsePortType henvendelseWS) {
    		this.henvendelseWS = henvendelseWS;
    	}

        @Override
        public List<Melding> hentAlleMeldinger(String aktorId) {
        	Transformer<WSHenvendelse, Melding> somMelding = new Transformer<WSHenvendelse, Melding>() {
    			@Override
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
            return on(henvendelseWS.hentHenvendelseListe(aktorId)).map(somMelding).collect();
        }

    }

}
