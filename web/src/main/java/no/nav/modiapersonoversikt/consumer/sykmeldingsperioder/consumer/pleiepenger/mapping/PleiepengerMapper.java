package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.pleiepenger.mapping;

import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeRequest;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeResponse;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.pleiepenger.Arbeidsforhold;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.pleiepenger.Pleiepengeperiode;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.pleiepenger.Pleiepengerrettighet;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.pleiepenger.Vedtak;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetRequest;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetResponse;

import java.time.LocalDate;
import java.util.List;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class PleiepengerMapper extends Mapper {

    private static final String ORGNUMMER_MANGLER = "000000000";
    private static final String KONTONUMMER_MANGLER = "0";
    private static final String HAR_REFUSJON = "J";
    private VedtaksMapper vedtakMapper = new VedtaksMapper();

    public PleiepengerMapper() {
        registrerRequestMapper();
        registrerResponseMapper();
        registrerPleiepengerettighetMapper();
        registrerPleiepengeperiodeMapper();
        registrerVedtakMapper();
        registrerArbeidsforholdMapper();
    }

    private void registrerRequestMapper() {
        registerMapper(PleiepengerListeRequest.class, WSHentPleiepengerettighetRequest.class, (request) ->
                new WSHentPleiepengerettighetRequest()
                        .withOmsorgsperson(new WSPerson().withIdent(request.ident)));
    }

    private void registrerResponseMapper() {
        registerMapper(WSHentPleiepengerettighetResponse.class, PleiepengerListeResponse.class, (response) -> {
            List<Pleiepengerrettighet> pleiepengerrettighetListe =
                    map(response.getPleiepengerettighetListe());
            return new PleiepengerListeResponse(pleiepengerrettighetListe);
        });
    }

    private void registrerPleiepengerettighetMapper() {
        registerMapper(WSPleiepengerettighet.class, Pleiepengerrettighet.class, (rettighet) ->
                new Pleiepengerrettighet()
                        .withBarnet(rettighet.getBarnet().getIdent())
                        .withOmsorgsperson(rettighet.getOmsorgsperson().getIdent())
                        .withAndreOmsorgsperson(ofNullable(rettighet.getAndreOmsorgsperson())
                                .map(WSPerson::getIdent)
                                .orElse(null))
                        .withPleiepengedager(rettighet.getPleiepengedager())
                        .withForbrukteDagerTOMIDag(rettighet.getForbrukteDagerTOMIDag())
                        .withRestDagerFOMIMorgen(ofNullable(rettighet.getRestDagerFOMIMorgen()).orElse(0))
                        .withRestDagerAnvist(ofNullable(rettighet.getRestDagerAnvist()).orElse(0))
                        .withPerioder(map(rettighet.getPleiepengeperiodeListe())));
    }

    private void registrerPleiepengeperiodeMapper() {
        registerMapper(WSPleiepengeperiode.class, Pleiepengeperiode.class, (periode) ->
                new Pleiepengeperiode()
                        .withFraOgMed(map(periode.getPleiepengerFom()))
                        .withVedtakListe(map(periode.getVedtakListe()))
                        .withArbeidsforholdListe(map(periode.getArbeidsforholdListe()).stream()
                                .map(arbeidsforhold -> ((Arbeidsforhold) arbeidsforhold)
                                        .withArbeidskategori(periode.getArbeidskategori().getTermnavn()))
                                .collect(toList()))
                        .withAntallPleiepengedager(periode.getAntallPleiepengedager()));
    }

    private void registrerArbeidsforholdMapper() {
        registerMapper(WSArbeidsforhold.class, Arbeidsforhold.class, (arbeidsforhold) ->
            new Arbeidsforhold()
                    .withArbeidsgiverOrgnr(of(arbeidsforhold.getArbeidsgiverOrgnr())
                            .filter(orgnummer -> !orgnummer.equals(ORGNUMMER_MANGLER))
                            .orElse(null))
                    .withArbeidsgiverKontonr(ofNullable(arbeidsforhold.getArbeidsgiverKontonr())
                            .map(String::trim)
                            .filter(kontonummer -> !kontonummer.equals(KONTONUMMER_MANGLER))
                            .orElse(null))
                    .withInntektForPerioden(arbeidsforhold.getInntektForPerioden())
                    .withInntektsperiode(of(arbeidsforhold)
                            .map(WSArbeidsforhold::getInntektsperiode)
                            .map(WSKodeverdi::getTermnavn)
                            .orElse(null))
                    .withRefusjonstype(ofNullable(arbeidsforhold.getRefusjonstype())
                            .filter(wsRefusjonstype -> wsRefusjonstype.getKode().equals(HAR_REFUSJON))
                            .map(WSKodeverdi::getTermnavn)
                            .orElse("Ikke refusjon"))
                    .withRefusjonTom((LocalDate) ofNullable(arbeidsforhold.getRefusjonTom())
                            .map(this::map)
                            .orElse(null)));
    }

    private void registrerVedtakMapper() {
        registerMapper(WSVedtak.class, Vedtak.class, (vedtak) ->
                vedtakMapper.map(vedtak));
    }

}
