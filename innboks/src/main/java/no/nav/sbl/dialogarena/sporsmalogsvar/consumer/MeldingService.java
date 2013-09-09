package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> behandlingsresultat;
                    try {
                        behandlingsresultat = mapper.readValue(wsMelding.getBehandlingsresultat(), Map.class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    melding.overskrift = behandlingsresultat.get("overskrift");
                    melding.fritekst = behandlingsresultat.get("fritekst");
                    return melding;
                }
            };
            return on(henvendelseWS.hentHenvendelseListe(aktorId)).map(somMelding).collect();
        }

    }

}
