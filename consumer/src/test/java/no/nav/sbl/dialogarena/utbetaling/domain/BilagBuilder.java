package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPeriode;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;

public class BilagBuilder {

    private String melding = "Bilagsmelding ... Skatt 14%";
    private List<PosteringsDetalj> posteringsDetaljer = new ArrayList<>();
    private DateTime startDato = new DateTime().minusDays(32);
    private DateTime sluttDato = new DateTime().minusDays(2);

    public BilagBuilder() {
        posteringsDetaljer.addAll(asList(
                new PosteringsDetaljBuilder().createPosteringsDetalj(),
                new PosteringsDetaljBuilder().createPosteringsDetalj(),
                new PosteringsDetaljBuilder().createPosteringsDetalj()
        ));
    }

    public BilagBuilder setStartDato(DateTime startDato) {
        this.startDato = startDato;
        return this;
    }

    public BilagBuilder setSluttDato(DateTime sluttDato) {
        this.sluttDato = sluttDato;
        return this;
    }

    public BilagBuilder setMelding(String melding) {
        this.melding = melding;
        return this;
    }

    public BilagBuilder setPosteringsDetaljer(List<PosteringsDetalj> posteringsDetaljer) {
        this.posteringsDetaljer = posteringsDetaljer;
        return this;
    }

    public Bilag createBilag() {
        return new Bilag(new WSBilag()
                .withMeldingListe(new WSMelding().withMeldingtekst(melding))
                .withPosteringsdetaljerListe(on(posteringsDetaljer).map(tilWSPosteringsdetaljer).collect())
                .withBilagPeriode(new WSPeriode().withPeriodeFomDato(startDato).withPeriodeTomDato(sluttDato)));
    }

    private Transformer<PosteringsDetalj, WSPosteringsdetaljer> tilWSPosteringsdetaljer = new Transformer<PosteringsDetalj, WSPosteringsdetaljer>() {
        @Override
        public WSPosteringsdetaljer transform(PosteringsDetalj posteringsDetalj) {
            return new WSPosteringsdetaljer()
                    .withKontoBeskrHoved(posteringsDetalj.getHovedBeskrivelse())
                    .withKontoBeskrUnder(posteringsDetalj.getUnderBeskrivelse())
                    .withBelop(posteringsDetalj.getBelop());
        }
    };

}