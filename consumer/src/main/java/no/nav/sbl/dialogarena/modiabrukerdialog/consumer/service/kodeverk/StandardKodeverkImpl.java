package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import no.nav.common.utils.IdUtils;
import no.nav.log.MDCConstants;
import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static javax.xml.bind.JAXBContext.newInstance;
import static org.joda.time.DateTime.now;

/**
 * Tilbyr kodeverkoppslag. Implementasjonen laster kodeverk for arkivtemaer fra webservice on-demand,
 * og lagrer de i en intern struktur som brukes ved oppslag. Klassen har fallback-mekanisme ved at
 * den dumper allerede innlastet kodverk til fil(er), og dersom kall til webservice feiler
 * vil ev. eksisterende fildump brukes til å populere den interne datastrukturen.
 */

public class StandardKodeverkImpl implements StandardKodeverk {

    private static final Logger logger = LoggerFactory.getLogger(StandardKodeverkImpl.class);
    private static final Locale NORSK_BOKMAAL = new Locale("nb", "no");
    private static final String ARKIVTEMA_KODEVERKNAVN = "Arkivtemaer";

    private String spraak;
    private Map<String, XMLEnkeltKodeverk> kodeverk;

    private File dumpDirectory;
    private boolean dumpActive;

    @Inject
    @Qualifier("kodeverkPortTypeV2")
    private KodeverkPortType kodeverkPortType;

    @Value("${modiabrukerdialog.datadir:/tmp}")
    private File brukerprofilDataDirectory;

    @Override
    @PostConstruct
    public void initKodeverk() {
        if (brukerprofilDataDirectory == null || brukerprofilDataDirectory.getName().startsWith("${")) {
            brukerprofilDataDirectory = new File(EnvironmentUtils.getRequiredProperty("java.io.tmpdir"));
            logger.warn("Definer property 'modiabrukerdialog.datadir' for å aktivere fallback for kodeverk dersom tjenesten går ned");
        }
        if (brukerprofilDataDirectory != null) {
            dumpDirectory = new File(brukerprofilDataDirectory, "kodeverkdump");
            dumpActive = true;
            logger.info("Benytter katalog {} til å ta vare på kodeverk, i tilfelle tjeneste går ned.", dumpDirectory);
        } else {
            logger.info("Kodeverk-failback er ikke aktivert.");
            dumpActive = false;
        }
        try {
            lastInnNyeKodeverk();
        } catch (RuntimeException ex) {
            logger.warn("Kunne ikke hente kodeverk under oppstart av applikasjon. " + ex, ex);
        }
    }

    public StandardKodeverkImpl() {
        this.spraak = NORSK_BOKMAAL.getLanguage();
        this.kodeverk = new HashMap<>();
    }

    @Override
    public String getArkivtemaNavn(String arkivtemaKode) {
        return hentFoersteGyldigeTermnavnFraGyldigKodeIKodeverk(arkivtemaKode, ARKIVTEMA_KODEVERKNAVN);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *") // Midnatt hver dag
    public void lastInnNyeKodeverk() {
        MDC.put(MDCConstants.MDC_CALL_ID, IdUtils.generateId());
        Map<String, XMLEnkeltKodeverk> oppdatertKodeverk = new HashMap<>();
        XMLEnkeltKodeverk enkeltkodeverk = initKodeverkMedNavn(ARKIVTEMA_KODEVERKNAVN);
        oppdatertKodeverk.put(ARKIVTEMA_KODEVERKNAVN, enkeltkodeverk);
        this.kodeverk.clear();
        this.kodeverk.putAll(oppdatertKodeverk);
    }

    private XMLEnkeltKodeverk initKodeverkMedNavn(String kodeverksnavn) {
        XMLEnkeltKodeverk enkeltkodeverk = hentKodeverk(kodeverksnavn);
        List<XMLKode> gyldige = getGyldigeKodeverk(enkeltkodeverk);
        enkeltkodeverk.getKode().clear();
        enkeltkodeverk.getKode().addAll(gyldige);
        return enkeltkodeverk;
    }

    private List<XMLKode> getGyldigeKodeverk(XMLEnkeltKodeverk enkeltkodeverk) {
        return enkeltkodeverk.getKode().stream()
                .filter(kode -> GYLDIGHETSPERIODER.apply(kode).stream()
                        .anyMatch(periodeMed(now())))
                .collect(toList());
    }

    private XMLEnkeltKodeverk kodeverkMedNavn(String kodeverknavn) {
        XMLEnkeltKodeverk kodeverket = kodeverk.get(kodeverknavn);
        if (kodeverket != null) {
            return kodeverket;
        }
        kodeverk.put(kodeverknavn, initKodeverkMedNavn(kodeverknavn));
        return kodeverk.get(kodeverknavn);
    }

    private String hentFoersteGyldigeTermnavnFraGyldigKodeIKodeverk(String kodenavn, String kodeverknavn) {
        for (XMLKode kode : kodeverkMedNavn(kodeverknavn).getKode()) {
            if (kode.getNavn().equalsIgnoreCase(kodenavn) && erGyldigPeriode(kode.getGyldighetsperiode())) {
                for (XMLTerm term : kode.getTerm()) {
                    if (erGyldigPeriode(term.getGyldighetsperiode())) {
                        return term.getNavn();
                    }
                }
            }
        }
        return null;
    }

    private boolean erGyldigPeriode(List<XMLPeriode> gyldighetsperiode) {
        if (gyldighetsperiode == null || gyldighetsperiode.isEmpty()) {
            return true;
        }
        DateMidnight fom = gyldighetsperiode.get(0).getFom();
        DateMidnight tom = gyldighetsperiode.get(0).getTom();
        DateMidnight now = DateMidnight.now();

        return now.isAfter(fom) && now.isBefore(tom);
    }

    private XMLEnkeltKodeverk hentKodeverk(String navn) {
        XMLEnkeltKodeverk kodeverket = null;
        Optional<RuntimeException> webserviceException = empty();
        try {
            kodeverket = (XMLEnkeltKodeverk) kodeverkPortType.hentKodeverk(new XMLHentKodeverkRequest().withNavn(navn).withSpraak(spraak)).getKodeverk();
        } catch (HentKodeverkHentKodeverkKodeverkIkkeFunnet kodeverkIkkeFunnet) {
            throw new SystemException("Kodeverk '" + navn + "' (" + spraak + "): " + kodeverkIkkeFunnet.getMessage(), kodeverkIkkeFunnet);
        } catch (RuntimeException e) {
            webserviceException = of(e);
        }

        if (webserviceException.isPresent()) {
            RuntimeException kodeverkfeil = webserviceException.get();
            if (kodeverk.containsKey(navn)) {
                logger.warn("Kodeverktjeneste feilet ({}) for {}. Benytter eksisterende kodeverk i minne.", kodeverkfeil.getMessage(), navn);
                return kodeverk.get(navn);
            }
            logger.warn("Kodeverktjeneste feilet ({})! Forsøker fallback", kodeverkfeil.getMessage());
            try {
                kodeverket = (XMLEnkeltKodeverk) readFromDump(navn);
            } catch (RuntimeException dumpException) {
                logger.warn("Fallback feilet ({}), avbryter.", dumpException.getMessage());
                kodeverkfeil.addSuppressed(dumpException);
                throw kodeverkfeil;
            }
        } else {
            dumpIfPossible(navn, kodeverket);
        }
        return kodeverket;
    }

    private static final Function<XMLKode, List<XMLPeriode>> GYLDIGHETSPERIODER =
            kode -> kode.getGyldighetsperiode() != null ? kode.getGyldighetsperiode() : (Collections.emptyList());

    private static Predicate<XMLPeriode> periodeMed(final DateTime atTime) {
        return periode -> atTime.isAfter(periode.getFom()) && atTime.isBefore(periode.getTom());
    }

    private static final JAXBContext JAXB;

    static {
        try {
            JAXB = newInstance(XMLKodeverk.class);
        } catch (JAXBException e) {
            throw new RuntimeException(createErrorMessage(e), e);
        }
    }

    private static String createErrorMessage(JAXBException e) {
        return "Unable to load class " + StandardKodeverkImpl.class.getName() + ", error creating JAXB context for " + XMLKodeverk.class.getName() + ": " + e.getMessage();
    }

    @SuppressWarnings("unchecked")
    private XMLKodeverk readFromDump(String dumpName) {
        if (dumpActive && dumpDirectory.exists()) {
            File dumpFile = new File(dumpName + ".xml");

            logger.info("Leser dump fra fil '{}'", dumpFile);

            try {
                return ((JAXBElement<XMLKodeverk>) JAXB.createUnmarshaller().unmarshal(dumpFile)).getValue();
            } catch (JAXBException e) {
                throw new RuntimeException("Feil ved innlasting av dump " + dumpFile + ": " + e.getMessage(), e);
            }

        } else {
            throw new IllegalStateException("Forsøkte å laste fildump '" + dumpName + ".xml', men fant ikke filen");
        }
    }

    private void dumpIfPossible(String dumpName, XMLKodeverk kodeverket) {

        if (dumpActive) {
            makeDirs(dumpDirectory);
            File dumpFile = new File(dumpDirectory, dumpName + ".xml");

            logger.info("Dumper til filen '{}'", dumpFile);

            try (Writer out = new FileWriter(dumpFile)) {
                JAXB.createMarshaller().marshal(createJAXBElement(dumpName, kodeverket), out);
            } catch (JAXBException | IOException e) {
                logger.error("Klarte ikke å dumpe '{}' til fil. {}\n{}", dumpName, e.getMessage(), e);
            }
        }
    }

    private JAXBElement<XMLKodeverk> createJAXBElement(String dumpName, XMLKodeverk kodeverket) {
        return new JAXBElement<>(createQName(dumpName), XMLKodeverk.class, kodeverket);
    }

    private QName createQName(String dumpName) {
        return new QName(StandardKodeverkImpl.class.getName() + "." + dumpName, dumpName);
    }

    private File makeDirs(File file) {
        if (file.isDirectory() || (file.mkdirs() && file.isDirectory())) {
            return file;
        } else {
            throw new RuntimeException("Unable to make directories: " + file + (file.isFile() ? " It is an existing file!" : ""));
        }
    }

}
