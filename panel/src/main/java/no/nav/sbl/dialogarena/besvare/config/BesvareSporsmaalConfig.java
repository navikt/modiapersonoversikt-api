package no.nav.sbl.dialogarena.besvare.config;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.HenvendelseSporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.BesvarSporsmalRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.BesvarSporsmalResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.HentAlleSporsmalOgSvarRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.HentAlleSporsmalOgSvarResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.HentSporsmalOgSvarRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.HentSporsmalOgSvarResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.OpprettSporsmalRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.OpprettSporsmalResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.SporsmalOgSvar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BesvareSporsmaalConfig {

    @Bean
    public HenvendelseSporsmalOgSvarPortType sporsmalOgSvarPortType() {
        return new HenvendelseSporsmalOgSvarPortType() {

            @Override
            public OpprettSporsmalResponse opprettSporsmal(OpprettSporsmalRequest parameters) {
                return null;
            }


            @Override
            public HentSporsmalOgSvarResponse hentSporsmalOgSvar(HentSporsmalOgSvarRequest parameters) {
                return null;
            }


            @Override
            public HentAlleSporsmalOgSvarResponse hentAlleSporsmalOgSvar(HentAlleSporsmalOgSvarRequest parameters) {
                return new HentAlleSporsmalOgSvarResponse().withSporsmalOgSvar(
                        new SporsmalOgSvar().withSporsmal("Kor e pængan min'?"),
                        new SporsmalOgSvar().withSporsmal("Kor eeee dæm pængan min'? E dæm i sjarken?"));
            }


            @Override
            public BesvarSporsmalResponse besvarSporsmal(BesvarSporsmalRequest parameters) {
                return null;
            }
        };
    }

}
