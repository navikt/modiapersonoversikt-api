package no.nav.kjerneinfo.consumer.fim.person.support;

import kotlin.Pair;
import no.nav.kjerneinfo.consumer.fim.person.exception.AuthorizationWithSikkerhetstiltakException;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.consumer.fim.person.to.RecoverableAuthorizationException;
import no.nav.kjerneinfo.consumer.mdc.MDCUtils;
import no.nav.kjerneinfo.domain.person.GeografiskTilknytning;
import no.nav.kjerneinfo.domain.person.GeografiskTilknytningstyper;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.naudit.Audit;
import no.nav.sbl.dialogarena.naudit.AuditResources;
import no.nav.sbl.dialogarena.rsbac.DecisionEnums;
import no.nav.tjeneste.virksomhet.person.v3.binding.*;
import no.nav.tjeneste.virksomhet.person.v3.feil.PersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kodeverdi;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Optional;

import static java.util.Collections.singletonList;

public class HentPersonService {
    private static final Logger logger = LoggerFactory.getLogger(HentPersonService.class);
    private static Audit.AuditDescriptor<Person> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Personalia,
            (person) -> singletonList(new Pair<>("fnr", person.getFodselsnummer().getNummer()))
    );
    private static final String FNR_REGEX = "\\d{11}";

    private final PersonV3 service;
    private final KjerneinfoMapper mapper;
    private final OrganisasjonEnhetV2Service organisasjonEnhetV2Service;
    private final Tilgangskontroll tilgangskontroll;

    HentPersonService(PersonV3 service, KjerneinfoMapper mapper, OrganisasjonEnhetV2Service organisasjonEnhetV2Service,
                      Tilgangskontroll tilgangskontroll
    ) {
        this.service = service;
        this.mapper = mapper;
        this.organisasjonEnhetV2Service = organisasjonEnhetV2Service;
        this.tilgangskontroll = tilgangskontroll;
    }

    protected HentKjerneinformasjonResponse hentPerson(HentKjerneinformasjonRequest hentKjerneinformasjonRequest) {
        String requestIdent = hentKjerneinformasjonRequest.getIdent();
        MDCUtils.putMDCInfo("hentPerson()", "Personidentifikator:" + requestIdent);
        logger.info("Henter ut kjerneinformasjon om bruker med personidentifikator {}", requestIdent);

        if (!erFnrGodkjent(requestIdent)) {
            logger.warn("{} er et ugyldig fødselsnummer, kan ikke hentes ut.", requestIdent);
            throw new ApplicationException("UgyldigFnr", new HentPersonPersonIkkeFunnet("Ugyldig fnr", new PersonIkkeFunnet().withFeilmelding("Ugyldig fnr")), "hentkjerneinformasjonpersonikkefunnet.feilmelding");
        }

        HentPersonRequest wsRequest = lagHentPersonRequest(requestIdent);

        HentPersonResponse wsResponse;
        try {
            wsResponse = service.hentPerson(wsRequest);
        } catch (HentPersonPersonIkkeFunnet hentPersonPersonIkkeFunnet) {
            logger.info("HentKjerneinformasjonPersonIkkeFunnet ved kall på hentPerson", hentPersonPersonIkkeFunnet.getMessage());
            throw new ApplicationException("HentKjerneinformasjonPersonIkkeFunnet", hentPersonPersonIkkeFunnet, "hentkjerneinformasjonpersonikkefunnet.feilmelding");
        } catch (HentPersonSikkerhetsbegrensning hentPersonSikkerhetsbegrensning) {
            logger.info("HentKjerneinformasjonSikkerhetsbegrensning ved kall på hentPerson", hentPersonSikkerhetsbegrensning.getMessage());
            String faultDescriptionKey = "sikkerhetsbegrensning.diskresjonskode";
            if (hentPersonSikkerhetsbegrensning.getFaultInfo() != null && hentPersonSikkerhetsbegrensning.getFaultInfo().getFeilaarsak() != null) {
                if (FeilAarsaker.FP1_SFA.name().equals(hentPersonSikkerhetsbegrensning.getFaultInfo().getFeilaarsak())) {
                    faultDescriptionKey = "sikkerhetsbegrensning.diskresjonskode6";
                } else if (FeilAarsaker.FP2_FA.name().equals(hentPersonSikkerhetsbegrensning.getFaultInfo().getFeilaarsak())) {
                    faultDescriptionKey = "sikkerhetsbegrensning.diskresjonskode7";
                } else if (FeilAarsaker.FP3_EA.name().equals(hentPersonSikkerhetsbegrensning.getFaultInfo().getFeilaarsak())) {
                    faultDescriptionKey = "sikkerhetsbegrensning.diskresjonEgenAnsatt";
                }
            }
            throw new AuthorizationException(faultDescriptionKey, hentPersonSikkerhetsbegrensning);
        }
        HentKjerneinformasjonResponse response = mapper.map(wsResponse, HentKjerneinformasjonResponse.class);
        auditLogger.log(response.getPerson());

        final GeografiskTilknytning geografiskTilknytning = response.getPerson().getPersonfakta().getGeografiskTilknytning();
        if (skalHenteEnhetFraNORG(geografiskTilknytning)) {
            oppdaterAnsvarligEnhetMedDataFraNORG(response, geografiskTilknytning);
        }

        return filtrerFamilierelasjonerSjekkForSenstiveData(response);
    }

    protected GeografiskTilknytning hentGeografiskTilknytning(String requestIdent) {
        Optional<HentGeografiskTilknytningResponse> response = hentGeografiskTilknytningFraService(requestIdent);
        return response
                .map(this::tilDomeneobjekt)
                .orElse(new GeografiskTilknytning());
    }

    private Optional<HentGeografiskTilknytningResponse> hentGeografiskTilknytningFraService(String requestIdent) {
        HentGeografiskTilknytningRequest request = new HentGeografiskTilknytningRequest().withAktoer(lagAktoer(requestIdent));

        HentGeografiskTilknytningResponse response;
        try {
            response = service.hentGeografiskTilknytning(request);
        } catch (HentGeografiskTilknytningSikkerhetsbegrensing hentGeografiskTilknytningSikkerhetsbegrensing) {
            logger.info("HentGeografiskTilknytningSikkerhetsbegrensing ved kall på hentGeografiskTilknyttning", hentGeografiskTilknytningSikkerhetsbegrensing);
            throw new AuthorizationException("sikkerhetsbegrensning.diskresjonskode", hentGeografiskTilknytningSikkerhetsbegrensing);
        } catch (HentGeografiskTilknytningPersonIkkeFunnet hentGeografiskTilknytningPersonIkkeFunnet) {
            logger.info("HentGeografiskTilknytningPersonIkkeFunnet ved kall på hentGeografiskTilknyttning", hentGeografiskTilknytningPersonIkkeFunnet);
            throw new ApplicationException("HentGeografiskTilknytningPersonIkkeFunnet", hentGeografiskTilknytningPersonIkkeFunnet, "hentkjerneinformasjonpersonikkefunnet.feilmelding");
        }

        return Optional.of(response);
    }

    private GeografiskTilknytning tilDomeneobjekt(HentGeografiskTilknytningResponse response) {
        String geografiskTilknytning = getGeografiskTilknytning(response);
        String diskresjonskode = getDiskresjonskode(response);
        return new GeografiskTilknytning().withValue(geografiskTilknytning).withDiskresjonskode(diskresjonskode);
    }

    private String getDiskresjonskode(HentGeografiskTilknytningResponse response) {
        return Optional.ofNullable(response.getDiskresjonskode())
                .map(Kodeverdi::getValue)
                .orElse(null);
    }

    private String getGeografiskTilknytning(HentGeografiskTilknytningResponse response) {
        return Optional.ofNullable(response.getGeografiskTilknytning())
                .map(no.nav.tjeneste.virksomhet.person.v3.informasjon.GeografiskTilknytning::getGeografiskTilknytning)
                .orElse(null);
    }


    private boolean skalHenteEnhetFraNORG(GeografiskTilknytning geografiskTilknytning) {
        return geografiskTilknytning == null || geografiskTilknytning.getType() != GeografiskTilknytningstyper.LAND;
    }

    private HentPersonRequest lagHentPersonRequest(String requestIdent) {
        HentPersonRequest wsRequest = new HentPersonRequest()
                .withAktoer(lagAktoer(requestIdent))
                .withInformasjonsbehov(
                        Informasjonsbehov.ADRESSE,
                        Informasjonsbehov.BANKKONTO,
                        Informasjonsbehov.FAMILIERELASJONER,
                        Informasjonsbehov.KOMMUNIKASJON,
                        Informasjonsbehov.SPORINGSINFORMASJON);
        return wsRequest;
    }

    private PersonIdent lagAktoer(String ident) {
        return new PersonIdent().withIdent(new NorskIdent().withIdent(ident));
    }

    private void oppdaterAnsvarligEnhetMedDataFraNORG(HentKjerneinformasjonResponse response, GeografiskTilknytning geografiskTilknytning) {
        final String diskresjonskode = getDiskresjonskode(response.getPerson());
        final String geografiskTilknytningValue = geografiskTilknytning == null ? null : geografiskTilknytning.getValue();
        final Optional<AnsattEnhet> ansattEnhetOptional = organisasjonEnhetV2Service.finnNAVKontor(geografiskTilknytningValue, diskresjonskode);

        if (ansattEnhetOptional.isPresent()) {
            final AnsattEnhet ansattEnhet = ansattEnhetOptional.get();

            AnsvarligEnhet ansvarligEnhet = new AnsvarligEnhet.With().organisasjonsenhet(
                    new Organisasjonsenhet.With()
                            .organisasjonselementId(ansattEnhet.enhetId)
                            .organisasjonselementNavn(ansattEnhet.enhetNavn)
                            .geografiskOmrade(geografiskTilknytningValue)
                            .done())
                    .done();
            response.getPerson().getPersonfakta().setAnsvarligEnhet(ansvarligEnhet);
        }
    }

    private String getDiskresjonskode(Person person) {
        return person.getPersonfakta().getDiskresjonskode() == null
                ? ""
                : person.getPersonfakta().getDiskresjonskode().getKodeRef();
    }

    private HentKjerneinformasjonResponse filtrerFamilierelasjonerSjekkForSenstiveData(HentKjerneinformasjonResponse response) {
        if (response == null || response.getPerson() == null || response.getPerson().getPersonfakta() == null
                || response.getPerson().getPersonfakta().getHarFraRolleIList() == null) {
            return response;
        }

        Iterator it = response.getPerson().getPersonfakta().getHarFraRolleIList().iterator();
        while (it.hasNext()) {
            Familierelasjon familierelasjon = (Familierelasjon) it.next();
            if (familierelasjon.getTilPerson() != null && familierelasjon.getTilPerson().getPersonfakta() == null) {
                it.remove();
                continue;
            }
            if (familierelasjon.getTilPerson() != null
                    && familierelasjon.getTilPerson().getPersonfakta() != null
                    && familierelasjon.getTilPerson().getPersonfakta().isHarDiskresjonskode6Eller7()
                    && !saksbehandlerHarTilgangTilDiskresjonskode(getDiskresjonskode(familierelasjon.getTilPerson()))) {
                familierelasjon.getTilPerson().setHideFodselsnummerOgNavn(true);
            }
        }
        return response;
    }

    private boolean saksbehandlerHarTilgangTilDiskresjonskode(String diskresjonskode) {
        return tilgangskontroll.check(Policies.tilgangTilDiskresjonskode.with(diskresjonskode))
                .getDecision()
                .getDecision()
                .equals(DecisionEnums.PERMIT);
    }

    private boolean erFnrGodkjent(String fnr) {
        if (fnr == null) {
            return false;
        }
        return fnr.matches(FNR_REGEX);
    }
}
