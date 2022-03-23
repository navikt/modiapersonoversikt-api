package no.nav.modiapersonoversikt.consumer.infotrygd.consumer.utbetalinger;

import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;
import no.nav.modiapersonoversikt.infrastructure.core.exception.SystemException;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.utbetalinger.Hovedytelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonIkkeTilgang;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import org.joda.time.LocalDate;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.consumer.infotrygd.consumer.utbetalinger.Transformers.TO_HOVEDYTELSE;
import static no.nav.modiapersonoversikt.consumer.infotrygd.consumer.utbetalinger.UtbetalingUtils.fjernHistoriskUtbetalingerMedFeilUtbetalingsType;
import static no.nav.modiapersonoversikt.consumer.infotrygd.consumer.utbetalinger.UtbetalingUtils.utbetalingInnenforSokeperioden;
import static org.slf4j.LoggerFactory.getLogger;

public class UtbetalingerServiceImpl implements UtbetalingerService {
    private static final Logger logger = getLogger(UtbetalingerServiceImpl.class);

    @Autowired
    private UtbetalingV1 utbetalingV1;

    @Override
    public List<Hovedytelse> hentUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato, String utbetalingstype) {
        return getWSUtbetalinger(fnr, startDato, sluttDato).stream()
                .filter(utbetaling -> utbetalingInnenforSokeperioden(utbetaling, startDato, sluttDato))
                .map(TO_HOVEDYTELSE)
                .filter(UtbetalingUtils::harGyldigUtbetaling)
                .map(hovedytelse -> fjernHistoriskUtbetalingerMedFeilUtbetalingsType(hovedytelse, utbetalingstype))
                .collect(toList());
    }

    @Override
    public void ping() {
        utbetalingV1.ping();
    }

    protected List<WSUtbetaling> getWSUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato) {
        logger.info("---- Spør etter utebetalinger. Fnr: {}. ----", fnr);
        try {
            return utbetalingV1.hentUtbetalingsinformasjon(createRequest(fnr, startDato, sluttDato)).getUtbetalingListe();
        } catch (HentUtbetalingsinformasjonPeriodeIkkeGyldig ex) {
            throw new ApplicationException("Utbetalingsperioden er ikke gyldig. ", ex);
        } catch (HentUtbetalingsinformasjonPersonIkkeFunnet ex) {
            throw new ApplicationException("Person ikke funnet. ", ex);
        } catch (HentUtbetalingsinformasjonIkkeTilgang ex) {
            throw new ApplicationException("Ikke tilgang. ", ex);
        } catch (Exception e) {
            logger.error("Henting av utbetalinger for bruker feilet.", e);
            throw new SystemException("Henting av utbetalinger for bruker med fnr " + fnr + " mellom " + startDato + " og " + sluttDato + " feilet.", e);
        }
    }

    protected WSHentUtbetalingsinformasjonRequest createRequest(String fnr, LocalDate startDato, LocalDate sluttDato) {
        return new WSHentUtbetalingsinformasjonRequest()
                .withId(new WSIdent()
                        .withIdent(fnr)
                        .withIdentType(new WSIdenttyper().withValue("Personnr"))
                        .withRolle(new WSIdentroller().withValue("Rettighetshaver")))
                .withPeriode(createPeriode(startDato, sluttDato))
                .withYtelsestypeListe(new List<WSYtelsestyper>() {
                    @Override
                    public int size() {
                        return 0;
                    }

                    @Override
                    public boolean isEmpty() {
                        return false;
                    }

                    @Override
                    public boolean contains(Object o) {
                        return false;
                    }

                    @Override
                    public Iterator<WSYtelsestyper> iterator() {
                        return null;
                    }

                    @Override
                    public Object[] toArray() {
                        return new Object[0];
                    }

                    @Override
                    public <T> T[] toArray(T[] a) {
                        return null;
                    }

                    @Override
                    public boolean add(WSYtelsestyper wsYtelsestyper) {
                        return false;
                    }

                    @Override
                    public boolean remove(Object o) {
                        return false;
                    }

                    @Override
                    public boolean containsAll(Collection<?> c) {
                        return false;
                    }

                    @Override
                    public boolean addAll(Collection<? extends WSYtelsestyper> c) {
                        return false;
                    }

                    @Override
                    public boolean addAll(int index, Collection<? extends WSYtelsestyper> c) {
                        return false;
                    }

                    @Override
                    public boolean removeAll(Collection<?> c) {
                        return false;
                    }

                    @Override
                    public boolean retainAll(Collection<?> c) {
                        return false;
                    }

                    @Override
                    public void clear() {

                    }

                    @Override
                    public WSYtelsestyper get(int index) {
                        return null;
                    }

                    @Override
                    public WSYtelsestyper set(int index, WSYtelsestyper element) {
                        return null;
                    }

                    @Override
                    public void add(int index, WSYtelsestyper element) {

                    }

                    @Override
                    public WSYtelsestyper remove(int index) {
                        return null;
                    }

                    @Override
                    public int indexOf(Object o) {
                        return 0;
                    }

                    @Override
                    public int lastIndexOf(Object o) {
                        return 0;
                    }

                    @Override
                    public ListIterator<WSYtelsestyper> listIterator() {
                        return null;
                    }

                    @Override
                    public ListIterator<WSYtelsestyper> listIterator(int index) {
                        return null;
                    }

                    @Override
                    public List<WSYtelsestyper> subList(int fromIndex, int toIndex) {
                        return null;
                    }
                });
    }


    protected WSForespurtPeriode createPeriode(LocalDate startDato, LocalDate sluttDato) {
        return new WSForespurtPeriode()
                .withFom(startDato.toDateTimeAtStartOfDay())
                .withTom(sluttDato.toDateTimeAtStartOfDay());
    }

}
