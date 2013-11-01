package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerRequest;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerResponse;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import java.util.List;
import java.util.Random;

@Configuration
public class BesvareHenvendelsePortTypeMock {

    public static final Integer TRAAD_ID = 1;

    @Bean
    public BesvareHenvendelsePortType besvareHenvendelsePortType() {
        return new BesvareHenvendelsePortType() {
            @Override
            public boolean ping() {
                return true;
            }

            @Override
            public void journalforMeldinger(@WebParam(name = "meldinger", targetNamespace = "") List<WSMelding> meldinger) {
            }

            @Override
            public void besvarSporsmal(WSSvar wsSvar) {
            }

            @Override
            public WSSporsmalOgSvar hentSporsmalOgSvar(String oppgaveId) {
                Random random = new Random();
                return new WSSporsmalOgSvar()
                        .withSporsmal(new WSSporsmal()
                                .withBehandlingsId("" + random.nextInt())
                                .withTraad("" + TRAAD_ID)
                                .withOpprettet(DateTime.now())
                                .withTema("FAMILIE_OG_BARN")
                                .withFritekst("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore " +
                                        "magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut " +
                                        "aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel " +
                                        "illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril " +
                                        "delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet" +
                                        " doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit " +
                                        "eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus"))
                        .withSvar(new WSSvar().withBehandlingsId("" + random.nextInt()));
            }

            @Override
            public HentSakerResponse hentSaker(HentSakerRequest parameters) {
                return new HentSakerResponse();
            }
        };
    }

    public Pingable besvareHenvendelsePing(){
        return new MockPingable("BesvareHenvendelsePortType");
    }

}
