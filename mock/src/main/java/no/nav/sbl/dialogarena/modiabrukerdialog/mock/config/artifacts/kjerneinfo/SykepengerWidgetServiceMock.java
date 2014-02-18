package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerResponse;
import no.nav.sykmeldingsperioder.domain.Bruker;
import no.nav.sykmeldingsperioder.domain.foreldrepenger.Foreldrepengerettighet;
import no.nav.sykmeldingsperioder.domain.sykepenger.Sykmeldingsperiode;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SykepengerWidgetServiceMock {

    private static Bruker bruker = new Bruker();

    public static SykepengerWidgetService getSykepengerWidgetServiceMock() {
        return mock(SykepengerWidgetService.class);
    }

    public static ForeldrepengerServiceBi getForeldrepengerServiceBiMock() {
        ForeldrepengerServiceBi mock = mock(ForeldrepengerServiceBi.class);
        when(mock.hentForeldrepengerListe(any(ForeldrepengerListeRequest.class))).thenReturn(getForeldrepengerListeResponse());
        return mock;
    }

    public static SykepengerServiceBi getSykepengerServiceBiMock() {
        SykepengerServiceBi mock = mock(SykepengerServiceBi.class);
        when(mock.hentSykmeldingsperioder(any(SykepengerRequest.class))).thenReturn(getSykepengerResponse());
        return mock;
    }

    private static ForeldrepengerListeResponse getForeldrepengerListeResponse() {
        ForeldrepengerListeResponse foreldrepengerListeResponse = new ForeldrepengerListeResponse();
        Foreldrepengerettighet foreldrepengerettighet = new Foreldrepengerettighet();
        foreldrepengerettighet.setBruker(bruker);
        foreldrepengerettighet.setAndreForeldersFnr("01014513371");
        foreldrepengerettighet.setAntallBarn(3);
        foreldrepengerettighet.setBarnetsFoedselsdato(new LocalDate().minusDays(1000));
        foreldrepengerettighet.setDekningsgrad(13.37);
        foreldrepengerettighet.setFedrekvoteTom(new LocalDate().plusDays(1000));
        foreldrepengerettighet.setGraderingsdager(3);
        foreldrepengerettighet.setMoedrekvoteTom(new LocalDate().plusDays(1000));
        foreldrepengerettighet.setMaksdato(new LocalDate().plusDays(1000));
        foreldrepengerListeResponse.setForeldrepengerettighet(foreldrepengerettighet);
        return foreldrepengerListeResponse;
    }

    private static SykepengerResponse getSykepengerResponse() {
        SykepengerResponse sykepengerResponse = new SykepengerResponse();
        sykepengerResponse.setBruker(bruker);
        List<Sykmeldingsperiode> sykmeldingsperioder = new ArrayList<>();
        Sykmeldingsperiode sykmeldingsperiode1 = new Sykmeldingsperiode();
        Sykmeldingsperiode sykmeldingsperiode2 = new Sykmeldingsperiode();
        sykmeldingsperioder.addAll(asList(sykmeldingsperiode1, sykmeldingsperiode2));
        sykepengerResponse.setSykmeldingsperioder(sykmeldingsperioder);
        return sykepengerResponse;
    }
}
