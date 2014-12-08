package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import no.nav.modig.common.MDCOperations;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.StandardKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLPeriode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static javax.xml.bind.JAXBContext.newInstance;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.exists;
import static no.nav.modig.lang.collections.PredicateUtils.fileExists;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.TransformerUtils.appendPathname;
import static no.nav.modig.lang.collections.TransformerUtils.makeDirs;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
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

    private Optional<File> dumpDirectory;

    @Inject
    @Qualifier("kodeverkPortTypeV2")
    private KodeverkPortType kodeverkPortType;

    @Value("${modiabrukerdialog.datadir}")
    private File brukerprofilDataDirectory;

    @Override
    @PostConstruct
    public void initKodeverk() {
        if (brukerprofilDataDirectory == null || brukerprofilDataDirectory.getName().startsWith("${")) {
            brukerprofilDataDirectory = new File(System.getProperty("java.io.tmpdir"));
            logger.warn("Definer property 'modiabrukerdialog.datadir' for å aktivere fallback for kodeverk dersom tjenesten går ned");
        }
        dumpDirectory = optional(brukerprofilDataDirectory).map(appendPathname("kodeverkdump"));
        if (dumpDirectory.isSome()) {
            logger.info("Benytter katalog {} til å ta vare på kodeverk, i tilfelle tjeneste går ned.", dumpDirectory);
        } else {
            logger.info("Kodeverk-failback er ikke aktivert.");
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
        return hentFoersteTermnavnFraKodeIKodeverk(arkivtemaKode, ARKIVTEMA_KODEVERKNAVN);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void lastInnNyeKodeverk() {
        MDCOperations.putToMDC(MDCOperations.MDC_CALL_ID, MDCOperations.generateCallId());
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
        return on(enkeltkodeverk.getKode()).filter(where(GYLDIGHETSPERIODER, exists(periodeMed(now())))).collect();
    }

    private XMLEnkeltKodeverk kodeverkMedNavn(String kodeverknavn) {
        XMLEnkeltKodeverk kodeverket = kodeverk.get(kodeverknavn);
        if (kodeverket != null) {
            return kodeverket;
        }
        kodeverk.put(kodeverknavn, initKodeverkMedNavn(kodeverknavn));
        return kodeverk.get(kodeverknavn);
    }

    private String hentFoersteTermnavnFraKodeIKodeverk(String kodenavn, String kodeverknavn) {
        for (XMLKode kode : kodeverkMedNavn(kodeverknavn).getKode()) {
            if (kode.getNavn().equalsIgnoreCase(kodenavn)) {
                return kode.getTerm().get(0).getNavn();
            }
        }
        return null;
    }

    private XMLEnkeltKodeverk hentKodeverk(String navn) {
        XMLEnkeltKodeverk kodeverket = null;
        Optional<RuntimeException> webserviceException = none();
        try {
            kodeverket = (XMLEnkeltKodeverk) kodeverkPortType.hentKodeverk(new XMLHentKodeverkRequest().withNavn(navn).withSpraak(spraak)).getKodeverk();
        } catch (HentKodeverkHentKodeverkKodeverkIkkeFunnet kodeverkIkkeFunnet) {
            throw new SystemException("Kodeverk '" + navn + "' (" + spraak + "): " + kodeverkIkkeFunnet.getMessage(), kodeverkIkkeFunnet);
        } catch (RuntimeException e) {
            webserviceException = optional(e);
        }

        if (webserviceException.isSome()) {
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

    private static final Transformer<XMLKode, List<XMLPeriode>> GYLDIGHETSPERIODER = new Transformer<XMLKode, List<XMLPeriode>>() {
        @Override
        public List<XMLPeriode> transform(XMLKode kode) {
            return optional(kode.getGyldighetsperiode()).getOrElse(Collections.<XMLPeriode>emptyList());
        }
    };

    private static Predicate<XMLPeriode> periodeMed(final DateTime atTime) {
        return new Predicate<XMLPeriode>() {
            @Override
            public boolean evaluate(XMLPeriode periode) {
                return atTime.isAfter(periode.getFom()) && atTime.isBefore(periode.getTom());
            }
        };
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
        for (File dumpFile : dumpDirectory.map(fileExists(), appendPathname(dumpName + ".xml"))) {
            logger.info("Leser dump fra fil '{}'", dumpFile);
            try {
                return ((JAXBElement<XMLKodeverk>) JAXB.createUnmarshaller().unmarshal(dumpFile)).getValue();
            } catch (JAXBException e) {
                throw new RuntimeException("Feil ved innlasting av dump " + dumpFile + ": " + e.getMessage(), e);
            }
        }
        throw new IllegalStateException("Forsøkte å laste fildump '" + dumpName + ".xml', men fant ikke filen");
    }

    private void dumpIfPossible(String dumpName, XMLKodeverk kodeverket) {
        for (File dumpFile : dumpDirectory.map(makeDirs()).map(appendPathname(dumpName + ".xml"))) {
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

}
