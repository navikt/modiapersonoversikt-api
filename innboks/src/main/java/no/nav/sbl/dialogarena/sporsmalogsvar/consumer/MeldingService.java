package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;

import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.TransformerUtils.asString;
import static no.nav.modig.lang.option.Optional.optional;

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
                public Melding transform(WSHenvendelse wsMelding) {
                	String henvendelseType = wsMelding.getHenvendelseType();
                	if (!"SPORSMAL".equals(henvendelseType) && !"SVAR".equals(henvendelseType)) {
                		return null;
                	}
                    Melding melding = new Melding()
                            .withId(wsMelding.getBehandlingsId())
                            .withType(Meldingstype.valueOf(henvendelseType))
                            .withTraadId(wsMelding.getTraad());
                    melding.opprettet = wsMelding.getOpprettetDato();
                    melding.tema = wsMelding.getTema();
                    for (String behandlinsresultat : optional(wsMelding.getBehandlingsresultat()).map(asString())) {
                        String[] parts = behandlinsresultat.split("#");
                        melding.overskrift = parts[0];
                        melding.fritekst = parts[1];
                    }
                    return melding;
                }
            };
            return on(henvendelseWS.hentHenvendelseListe(aktorId)).map(somMelding).collect();
        }

    }

}
