package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.journal.v2.*;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.journal.v2.meldinger.*;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class JoarkPortTypeMock {
    public static JournalV2 createJournalV2Mock() {

        return new JournalV2() {

            @Override
            public WSHentJournalpostListeResponse hentJournalpostListe(WSHentJournalpostListeRequest request) throws HentJournalpostListeSikkerhetsbegrensning {
                WSHentJournalpostListeResponse response = new WSHentJournalpostListeResponse();

                List<WSJournalpost> journalposts = new ArrayList<>();
                Map<String, List<WSJournalpost>> journalPoster = new HashMap<>();

                List<WSSak> saker = request.getSakListe();

                getRandomGeneratedJournalposter(journalPoster, saker);

                leggTilJournalposterSomHarSaksidIResponse(journalposts, journalPoster, saker);

                response.getJournalpostListe().addAll(journalposts);
                return response;
            }

            @Override
            public WSHentDokumentURLResponse hentDokumentURL(WSHentDokumentURLRequest request) throws HentDokumentURLSikkerhetsbegrensning, HentDokumentURLDokumentIkkeFunnet {
                return null;
            }

            @Override
            public WSHentDokumentResponse hentDokument(WSHentDokumentRequest request) throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet {
                return new WSHentDokumentResponse();
            }

            @Override
            public void ping() {}

        };
    }


    private static void leggTilJournalposterSomHarSaksidIResponse(List<WSJournalpost> journalposts, Map<String, List<WSJournalpost>> journalPoster, List<WSSak> saker) {
        saker.stream()
                .forEach(sak -> {
                    if (journalPoster.containsKey(sak.getSakId())) {
                        journalposts.addAll(journalPoster.get(sak.getSakId()));
                    }
                });
    }

    private static void getRandomGeneratedJournalposter(Map<String, List<WSJournalpost>> journalPoster, List<WSSak> saker) {
        Random randomJournalpostIndex = new Random();
        IntStream indexJournalpostIndexer = randomJournalpostIndex.ints(0, saker.size()).distinct().limit(4);
        List<Integer> index = indexJournalpostIndexer.boxed().collect(Collectors.toList());
        int i = 0;
        String saksId = saker.get(index.get(i)).getSakId();
        journalPoster.put(saksId, asList(mottattSoknad(saksId, "DAG", new DateTime().minusDays(20))));
        saksId = saker.get(index.get(++i)).getSakId();
        journalPoster.put(saksId, asList(soknadUnderBehandling(saksId, "OMS", new DateTime().minusDays(40))));
        saksId = saker.get(index.get(++i)).getSakId();
        journalPoster.put(saksId, asList(soknadUnderBehandling(saksId, "OPP", new DateTime().minusDays(19))));
        saksId = saker.get(index.get(++i)).getSakId();
        journalPoster.put(saksId, asList(
                soknadUnderBehandling(saksId, "DAG", new DateTime().minusDays(100)),
                mottattSoknad(saksId, "DAG", new DateTime()),
                mottattBekreftelse(saksId, "DAG", new DateTime().minusDays(200)),
                forvaltningsnotat(saksId, "DAG", new DateTime())
        ));
    }


    private static WSJournalpost mottattSoknad(String id, String tema, DateTime mottattDato) {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId(id);
        journalpost.setEksternPart(new WSPerson().withIdent("11111111111").withNavn("Andreas"));
        WSArkivtemaer arkivtemaer = new WSArkivtemaer();
        arkivtemaer.setValue(tema);
        journalpost.setArkivtema(arkivtemaer);
        WSRegistertSak sak = new WSRegistertSak();
        sak.setSakId(id);
        journalpost.setGjelderSak(sak);
        journalpost.getDokumentinfoRelasjonListe()
                .add(dokumentInfoRelasjonMedTittel("Dagpengesøknad"));
        WSKommunikasjonsretninger kommunikasjonsretninger = new WSKommunikasjonsretninger();
        kommunikasjonsretninger.setValue("I");
        journalpost.setKommunikasjonsretning(kommunikasjonsretninger);
        try {
            journalpost.setMottatt(DatatypeFactory.newInstance().newXMLGregorianCalendar(mottattDato.toGregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return journalpost;
    }

    private static WSJournalpost soknadUnderBehandling(String id, String tema, DateTime mottattDato) {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId(id);
        journalpost.setEksternPart(new WSPerson().withIdent("11111111111").withNavn("Andreas"));
        WSArkivtemaer arkivtemaer = new WSArkivtemaer();
        arkivtemaer.setValue(tema);
        journalpost.setArkivtema(arkivtemaer);
        WSRegistertSak sak = new WSRegistertSak();
        sak.setSakId(id);
        journalpost.setGjelderSak(sak);
        WSKommunikasjonsretninger kommunikasjonsretninger = new WSKommunikasjonsretninger();
        kommunikasjonsretninger.setValue("I");
        journalpost.setKommunikasjonsretning(kommunikasjonsretninger);
        journalpost.getDokumentinfoRelasjonListe().add(dokumentInfoRelasjonMedTittel("Klage om behandling av dagpenger"));
        try {
            journalpost.setMottatt(DatatypeFactory.newInstance().newXMLGregorianCalendar(mottattDato.toGregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return journalpost;
    }


    private static WSJournalpost mottattBekreftelse(String id, String tema, DateTime mottattDato) {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId(id);
        journalpost.setEksternPart(new WSPerson().withIdent("11111111111").withNavn("Andreas"));
        WSArkivtemaer arkivtemaer = new WSArkivtemaer();
        arkivtemaer.setValue(tema);
        journalpost.setArkivtema(arkivtemaer);
        WSRegistertSak sak = new WSRegistertSak();
        sak.setSakId(id);
        journalpost.setGjelderSak(sak);
        WSKommunikasjonsretninger kommunikasjonsretninger = new WSKommunikasjonsretninger();
        kommunikasjonsretninger.setValue("U");
        journalpost.setKommunikasjonsretning(kommunikasjonsretninger);
        journalpost.getDokumentinfoRelasjonListe().add(dokumentInfoRelasjonMedTittel("Testdata dagpenger"));
        try {
            journalpost.setSendt(DatatypeFactory.newInstance().newXMLGregorianCalendar(mottattDato.toGregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return journalpost;
    }


    private static WSJournalpost forvaltningsnotat(String id, String tema, DateTime mottattDato) {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId(id);
        WSArkivtemaer arkivtemaer = new WSArkivtemaer();
        arkivtemaer.setValue(tema);
        journalpost.setArkivtema(arkivtemaer);
        WSRegistertSak sak = new WSRegistertSak();
        sak.setSakId(id);
        journalpost.setGjelderSak(sak);
        WSKommunikasjonsretninger kommunikasjonsretninger = new WSKommunikasjonsretninger();
        kommunikasjonsretninger.setValue("N");
        journalpost.setKommunikasjonsretning(kommunikasjonsretninger);
        journalpost.getDokumentinfoRelasjonListe().add(dokumentInfoRelasjonMedTittel("Tittel på forvaltningsnotat"));
        journalpost.getDokumentinfoRelasjonListe().add(vedleggInfoRelasjonMedTittel("Tittel på vedlegg for forvaltningsnotat"));


        try {
            journalpost.setSendt(DatatypeFactory.newInstance().newXMLGregorianCalendar(mottattDato.toGregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return journalpost;
    }

    private static WSDokumentinfoRelasjon dokumentInfoRelasjonMedTittel(String tittel) {
        WSJournalfoertDokumentInfo dokumentInfo = new WSJournalfoertDokumentInfo();
        dokumentInfo.setTittel(tittel);
        dokumentInfo.setDokumentId("123");
        WSDokumentinfoRelasjon dokumentinfoRelasjon = new WSDokumentinfoRelasjon();
        dokumentinfoRelasjon.setJournalfoertDokument(dokumentInfo);
        dokumentinfoRelasjon.setDokumentTilknyttetJournalpost(lagTilknyttetJournalpostSom());
        return dokumentinfoRelasjon;
    }

    private static WSTilknyttetJournalpostSom lagTilknyttetJournalpostSom() {
        WSTilknyttetJournalpostSom tilknyttetJournalpostSom = new WSTilknyttetJournalpostSom();
        tilknyttetJournalpostSom.setValue("HOVEDDOKUMENT");

        return tilknyttetJournalpostSom;
    }

    private static WSDokumentinfoRelasjon vedleggInfoRelasjonMedTittel(String tittel) {
        WSJournalfoertDokumentInfo dokumentInfo = new WSJournalfoertDokumentInfo();
        dokumentInfo.setTittel(tittel);
        dokumentInfo.setDokumentId("456");
        WSDokumentinfoRelasjon dokumentinfoRelasjon = new WSDokumentinfoRelasjon();
        dokumentinfoRelasjon.setJournalfoertDokument(dokumentInfo);
        dokumentinfoRelasjon.setDokumentTilknyttetJournalpost(lagTilknyttetJournalpostSomVedlegg());
        return dokumentinfoRelasjon;
    }

    private static WSTilknyttetJournalpostSom lagTilknyttetJournalpostSomVedlegg() {
        WSTilknyttetJournalpostSom tilknyttetJournalpostSom = new WSTilknyttetJournalpostSom();
        tilknyttetJournalpostSom.setValue("VEDLEGG");

        return tilknyttetJournalpostSom;
    }
}