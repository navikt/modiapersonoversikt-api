package no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.mapping;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.utils.DateUtils;
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.mapping.to.SykepengerResponse;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.*;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.sykepenger.*;
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeRequest;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeResponse;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Mapperklasse som benytter SykepengerFIMMapper for custom mapping.
 */
public class SykepengerMapper {
    private static Logger logger = LoggerFactory.getLogger(SykepengerMapper.class);
    private static SykepengerMapper instance = null;

    private SykepengerMapper() {
    }

    public static SykepengerMapper getInstance() {
        if (instance == null) {
            instance = new SykepengerMapper();
        }

        return instance;
    }

    public FimHentSykepengerListeRequest map(SykepengerRequest source) {
        if (source == null) {
            return null;
        }
        FimHentSykepengerListeRequest request = new FimHentSykepengerListeRequest();
        request.setIdent(source.getIdent());
        FimsykPeriode periode = new FimsykPeriode();
        periode.setFom(map(source.getFrom()));
        periode.setTom(map(source.getTo()));
        request.setSykmelding(periode);
        return request;
    }

    public SykepengerResponse map(FimHentSykepengerListeResponse source) {
        if (source == null) {
            return null;
        }
        SykepengerResponse response = new SykepengerResponse();
        response.setSykmeldingsperioder(forEach(source.getSykmeldingsperiodeListe(), this::map));
        return response;
    }

    private Sykmeldingsperiode map(FimsykSykmeldingsperiode source) {
        if (source == null) {
            return null;
        }
        Sykmeldingsperiode periode = new Sykmeldingsperiode();
        periode.setFodselsnummer(null);
        periode.setSykmeldtFom(map(source.getSykmeldtFom()));
        if (source.getForbrukteDager() != null) {
            periode.setForbrukteDager(source.getForbrukteDager().intValue());
        }
        periode.setFerie1(map(source.getFerie1()));
        periode.setFerie2(map(source.getFerie2()));
        periode.setSanksjon(map(source.getSanksjon()));
        periode.setStansarsak(map(source.getStansaarsak()));
        periode.setUnntakAktivitet(map(source.getUnntakAktivitet()));
        periode.setGjeldendeForsikring(map(source.getGjeldendeForsikring()));
        periode.setSykmeldinger(forEach(source.getSykmeldingListe(), this::map));
        periode.setBruker(map(source.getSykmeldt()));
        periode.setMidlertidigStanset(map(source.getMidlertidigStanset()));
        periode.setHistoriskeUtbetalinger(null);
        periode.setKommendeUtbetalinger(null);
        periode.setUtbetalingerPaVent(null);

        List<HistoriskUtbetaling> historiskeUtbetalinger = new ArrayList<>();
        List<UtbetalingPaVent> utbetalingerPaaVent = new ArrayList<>();
        List<KommendeUtbetaling> kommendeUtbetalinger = new ArrayList<>();
        for (FimsykVedtak utbetaling : source.getVedtakListe()) {
            if (utbetaling instanceof FimsykHistoriskVedtak) {
                kommendeUtbetalinger.add(map((FimsykHistoriskVedtak)utbetaling));
            } else if (utbetaling instanceof FimsykKommendeVedtak) {
                utbetalingerPaaVent.add(createUtbetalingPaVentObjekt(source, (FimsykKommendeVedtak)utbetaling));
            }
        }
        periode.setUtbetalingerPaVent(utbetalingerPaaVent);
        periode.setKommendeUtbetalinger(kommendeUtbetalinger);
        periode.setHistoriskeUtbetalinger(historiskeUtbetalinger);

        periode.setSlutt(map(source.getSlutt()));
        periode.setArbeidsforholdListe(forEach(source.getArbeidsforholdListe(), this::map));
        periode.setErArbeidsgiverperiode(source.isErArbeidsgiverperiode());
        periode.setArbeidskategori(map(source.getArbeidskategori()));

        return periode;
    }

    private Arbeidsforhold map(FimsykArbeidsforhold source) {
        if (source == null) {
            return null;
        }
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        arbeidsforhold.setArbeidsgiverNavn(source.getArbeidsgiverNavn());
        arbeidsforhold.setArbeidsgiverKontonr(source.getArbeidsgiverKontonr());
        arbeidsforhold.setInntektsperiode(map(source.getInntektsperiode()));
        if (source.getInntektForPerioden() != null) {
            arbeidsforhold.setInntektForPerioden(source.getInntektForPerioden().doubleValue());
        }
        arbeidsforhold.setSykepengerFom(map(source.getSykepengerFom()));
        arbeidsforhold.setRefusjonTom(map(source.getRefusjonTom()));
        arbeidsforhold.setRefusjonstype(map(source.getRefusjonstype()));

        return arbeidsforhold;
    }

    private KommendeUtbetaling map(FimsykHistoriskVedtak source) {
        if (source == null) {
            return null;
        }
        KommendeUtbetaling utbetaling = new KommendeUtbetaling();
        utbetaling.setUtbetalingsdato(map(source.getUtbetalt()));
        if (source.getBruttobeloep() != null) {
            utbetaling.setBruttobeloep(source.getBruttobeloep().doubleValue());
        }
        utbetaling.setArbeidsgiverNavn(source.getArbeidsgiverNavn());
        utbetaling.setArbeidsgiverKontonr(source.getArbeidsgiverKontonr());
        utbetaling.setArbeidsgiverOrgnr(source.getArbeidsgiverOrgnr());
        if (source.getDagsats() != null) {
            utbetaling.setDagsats(source.getDagsats().doubleValue());
        }
        utbetaling.setSaksbehandler(source.getSaksbehandler());
        if (source.getPeriodetype() != null) {
            utbetaling.setType(new Kodeverkstype(source.getPeriodetype().getKode(), source.getPeriodetype().getTermnavn()));
        }

        utbetaling.setVedtak(map(source.getVedtak()));
        if (source.getUtbetalingsgrad() != null) {
            utbetaling.setUtbetalingsgrad(source.getUtbetalingsgrad().doubleValue());
        }

        return utbetaling;
    }

    private UtbetalingPaVent map(FimsykKommendeVedtak source) {
        if (source == null) {
            return null;
        }
        UtbetalingPaVent utbetaling = new UtbetalingPaVent();
        utbetaling.setOppgjoerstype(map(source.getOppgjoerstype()));
        utbetaling.setVedtak(map(source.getVedtak()));
        if (source.getUtbetalingsgrad() != null) {
            utbetaling.setUtbetalingsgrad(source.getUtbetalingsgrad().doubleValue());
        }
        return utbetaling;
    }

    private Bruker map(FimsykBruker source) {
        if (source == null) {
            return null;
        }
        Bruker bruker = new Bruker();
        bruker.setIdent(source.getIdent());
        return bruker;
    }

    private Sykmelding map(FimsykSykmelding source) {
        if (source == null) {
            return null;
        }
        Sykmelding sykmelding = new Sykmelding();
        sykmelding.setSykmelder(source.getSykmelder());
        sykmelding.setBehandlet(map(source.getBehandlet()));
        sykmelding.setSykmeldt(map(source.getSykmeldt()));
        sykmelding.setGradAvSykmeldingListe(forEach(source.getGradAvSykmeldingListe(), this::map));
        sykmelding.setGjelderYrkesskade(map(source.getGjelderYrkesskade()));

        List<FimsykGradering> graderinger = source.getGradAvSykmeldingListe();
        XMLGregorianCalendar lastTom = null;
        FimsykGradering lastGradering = null;
        for (FimsykGradering gradering : graderinger) {
            if (lastTom == null || lastTom.compare(gradering.getGradert().getTom()) < 0) {
                lastTom = gradering.getGradert().getTom();
                lastGradering = gradering;
            }
        }

        if (lastGradering != null) {
            sykmelding.setSykmeldingsgrad(lastGradering.getSykmeldingsgrad().doubleValue());
        }

        return sykmelding;
    }

    private Gradering map(FimsykGradering source) {
        if (source == null) {
            return null;
        }
        Gradering gradering = new Gradering();
        gradering.setGradert(map(source.getGradert()));
        if (source.getSykmeldingsgrad() != null) {
            gradering.setSykmeldingsgrad(source.getSykmeldingsgrad().doubleValue());
        }
        return gradering;
    }

    private Yrkesskade map(FimsykYrkesskade source) {
        if (source == null) {
            return null;
        }
        Yrkesskade skade = new Yrkesskade();
        skade.setYrkesskadeart(map(source.getYrkesskadeart()));
        skade.setSkadet(map(source.getSkadet()));
        skade.setVedtatt(map(source.getVedtatt()));
        return skade;
    }

    private Forsikring map(FimsykForsikring source) {
        if (source == null) {
            return null;
        }
        Forsikring forsikring = new Forsikring();
        forsikring.setForsikringsordning(source.getForsikringsordning());
        if (source.getPremiegrunnlag() != null) {
            forsikring.setPremiegrunnlag(source.getPremiegrunnlag().doubleValue());
        }
        forsikring.setErGyldig(source.isErGyldig());
        forsikring.setForsikret(map(source.getForsikret()));
        return forsikring;
    }

    private Periode map(FimsykPeriode source) {
        if (source == null) {
            return null;
        }
        Periode periode = new Periode();
        periode.setFrom(map(source.getFom()));
        periode.setTo(map(source.getTom()));
        return periode;
    }

    private Kodeverkstype map(FimsykKodeverdi source) {
        if (source == null) {
            return null;
        }
        Kodeverkstype type = new Kodeverkstype();
        type.setKode(source.getKode());
        type.setTermnavn(source.getTermnavn());
        return type;
    }

    private UtbetalingPaVent createUtbetalingPaVentObjekt(FimsykSykmeldingsperiode from, FimsykKommendeVedtak utbetaling) {
        if (from == null || utbetaling == null) {
            return null;
        }
        UtbetalingPaVent utbetalingPaaVent = map(utbetaling);
        FimsykArbeidskategori arbeidskategori = from.getArbeidskategori();
        FimsykStansaarsak stansaarsak = from.getStansaarsak();
        FimsykPeriode ferie1 = from.getFerie1();
        FimsykPeriode ferie2 = from.getFerie2();
        FimsykPeriode sanksjon = from.getSanksjon();
        Optional<FimsykPeriode> sykmeldt = getSykmeldt(from);

        if (arbeidskategori != null) {
            utbetalingPaaVent.setArbeidskategori(new Kodeverkstype(arbeidskategori.getKode(), arbeidskategori.getTermnavn()));
        }
        if (stansaarsak != null) {
            utbetalingPaaVent.setStansaarsak(new Kodeverkstype(stansaarsak.getKode(), stansaarsak.getTermnavn()));
        }
        if (ferie1 != null) {
            utbetalingPaaVent.setFerie1(map(ferie1));
        }
        if (ferie2 != null) {
            utbetalingPaaVent.setFerie2(map(ferie2));
        }
        if (sanksjon != null) {
            utbetalingPaaVent.setSanksjon(map(sanksjon));
        }
        if (sykmeldt.isPresent()) {
            utbetalingPaaVent.setSykmeldt(map(sykmeldt.get()));
        }

        return utbetalingPaaVent;
    }

    private static Optional<FimsykPeriode> getSykmeldt(FimsykSykmeldingsperiode from) {
        if (from == null || from.getSykmeldingListe().isEmpty()) {
            return empty();
        }
        return ofNullable(from.getSykmeldingListe().get(0).getSykmeldt());
    }

    private XMLGregorianCalendar map(LocalDate source) {
        if (source == null) {
            return null;
        }
        return DateUtils.convertDateToXmlGregorianCalendar(source.toDate());
    }

    public LocalDate map(XMLGregorianCalendar source) {
        if (source == null) {
            return null;
        }
        return new LocalDate(source.getYear(), source.getMonth(), source.getDay());
    }

    private <S, T> List<T> forEach(List<S> list, Function<S, T> fn) {
        if (list == null) {
            return null;
        }
        return list.stream().map(fn).collect(Collectors.toList());
    }
}
