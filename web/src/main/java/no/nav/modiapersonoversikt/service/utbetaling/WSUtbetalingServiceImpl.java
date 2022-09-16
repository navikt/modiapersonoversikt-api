package no.nav.modiapersonoversikt.service.utbetaling;

import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonIkkeTilgang;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import org.joda.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.service.utbetaling.UtbetalingUtils.leggTilEkstraDagerPaaStartdato;
import static no.nav.modiapersonoversikt.service.utbetaling.UtbetalingUtils.finnUtbetalingerISokeperioden;
import static no.nav.modiapersonoversikt.utils.ConvertionUtils.toJavaTime;
import static no.nav.modiapersonoversikt.utils.ConvertionUtils.toJodaTime;

public class WSUtbetalingServiceImpl implements WSUtbetalingService {

    @Autowired
    private UtbetalingV1 utbetalingV1;

    @Override
    public List<WSUtbetaling> hentWSUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato) {

        return getWSUtbetalinger(fnr, startDato, sluttDato).stream()
                .filter(finnUtbetalingerISokeperioden(toJavaTime(startDato), toJavaTime(sluttDato)))
                .collect(toList());
    }

    @Override
    public void ping() {
        utbetalingV1.ping();
    }

    List<WSUtbetaling> getWSUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato) {
        try {
            WSHentUtbetalingsinformasjonRequest request = createRequest(
                    fnr,
                    toJodaTime(leggTilEkstraDagerPaaStartdato(toJavaTime(startDato))),
                    sluttDato
            );
            return utbetalingV1.hentUtbetalingsinformasjon(request).getUtbetalingListe();
        } catch (HentUtbetalingsinformasjonPeriodeIkkeGyldig ex) {
            throw new RuntimeException("Utbetalingsperioden er ikke gyldig. ", ex);
        } catch (HentUtbetalingsinformasjonPersonIkkeFunnet ex) {
            throw new RuntimeException("Person ikke funnet. ", ex);
        } catch (HentUtbetalingsinformasjonIkkeTilgang ex) {
            throw new RuntimeException("Ikke tilgang. ", ex);
        } catch (Exception e) {
            throw new RuntimeException("Henting av utbetalinger for bruker med fnr " + fnr + " mellom " + startDato + " og " + sluttDato + " feilet.", e);
        }
    }

    WSHentUtbetalingsinformasjonRequest createRequest(String fnr, LocalDate startDato, LocalDate sluttDato) {
        return new WSHentUtbetalingsinformasjonRequest()
            .withId(new WSIdent()
                    .withIdent(fnr)
                    .withIdentType(new WSIdenttyper().withValue("Personnr"))
                    .withRolle(new WSIdentroller().withValue("Rettighetshaver")))
            .withPeriode(createPeriode(startDato, sluttDato));
    }


    WSForespurtPeriode createPeriode(LocalDate startDato, LocalDate sluttDato) {
        return new WSForespurtPeriode()
            .withFom(startDato.toDateTimeAtStartOfDay())
            .withTom(sluttDato.toDateTimeAtStartOfDay());
    }

}
