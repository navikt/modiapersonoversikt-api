package no.nav.modiapersonoversikt.consumer.infotrygd.domain.utbetalinger;

import no.nav.modiapersonoversikt.consumer.infotrygd.domain.HistoriskUtbetaling;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Hovedytelse {
    String type;
    List<HistoriskUtbetaling> historiskUtbetalinger;

    public List<HistoriskUtbetaling> getHistoriskUtbetalinger() {
        return historiskUtbetalinger;
    }

    public void setHistoriskUtbetalinger(List<HistoriskUtbetaling> historiskUtbetalinger) {
        this.historiskUtbetalinger = historiskUtbetalinger;
    }

    public Hovedytelse() {
    }

    @SuppressWarnings("IncompleteCopyConstructor")
    public Hovedytelse(Hovedytelse other) {
        this.type = other.type;
        this.historiskUtbetalinger = other.historiskUtbetalinger == null ? null :
                other.historiskUtbetalinger.stream()
                        .map(HistoriskUtbetaling::new)
                        .collect(toList());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Hovedytelse withHistoriskUtbetalinger(List<HistoriskUtbetaling> historiskUtbetalinger) {
        this.historiskUtbetalinger = historiskUtbetalinger;
        return this;
    }
}
