package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Traad;
import org.apache.commons.collections15.Transformer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.containedIn;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding.TRAAD_ID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Traad.NYESTE_FORST;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.lucene.document.Field.Store.NO;

public class MeldingerSok {

    @Inject
    private HenvendelseBehandlingService henvendelse;

    private static final Logger logger = LoggerFactory.getLogger(MeldingerSok.class);
    public static final Integer TIME_TO_LIVE_MINUTES = 10;

    private static final String ID = "id";
    private static final String FRITEKST = "fritekst";
    private static final String TEMAGRUPPE = "temagruppe";
    private static final String ARKIVTEMA = "arkivtema";

    private static final String[] FIELDS = new String[]{FRITEKST, TEMAGRUPPE, ARKIVTEMA};

    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private MultiFieldQueryParser queryParser = new MultiFieldQueryParser(FIELDS, analyzer);

    private Map<String, List<Melding>> meldingerCache = new ConcurrentHashMap<>();
    private Map<String, RAMDirectory> directories = new ConcurrentHashMap<>();
    private Map<String, DateTime> indexingTimestamps = new ConcurrentHashMap<>();

    public MeldingerSok() {
        queryParser.setDefaultOperator(QueryParser.Operator.AND);
        queryParser.setAllowLeadingWildcard(true);
    }

    public void indekser(String fnr, String valgEnhet) {
        List<Melding> meldinger = henvendelse.hentMeldinger(fnr, valgEnhet);
        String navIdent = getSubjectHandler().getUid();

        String key = key(fnr, navIdent);
        meldingerCache.put(key, meldinger);
        directories.put(key, indekser(meldinger));
        indexingTimestamps.put(key, DateTime.now());
    }

    public List<Traad> sok(String fnr, String tekst) {
        try {
            String navIdent = getSubjectHandler().getUid();
            String key = key(fnr, navIdent);

            if (!directories.containsKey(key)) {
                throw new RuntimeException(String.format("Man må kalle %s.indekser før man kan søke", MeldingerSok.class.getName()));
            }

            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directories.get(key)));
            TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);

            Query query = queryParser.parse(query(tekst));
            searcher.search(query, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            final List<String> ider = hentBehandlingsIder(searcher, hits);
            Map<String, List<Melding>> traader = on(meldingerCache.get(key))
                    .filter(where(Melding.ID, containedIn(ider)))
                    .reduce(indexBy(TRAAD_ID));

            return on(traader.entrySet()).map(new Transformer<Map.Entry<String, List<Melding>>, Traad>() {
                @Override
                public Traad transform(Map.Entry<String, List<Melding>> entry) {
                    return new Traad(entry.getKey(), entry.getValue());
                }
            }).collect(NYESTE_FORST);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "1 * * * * *") // hvert minutt
    public void ryddOppCache() {
        logger.info("Starter opprydning av cache. Har {} directories", directories.size());
        int count = 0;
        for (Map.Entry<String, DateTime> entry : indexingTimestamps.entrySet()) {
            if (DateTime.now().minusMinutes(TIME_TO_LIVE_MINUTES).isAfter(entry.getValue())) {
                count++;
                String key = entry.getKey();
                indexingTimestamps.remove(key);
                directories.remove(key);
                meldingerCache.remove(key);
            }
        }
        logger.info("Fjernet {} directories", count);
    }

    private static String key(String fnr, String navIdent) {
        return fnr + "-" + navIdent;
    }

    private RAMDirectory indekser(List<Melding> meldinger) {
        try {
            RAMDirectory directory = new RAMDirectory();
            IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer));
            for (Melding melding : meldinger) {
                writer.addDocument(lagDokument(melding));
            }
            writer.commit();
            return directory;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Document lagDokument(Melding melding) {
        Document document = new Document();
        document.add(new StoredField(ID, melding.id));
        document.add(new TextField(FRITEKST, optional(melding.fritekst).getOrElse(""), NO));
        document.add(new TextField(TEMAGRUPPE, optional(melding.temagruppeNavn).getOrElse(""), NO));
        document.add(new TextField(ARKIVTEMA, optional(melding.journalfortTemanavn).getOrElse(""), NO));
        return document;
    }

    private static String query(String tekst) {
        return "*" + (tekst.isEmpty() ? ":" : trim(tekst)) + "*";
    }

    private static List<String> hentBehandlingsIder(final IndexSearcher searcher, ScoreDoc... hits) {
        return on(hits).map(new Transformer<ScoreDoc, String>() {
            @Override
            public String transform(ScoreDoc hit) {
                try {
                    return searcher.doc(hit.doc).get(ID);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).collect();
    }
}
