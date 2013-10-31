package no.nav.sbl.dialogarena.sporsmalogsvar.innboks.config;

import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSak;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerRequest;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.meldinger.HentSakerResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMeldingstype;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.meldinger.HentMeldingListe;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.meldinger.HentMeldingListeResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMeldingstype.INNGAENDE;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMeldingstype.UTGAENDE;
import static org.apache.commons.lang3.StringUtils.abbreviate;


public class TestContext {

    private static final Logger LOG = LoggerFactory.getLogger(TestContext.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public BesvareHenvendelsePortType besvareHenvendelsePortType() {
        return new BesvareHenvendelsePortType() {

            @Override
            public boolean ping() {
                return true;
            }

            @Override
            public void journalforMeldinger(List<no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSMelding> meldinger) {
                LOG.info("Journalfører {} meldinger", meldinger.size());
            }

            @Override
            public WSSporsmalOgSvar hentSporsmalOgSvar(String oppgaveId) {
                return new WSSporsmalOgSvar();
            }

            @Override
            public HentSakerResponse hentSaker(HentSakerRequest parameters) {
                return new HentSakerResponse().withSaker(Arrays.asList(
                        new WSSak().withGenerell(true).withOpprettetDato(DateTime.now()).withSakId("123").withStatuskode("Foobar").withTemakode("UFO"),
                        new WSSak().withGenerell(false).withOpprettetDato(DateTime.now().minusDays(1)).withSakId("12").withStatuskode("Something").withTemakode("BIL"),
                        new WSSak().withGenerell(true).withOpprettetDato(DateTime.now().minusDays(3)).withSakId("13").withStatuskode("Ingen Status").withTemakode("BIL"),
                        new WSSak().withGenerell(false).withOpprettetDato(DateTime.now().minusDays(3)).withSakId("1234").withStatuskode("Ingen Status").withTemakode("BAR")));
            }

            @Override
            public void besvarSporsmal(WSSvar svar) {
                LOG.info("BesvareHenvendelsePortType besvarer spørsmål (svar behandlingId {} \"{}\")", svar.getBehandlingsId(), abbreviate(svar.getFritekst(), 30));
            }
        };
    }

    @Bean
    public HenvendelseMeldingerPortType henvendelseMeldingerPortType() {
        final Integer traadId = 1;

        return new HenvendelseMeldingerPortType() {
            final String langTekst = "Lorem ipsum dolor sit amet, " +
                    "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
                    "minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure " +
                    "dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                    " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis" +
                    " eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis" +
                    " in iis qui facit eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus" +
                    " dynamicus, qui sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit" +
                    " litterarum formas humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes" +
                    " in futurum.";
            List<WSMelding> meldinger = asList(
                    createWSMelding(INNGAENDE, "" + traadId, "ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", DateTime.now().minusWeeks(2)),
                    createWSMelding(INNGAENDE, "" + (traadId + 1), "INTERNASJONALT", DateTime.now().minusWeeks(1)),
                    createWSMelding(UTGAENDE, "" + (traadId + 1), "INTERNASJONALT", DateTime.now().minusDays(5), DateTime.now().minusDays(4)),
                    createWSMelding(INNGAENDE, "" + (traadId + 2), "HJELPEMIDLER", DateTime.now().minusDays(3)),
                    createWSMelding(UTGAENDE, "" + (traadId + 2), "HJELPEMIDLER", DateTime.now().minusHours(5), null),
                    createWSMelding(INNGAENDE, "" + (traadId + 3), "HJELPEMIDLER", DateTime.now().minusHours(10)));
            @Override
            public HentMeldingListeResponse hentMeldingListe(HentMeldingListe hentMeldingListeRequest) {
                return new HentMeldingListeResponse().withMelding(meldinger);
            }

            @Override
            public void merkMeldingSomLest(String behandlingsId) {
            }

            @Override
            public void ping() {
            }

            WSMelding createWSMelding(WSMeldingstype type, String traad, String tema, DateTime opprettet) {
                return createWSMelding(type, traad, tema, opprettet, opprettet);
            }

            WSMelding createWSMelding(WSMeldingstype type, String traad, String tema, DateTime opprettet, DateTime lestdato) {
                Random random = new Random();

                return new WSMelding()
                        .withBehandlingsId("" + random.nextInt())
                        .withMeldingsType(type)
                        .withOpprettetDato(opprettet)
                        .withTraad(traad)
                        .withTemastruktur(tema)
                        .withLestDato(lestdato)
                        .withTekst(langTekst);
            }
        };
    }

}

