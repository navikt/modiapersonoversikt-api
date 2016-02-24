package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.innsynjournal.v1.HentDokumentDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.HentDokumentSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.HentTilgjengeligJournalpostListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.InnsynJournalV1;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.meldinger.HentDokumentRequest;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.meldinger.HentDokumentResponse;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.meldinger.HentTilgjengeligJournalpostListeRequest;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.meldinger.HentTilgjengeligJournalpostListeResponse;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Configuration;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

@Configuration
public class JoarkPortTypeMock {
    public static InnsynJournalV1 createInnsynJournalV1Mock() {

        return new InnsynJournalV1() {

            @Override
            public void ping() {
            }

            @Override
            public HentTilgjengeligJournalpostListeResponse hentTilgjengeligJournalpostListe(HentTilgjengeligJournalpostListeRequest hentTilgjengeligJournalpostListeRequest)
                    throws HentTilgjengeligJournalpostListeSikkerhetsbegrensning {

                HentTilgjengeligJournalpostListeResponse response = new HentTilgjengeligJournalpostListeResponse();

                List<Journalpost> journalposts = new ArrayList<>();
                Map<String, List<Journalpost>> journalPoster = new HashMap<>();

                List<Sak> saker = hentTilgjengeligJournalpostListeRequest.getSakListe();

                getRandomGeneratedJournalposter(journalPoster, saker);

                leggTilJournalposterSomHarSaksidIResponse(journalposts, journalPoster, saker);

                response.getJournalpostListe().addAll(journalposts);
                return response;
            }

            @Override
            public HentDokumentResponse hentDokument(HentDokumentRequest hentDokumentRequest) throws
                    HentDokumentDokumentIkkeFunnet, HentDokumentSikkerhetsbegrensning {
                return new HentDokumentResponse();
            }
        };
    }

    private static void leggTilJournalposterSomHarSaksidIResponse(List<Journalpost> journalposts, Map<String, List<Journalpost>> journalPoster, List<Sak> saker) {
        saker.stream()
                .forEach(sak -> {
                    if (journalPoster.containsKey(sak.getSakId())) {
                        journalposts.addAll(journalPoster.get(sak.getSakId()));
                    }
                });
    }

    private static void getRandomGeneratedJournalposter(Map<String, List<Journalpost>> journalPoster, List<Sak> saker) {
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


    private static Journalpost mottattSoknad(String id, String tema, DateTime mottattDato) {
        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId(id);
        journalpost.setBrukerErAvsenderMottaker(AvsenderMottaker.JA);
        Arkivtemaer arkivtemaer = new Arkivtemaer();
        arkivtemaer.setValue(tema);
        journalpost.setArkivtema(arkivtemaer);
        journalpost.setEksternPart("");
        Sak sak = new Sak();
        sak.setSakId(id);
        journalpost.setGjelderSak(sak);
        journalpost.getDokumentinfoRelasjonListe()
                .add(dokumentInfoRelasjonMedTittel("Dagpengesøknad"));
        Kommunikasjonsretninger kommunikasjonsretninger = new Kommunikasjonsretninger();
        kommunikasjonsretninger.setValue("I");
        journalpost.setKommunikasjonsretning(kommunikasjonsretninger);
        try {
            journalpost.setMottatt(DatatypeFactory.newInstance().newXMLGregorianCalendar(mottattDato.toGregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return journalpost;
    }

    private static Journalpost soknadUnderBehandling(String id, String tema, DateTime mottattDato) {
        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId(id);
        journalpost.setBrukerErAvsenderMottaker(AvsenderMottaker.JA);
        Arkivtemaer arkivtemaer = new Arkivtemaer();
        arkivtemaer.setValue(tema);
        journalpost.setArkivtema(arkivtemaer);
        journalpost.setEksternPart("");
        Sak sak = new Sak();
        sak.setSakId(id);
        journalpost.setGjelderSak(sak);
        Kommunikasjonsretninger kommunikasjonsretninger = new Kommunikasjonsretninger();
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


    private static Journalpost mottattBekreftelse(String id, String tema, DateTime mottattDato) {
        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId(id);
        journalpost.setBrukerErAvsenderMottaker(AvsenderMottaker.NEI);
        Arkivtemaer arkivtemaer = new Arkivtemaer();
        arkivtemaer.setValue(tema);
        journalpost.setArkivtema(arkivtemaer);
        journalpost.setEksternPart("");
        Sak sak = new Sak();
        sak.setSakId(id);
        journalpost.setGjelderSak(sak);
        Kommunikasjonsretninger kommunikasjonsretninger = new Kommunikasjonsretninger();
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


    private static Journalpost forvaltningsnotat(String id, String tema, DateTime mottattDato) {
        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId(id);
        journalpost.setBrukerErAvsenderMottaker(AvsenderMottaker.NEI);
        Arkivtemaer arkivtemaer = new Arkivtemaer();
        arkivtemaer.setValue(tema);
        journalpost.setArkivtema(arkivtemaer);
        journalpost.setEksternPart("");
        Sak sak = new Sak();
        sak.setSakId(id);
        journalpost.setGjelderSak(sak);
        Kommunikasjonsretninger kommunikasjonsretninger = new Kommunikasjonsretninger();
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

    private static DokumentinfoRelasjon dokumentInfoRelasjonMedTittel(String tittel) {
        JournalfoertDokumentInfo dokumentInfo = new JournalfoertDokumentInfo();
        dokumentInfo.setTittel(tittel);
        dokumentInfo.setDokumentId("123");
        dokumentInfo.setInnsynDokument(InnsynDokument.JA);
        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        dokumentinfoRelasjon.setJournalfoertDokument(dokumentInfo);
        dokumentinfoRelasjon.setDokumentTilknyttetJournalpost(lagTilknyttetJournalpostSom());
        return dokumentinfoRelasjon;
    }

    private static TilknyttetJournalpostSom lagTilknyttetJournalpostSom() {
        TilknyttetJournalpostSom tilknyttetJournalpostSom = new TilknyttetJournalpostSom();
        tilknyttetJournalpostSom.setValue("HOVEDDOKUMENT");

        return tilknyttetJournalpostSom;
    }

    private static DokumentinfoRelasjon vedleggInfoRelasjonMedTittel(String tittel) {
        JournalfoertDokumentInfo dokumentInfo = new JournalfoertDokumentInfo();
        dokumentInfo.setTittel(tittel);
        dokumentInfo.setDokumentId("456");
        dokumentInfo.setInnsynDokument(InnsynDokument.JA);
        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        dokumentinfoRelasjon.setJournalfoertDokument(dokumentInfo);
        dokumentinfoRelasjon.setDokumentTilknyttetJournalpost(lagTilknyttetJournalpostSomVedlegg());
        return dokumentinfoRelasjon;
    }

    private static TilknyttetJournalpostSom lagTilknyttetJournalpostSomVedlegg() {
        TilknyttetJournalpostSom tilknyttetJournalpostSom = new TilknyttetJournalpostSom();
        tilknyttetJournalpostSom.setValue("VEDLEGG");

        return tilknyttetJournalpostSom;
    }
}
