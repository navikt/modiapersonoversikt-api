package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.config;

import no.nav.modig.lang.collections.iter.PreparedIterable;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSak;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerRequest;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMeldingstype;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.meldinger.HentMeldingListe;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.meldinger.HentMeldingListeResponse;
import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.by;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMeldingstype.INNGAENDE;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMeldingstype.UTGAENDE;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.joda.time.DateTime.now;

@Configuration
public class TjenesterMock {

    public static final String TRAAD = "1";
    private static final Logger LOG = LoggerFactory.getLogger(TjenesterMock.class);
    private static final String LANG_TEKST = "Lorem ipsum dolor sit amet, " +
            "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
            "minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure " +
            "dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
            " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis" +
            " eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis" +
            " in iis qui facit eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus" +
            " dynamicus, qui sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit" +
            " litterarum formas humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes" +
            " in futurum.";

    public static final PreparedIterable<WSMelding> MELDINGER = on(asList(
            createWSMelding(INNGAENDE, "Jeg lurer på noe!", now().minusWeeks(2)),
            createWSMelding(UTGAENDE, "Hva da?", now().minusWeeks(1)),
            createWSMelding(INNGAENDE, "Jo nå skal du høre: " + LANG_TEKST, now().minusDays(5)),
            createWSMelding(UTGAENDE, "Ja det var ikke småtterier!", now().minusDays(4)),
            createWSMelding(INNGAENDE, "Nei det kan du si. Dette er litt sensitivt, men, joda, så neida så, og ikke nok med det, " + LANG_TEKST, now().minusDays(2)).withSensitiv(true)));


    private static WSMelding createWSMelding(WSMeldingstype type, String fritekst, DateTime opprettet) {
        return new WSMelding()
                .withBehandlingsId(randomNumeric(5))
                .withMeldingsType(type)
                .withOpprettetDato(opprettet)
                .withTraad(TRAAD)
                .withTekst(fritekst);
    }


    @Bean
    public HenvendelseMeldingerPortType henvendelseMeldingerPortType() {
        return new HenvendelseMeldingerPortType() {
            @Override
            public HentMeldingListeResponse hentMeldingListe(HentMeldingListe hentMeldingListeRequest) {
                LOG.info("Henter alle henvendelser for bruker med fødselsnummer " + hentMeldingListeRequest.getFodselsnummer());
                return new HentMeldingListeResponse().withMelding(MELDINGER.collect());
            }

            @Override
            public void merkMeldingSomLest(String behandlingsId) {
                LOG.info("HenvendelseMeldingerPortType: Melding med id {} er lest.", behandlingsId);
            }

            @Override
            public void ping() {
            }
        };

    }


    @Bean
    public BesvareHenvendelsePortType besvareHenvendelsePortType() {
        class BesvareHenvendelseStub extends ItPings implements BesvareHenvendelsePortType {

            @Override
            public void journalforMeldinger(final List<no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSMelding> meldinger) {
                LOG.info("Journalfører {} meldinger", meldinger.size());
                if (!meldinger.isEmpty()) {

                    MELDINGER.forEach(new Closure<WSMelding>() {
                        @Override
                        public void execute(WSMelding melding) {
                            melding.setJournalfortDato(DateTime.now());
                            melding.setJournalfortTema(meldinger.get(0).getArkivtema());
                            melding.setJournalfortSaksId(meldinger.get(0).getSaksId());
                        }
                    });

                }
            }


            @Override
            public void besvarSporsmal(WSSvar wsSvar) {
                LOG.info("BesvareHenvendelsePortType besvarer spørsmål (svar behandlingId {} \"{}\")", wsSvar.getBehandlingsId(), abbreviate(wsSvar.getFritekst(), 30));
            }

            @Override
            public WSSporsmalOgSvar hentSporsmalOgSvar(String oppgaveId) {
                WSMelding nyesteMelding = MELDINGER.collect(by(OPPRETTET_DATO).descending()).get(0);
                WSSporsmal sporsmal = new WSSporsmal()
                        .withFritekst(nyesteMelding.getTekst())
                        .withOpprettet(nyesteMelding.getOpprettetDato())
                        .withBehandlingsId(nyesteMelding.getBehandlingsId())
                        .withTema("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT")
                        .withTraad(TRAAD);
                return new WSSporsmalOgSvar().withSporsmal(sporsmal).withSvar(new WSSvar().withBehandlingsId(randomNumeric(5)));
            }

            @Override
            public HentSakerResponse hentSaker(HentSakerRequest parameters) {
                return new HentSakerResponse().withSaker(Arrays.asList(
                        new WSSak().withGenerell(true).withOpprettetDato(DateTime.now()).withSakId("123").withStatuskode("Foobar").withTemakode("UFO"),
                        new WSSak().withGenerell(false).withOpprettetDato(DateTime.now().minusDays(1)).withSakId("12").withStatuskode("Something").withTemakode("BIL"),
                        new WSSak().withGenerell(true).withOpprettetDato(DateTime.now().minusDays(3)).withSakId("13").withStatuskode("Ingen Status").withTemakode("BIL"),
                        new WSSak().withGenerell(false).withOpprettetDato(DateTime.now().minusDays(3)).withSakId("1234").withStatuskode("Ingen Status").withTemakode("BAR")));
            }
        }
        return new BesvareHenvendelseStub();
    }

    private static final Transformer<WSMelding, DateTime> OPPRETTET_DATO = new Transformer<WSMelding, DateTime>() {
        @Override
        public DateTime transform(WSMelding wsMelding) {
            return wsMelding.getOpprettetDato();
        }
    };


    abstract static class ItPings {
        public boolean ping() {
            return true;
        }
    }
}
