package no.nav.kjerneinfo.consumer.fim.person.support;

import no.nav.kjerneinfo.common.log.SporingUtils;
import no.nav.kjerneinfo.consumer.fim.person.exception.AuthorizationWithSikkerhetstiltakException;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.consumer.fim.person.to.RecoverableAuthorizationException;
import no.nav.kjerneinfo.consumer.mdc.MDCUtils;
import no.nav.kjerneinfo.domain.person.GeografiskTilknytning;
import no.nav.kjerneinfo.domain.person.GeografiskTilknytningstyper;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.modig.common.SporingsAksjon;
import no.nav.modig.common.SporingsLogger;
import no.nav.modig.common.SporingsLoggerFactory;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollUtenTPS;
import no.nav.sbl.dialogarena.rsbac.CombiningAlgo;
import no.nav.sbl.dialogarena.rsbac.DecisionEnums;
import no.nav.sbl.dialogarena.rsbac.PolicySet;
import no.nav.tjeneste.virksomhet.person.v3.*;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentGeografiskTilknytningRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentGeografiskTilknytningResponse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentPersonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class HentPersonService {
    private static final Logger logger = LoggerFactory.getLogger(HentPersonService.class);
    private static final String ANSVARLIG_ENHET_ATTRIBUTE_ID = "urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet";
    private static final String DISCRETION_CODE_ATTRIBUTE_ID = "urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code";
    private static final String FNR_REGEX = "\\d{11}";

    private final PersonV3 service;
    private final KjerneinfoMapper mapper;
    private final OrganisasjonEnhetV2Service organisasjonEnhetV2Service;
    private final TilgangskontrollUtenTPS tilgangskontroll;

    HentPersonService(PersonV3 service, KjerneinfoMapper mapper, OrganisasjonEnhetV2Service organisasjonEnhetV2Service,
                      TilgangskontrollUtenTPS tilgangskontroll
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

        if(!erFnrGodkjent(requestIdent)) {
            logger.warn("{} er et ugyldig fødselsnummer, kan ikke hentes ut.", requestIdent);
            throw new ApplicationException("UgyldigFnr", new HentPersonPersonIkkeFunnet("Ugyldig fnr"), "hentkjerneinformasjonpersonikkefunnet.feilmelding");
        }

        WSHentPersonRequest wsRequest = lagWSHentPersonRequest(requestIdent);

        WSHentPersonResponse wsResponse;
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
        logSporingsInformasjon(response.getPerson());

        final GeografiskTilknytning geografiskTilknytning = response.getPerson().getPersonfakta().getGeografiskTilknytning();
        if (skalHenteEnhetFraNORG(geografiskTilknytning)) {
            oppdaterAnsvarligEnhetMedDataFraNORG(response, geografiskTilknytning);
        }

        return filtrerFamilierelasjonerSjekkForSenstiveData(verifyAuthorization(response, hentKjerneinformasjonRequest));
    }

    protected GeografiskTilknytning hentGeografiskTilknytning(String requestIdent) {
        Optional<WSHentGeografiskTilknytningResponse> response = hentGeografiskTilknytningFraService(requestIdent);
        return response
                .map(this::tilDomeneobjekt)
                .orElse(new GeografiskTilknytning());
    }

    private Optional<WSHentGeografiskTilknytningResponse> hentGeografiskTilknytningFraService(String requestIdent) {
        WSHentGeografiskTilknytningRequest request = new WSHentGeografiskTilknytningRequest().withAktoer(lagAktoer(requestIdent));

        WSHentGeografiskTilknytningResponse response;
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

    private GeografiskTilknytning tilDomeneobjekt(WSHentGeografiskTilknytningResponse response) {
        String geografiskTilknytning = getGeografiskTilknytning(response);
        String diskresjonskode = getDiskresjonskode(response);
        return new GeografiskTilknytning().withValue(geografiskTilknytning).withDiskresjonskode(diskresjonskode);
    }

    private String getDiskresjonskode(WSHentGeografiskTilknytningResponse response) {
        return Optional.ofNullable(response.getDiskresjonskode())
                .map(WSKodeverdi::getValue)
                .orElse(null);
    }

    private String getGeografiskTilknytning(WSHentGeografiskTilknytningResponse response) {
        return Optional.ofNullable(response.getGeografiskTilknytning())
                .map(WSGeografiskTilknytning::getGeografiskTilknytning)
                .orElse(null);
    }


    private boolean skalHenteEnhetFraNORG(GeografiskTilknytning geografiskTilknytning) {
        return geografiskTilknytning == null || geografiskTilknytning.getType() != GeografiskTilknytningstyper.LAND;
    }

    private WSHentPersonRequest lagWSHentPersonRequest(String requestIdent) {
        WSHentPersonRequest wsRequest = new WSHentPersonRequest()
                .withAktoer(lagAktoer(requestIdent))
                .withInformasjonsbehov(
                        WSInformasjonsbehov.ADRESSE,
                        WSInformasjonsbehov.BANKKONTO,
                        WSInformasjonsbehov.FAMILIERELASJONER,
                        WSInformasjonsbehov.KOMMUNIKASJON,
                        WSInformasjonsbehov.SPORINGSINFORMASJON);
        return wsRequest;
    }

    private WSPersonIdent lagAktoer(String ident) {
        return new WSPersonIdent().withIdent(new WSNorskIdent().withIdent(ident));
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

    private void logSporingsInformasjon(Person person) {
        try {
            SporingsLogger sporingsLogger = SporingsLoggerFactory.sporingsLogger(SporingUtils.configFileAsBufferedReader(getConfigAsInputStream(),
                    "kjerneinfo-sporing-config.txt"));
            sporingsLogger.logg(person, SporingsAksjon.Les);
        } catch (Exception e) {
            logger.error("hentPerson:SporingsLogger ble ikke opprettet.");
        }
    }

    private HentKjerneinformasjonResponse verifyAuthorization(HentKjerneinformasjonResponse response, HentKjerneinformasjonRequest request) {
        if (response == null
                || erOrganisasjonsenhetIkkeTom(response)
                || isBlank(getOrganisasjonsElementId(response))) {
            return response;
        }

        Personfakta personfakta = response.getPerson().getPersonfakta();
        String resourceId = personfakta.getAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId() == null?
                "":
                personfakta.getAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId();
        String diskresjonskode = response.getPerson().getPersonfakta().getDiskresjonskode() == null?
                "0":
                response.getPerson().getPersonfakta().getDiskresjonskode().getKodeRef();

        if (saksbehandlerHarTilgang(resourceId, diskresjonskode)) {
            return response;
        }

        boolean saksbehandlerHarTilgangMedUtvidbarRolle = harSaksbehandlerHarTilgangMedUtvidbarRolle(resourceId, diskresjonskode);
        if (saksbehandlerHarTilgangMedUtvidbarRolle && request.isBegrunnet()) {
            return response;
        } else if (saksbehandlerHarTilgangMedUtvidbarRolle) {
            throw new RecoverableAuthorizationException("Saksbehandler kan få tilgang til bruker ved begrunnelse.");
        }

        AuthorizationWithSikkerhetstiltakException exception = new AuthorizationWithSikkerhetstiltakException("sikkerhetsbegrensning.geografisk");
        exception.setSikkerhetstiltak(response.getPerson().getPersonfakta().getSikkerhetstiltak());

        throw exception;
    }

    private boolean harSaksbehandlerHarTilgangMedUtvidbarRolle(String ansvarligEnhet, String diskresjonskode) {
        return saksbehandlerHarTilgang(ansvarligEnhet, diskresjonskode);
    }

    private boolean saksbehandlerHarTilgang(String ansvarligEnhet, String diskresjonskode) {
        return tilgangskontroll.check(new PolicySet<>(CombiningAlgo.denyOverride, asList(
                Policies.tilgangTilEnhetId.with(ansvarligEnhet),
                Policies.tilgangTilDiskresjonskode.with(diskresjonskode)
        )))
                .getDecision()
                .getDecision()
                .equals(DecisionEnums.PERMIT);
    }

    private boolean erOrganisasjonsenhetIkkeTom(HentKjerneinformasjonResponse response) {
        return response.getPerson().getPersonfakta().getAnsvarligEnhet() == null
                || response.getPerson().getPersonfakta().getAnsvarligEnhet().getOrganisasjonsenhet() == null;
    }

    private String getOrganisasjonsElementId(HentKjerneinformasjonResponse response) {
        return response.getPerson().getPersonfakta().getAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId();
    }

    private HentKjerneinformasjonResponse filtrerFamilierelasjonerSjekkForSenstiveData(HentKjerneinformasjonResponse response) {
        if (response == null || response.getPerson() == null || response.getPerson().getPersonfakta() == null
                || response.getPerson().getPersonfakta().getHarFraRolleIList() == null) {
            return response;
        }

        Iterator it = response.getPerson().getPersonfakta().getHarFraRolleIList().iterator();
        while (it.hasNext()){
            Familierelasjon familierelasjon = (Familierelasjon)it.next();
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

    private InputStream getConfigAsInputStream() throws IOException {
        return getClass().getClassLoader().getResource("kjerneinfo-sporing-config.txt").openStream();
    }


    private boolean saksbehandlerHarTilgangTilDiskresjonskode(String diskresjonskode) {
        return tilgangskontroll.check(Policies.tilgangTilDiskresjonskode.with(diskresjonskode))
                .getDecision()
                .getDecision()
                .equals(DecisionEnums.PERMIT);
    }

    private boolean erFnrGodkjent(String fnr) {
        if(fnr == null) {
            return false;
        }
        return fnr.matches(FNR_REGEX);
    }
}
