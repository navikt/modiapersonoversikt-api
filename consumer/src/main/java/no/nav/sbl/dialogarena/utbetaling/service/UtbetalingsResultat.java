package no.nav.sbl.dialogarena.utbetaling.service;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentUtbetalingerFraPeriode;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.splittUtbetalingerPerMaaned;
import static no.nav.sbl.dialogarena.utbetaling.filter.Filter.filtrer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere;

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

	public List<List<Utbetaling>> hentFiltrertUtbetalingerPerMaaned(FilterParametere filterParametre) {
		return splittUtbetalingerPerMaaned(getSynligeUtbetalinger(filterParametre));
	}

	public List<Utbetaling> hentUtbetalinger(LocalDate startDato, LocalDate sluttDato) {
		return hentUtbetalingerFraPeriode(utbetalinger, startDato, sluttDato);
	}

	public List<Utbetaling> getSynligeUtbetalinger(FilterParametere params) {
		List<Utbetaling> synligeUtbetalinger = new ArrayList<>();
		for (Utbetaling utbetaling : utbetalinger) {
			if (filtrer(utbetaling, params)) {
				synligeUtbetalinger.add(utbetaling);
			}
		}
		return synligeUtbetalinger;
	}

}
