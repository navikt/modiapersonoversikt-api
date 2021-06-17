package no.nav.modiapersonoversikt.consumer.kodeverk2;

import no.nav.modiapersonoversikt.consumer.kodeverk2.exception.KodeverkTjenesteFeiletException;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKodeverk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class CachingKodeverkClient implements KodeverkClient {

    private static final Logger logger = LoggerFactory.getLogger(CachingKodeverkClient.class);

    private KodeverkClient kodeverkClient;
    private final Map<String, XMLKodeverk> kodeverkMap;
    private final Optional<File> dumpDirectory;
    private static final int EN_DAG = 24*60*60*1000;

    public CachingKodeverkClient(KodeverkClient kodeverkClient, Optional<File> dumpDirectory) {
        this.kodeverkClient = kodeverkClient;
        this.kodeverkMap = new HashMap<>();
        this.dumpDirectory = dumpDirectory;
    }

    @Override
    public XMLKodeverk hentKodeverk(String navn) {
        if (kodeverkMap.containsKey(navn)) {
            return kodeverkMap.get(navn);
        } else {
            return hentKodeverkFraTjenesteEllerDisk(navn);
        }
    }

    private XMLKodeverk hentKodeverkFraTjenesteEllerDisk(String navn) {
        XMLKodeverk kodeverk;
        try {
            kodeverk = kodeverkClient.hentKodeverk(navn);
            kodeverkMap.put(navn, kodeverk);
            writeToDump(navn, kodeverk);
        } catch (KodeverkTjenesteFeiletException kodeverkException) {
            try {
                kodeverk = readFromDump(navn);
            } catch (RuntimeException dumpException) {
                logger.error("Fallback feilet ({}), avbryter.", dumpException.getMessage());
                kodeverkException.addSuppressed(dumpException);
                throw kodeverkException;
            }
        }
        return kodeverk;
    }

    @Override
    public String hentFoersteTermnavnForKode(String kodenavn, String kodeverknavn) {
        if (!kodeverkMap.containsKey(kodeverknavn)) {
            kodeverkMap.put(kodeverknavn, hentKodeverk(kodeverknavn));
        }

        return kodeverkClient.hentFoersteTermnavnForKode(kodenavn, (XMLEnkeltKodeverk) kodeverkMap.get(kodeverknavn));
    }

    @Override
    public String hentFoersteTermnavnForKode(String kodenavn, XMLEnkeltKodeverk xmlEnkeltKodeverk) {
        return kodeverkClient.hentFoersteTermnavnForKode(kodenavn, xmlEnkeltKodeverk);
    }

    @Scheduled(fixedRate = EN_DAG)
    public void oppdaterKodeverk() {
        Map<String, XMLKodeverk> oppdatertKodeverkMap = new HashMap<>();
        Set<String> alleBrukteKodeverkNavn = kodeverkMap.keySet();

        for (String kodeverkNavn : alleBrukteKodeverkNavn) {
            XMLKodeverk kodeverk = hentKodeverkFraTjenesteEllerDisk(kodeverkNavn);
            oppdatertKodeverkMap.put(kodeverkNavn, kodeverk);
            logger.info("Periodisk oppdatering av kodeverk med navn: " + kodeverkNavn);
        }
        kodeverkMap.clear();
        kodeverkMap.putAll(oppdatertKodeverkMap);
    }

    private static final JAXBContext JAXB;

    static {
        try {
            JAXB = JAXBContext.newInstance(XMLKodeverk.class);
        } catch (JAXBException e) {
            throw new RuntimeException(
                    "Unable to load class " + DefaultKodeverkClient.class.getName() +
                            ", error creating JAXB context for " + XMLKodeverk.class.getName() + ": " + e.getMessage(), e);
        }
    }

    private void writeToDump(String dumpName, XMLKodeverk kodeverket) {
        Optional<File> dumpFile = dumpDirectory
                .filter(file ->  file.isDirectory() || (file.mkdirs() && file.isDirectory()))
                .map(file -> new File(file, dumpName + ".xml"));

        if (dumpFile.isPresent()) {
            logger.info("Dumper til filen '{}'", dumpFile.get());
            try (Writer out = new FileWriter(dumpFile.get())) {
                JAXB.createMarshaller().marshal(new JAXBElement<>(
                        new QName(DefaultKodeverkClient.class.getName() + "." + dumpName, dumpName), XMLKodeverk.class, kodeverket), out);
            } catch (JAXBException | IOException e) {
                logger.error("Klarte ikke å dumpe '{}' til fil. {}\n{}", dumpName, e.getMessage(), e);
            }
        }
    }

    private XMLKodeverk readFromDump(String dumpName) {
        Optional<File> dumpFile = dumpDirectory
                .filter(file -> file.exists())
                .map(file -> new File(file, dumpName + ".xml"));

        if (dumpFile.isPresent()) {
            logger.info("Leser dump fra fil '{}'", dumpFile.get());
            try {
                @SuppressWarnings("unchecked")
                JAXBElement<XMLKodeverk> jaxbElement = (JAXBElement<XMLKodeverk>) JAXB.createUnmarshaller().unmarshal(dumpFile.get());
                return jaxbElement.getValue();
            } catch (JAXBException e) {
                throw new RuntimeException("Feil ved innlasting av dump " + dumpFile.get() + ": " + e.getMessage(), e);
            }
        }
        throw new IllegalStateException("Forsøkte å laste fildump '" + dumpName + ".xml', men fant ikke filen");
    }
}