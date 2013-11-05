package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSak;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerRequest;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerResponse;
import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.containedIn;
import static no.nav.modig.lang.collections.PredicateUtils.where;

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
            public void journalforMeldinger(final List<WSMelding> meldinger) {
                Transformer<WSMelding, String> behandlingsIdBesvare = new Transformer<WSMelding, String>() {
                    @Override
                    public String transform(WSMelding wsMelding) {
                        return wsMelding.getBehandlingsId();
                    }
                };
                Transformer<no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding, String> behandlingsIdMeldinger = new Transformer<no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding, String>() {
                    @Override
                    public String transform(no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding wsMelding) {
                        return wsMelding.getBehandlingsId();
                    }
                };
                if (!meldinger.isEmpty()) {
                    on(HenvendelseMeldingerPortTypeMock.MELDINGER).filter(where(behandlingsIdMeldinger, containedIn(on(meldinger).map(behandlingsIdBesvare).collect()))).forEach(new Closure<no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding>() {
                        @Override
                        public void execute(no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding melding) {
                            melding.setJournalfortDato(DateTime.now());
                            melding.setJournalfortTema(meldinger.get(0).getArkivtema());
                            melding.setJournalfortSaksId(meldinger.get(0).getSaksId());
                        }
                    });

                }
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
                return new HentSakerResponse().withSaker(Arrays.asList(
                        new WSSak().withGenerell(true).withOpprettetDato(DateTime.now()).withSakId("123").withStatuskode("Foobar").withTemakode("UFO"),
                        new WSSak().withGenerell(false).withOpprettetDato(DateTime.now().minusDays(1)).withSakId("12").withStatuskode("Something").withTemakode("BIL"),
                        new WSSak().withGenerell(true).withOpprettetDato(DateTime.now().minusDays(3)).withSakId("13").withStatuskode("Ingen Status").withTemakode("BIL"),
                        new WSSak().withGenerell(false).withOpprettetDato(DateTime.now().minusDays(3)).withSakId("1234").withStatuskode("Ingen Status").withTemakode("BAR")));
            }
        };
    }

    public Pingable besvareHenvendelsePing(){
        return new MockPingable("BesvareHenvendelsePortType");
    }

}
