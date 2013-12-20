package no.nav.sbl.dialogarena.utbetaling.service;

import java.io.Serializable;
import java.util.List;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

import org.joda.time.LocalDate;

public class UtbetalingsResultat implements Serializable {

	public final String fnr;
	public final LocalDate startDato, sluttDato;
	public final List<Utbetaling> utbetalinger;

	public UtbetalingsResultat(String fnr, LocalDate startDato, LocalDate sluttDato, List<Utbetaling> utbetalinger) {
		this.fnr = fnr;
		this.startDato = startDato;
		this.sluttDato = sluttDato;
		this.utbetalinger = utbetalinger;
	}

}
