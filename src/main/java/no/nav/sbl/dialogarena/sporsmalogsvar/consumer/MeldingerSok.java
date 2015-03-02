package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.lang.collections.TransformerUtils;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Traad;
import org.apache.commons.collections15.Transformer;
import org.apache.lucene.analysis.TokenStream;
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
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.HashMap;
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
import static org.apache.lucene.document.Field.Store.YES;
import static org.joda.time.DateTime.now;

public class MeldingerSok {

    private static final Logger logger = LoggerFactory.getLogger(MeldingerSok.class);
    public static final Integer TIME_TO_LIVE_MINUTES = 10;

    private static final String ID = "id";
    private static final String BEHANDLINGS_ID = "behandlingsId";
    private static final String FRITEKST = "fritekst";
    private static final String TEMAGRUPPE = "temagruppe";
    private static final String ARKIVTEMA = "arkivtema";

    private static final String[] FIELDS = new String[]{FRITEKST, TEMAGRUPPE, ARKIVTEMA};
    private static final StandardAnalyzer ANALYZER = new StandardAnalyzer();

    private MultiFieldQueryParser queryParser = new MultiFieldQueryParser(FIELDS, ANALYZER);

    protected Map<String, List<Melding>> meldingerCache = new ConcurrentHashMap<>();
    protected Map<String, RAMDirectory> directories = new ConcurrentHashMap<>();
    protected Map<String, DateTime> indexingTimestamps = new ConcurrentHashMap<>();

    public MeldingerSok() {
        queryParser.setDefaultOperator(QueryParser.Operator.AND);
        queryParser.setAllowLeadingWildcard(true);
    }

    public void indekser(String fnr, List<Melding> meldinger) {
        String navIdent = getSubjectHandler().getUid();

        String key = key(fnr, navIdent);
        meldingerCache.put(key, meldinger);
        directories.put(key, indekser(meldinger));
        indexingTimestamps.put(key, now());
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

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new NullFragmenter());

            Map<String, MeldingSokResultat> resultat = hentResultat(searcher, ANALYZER, highlighter, hits);
            final List<String> ider = on(resultat).map(TransformerUtils.<String>key()).collect();

            Map<String, List<Melding>> traader = on(meldingerCache.get(key))
                    .filter(where(Melding.ID, containedIn(ider)))
                    .map(highlighting(resultat))
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

    @Scheduled(cron = "1 * * * * *") // Hvert minutt
    public void ryddOppCache() {
        logger.info("Starter opprydning av cache. Har {} directories", directories.size());
        int count = 0;
        for (Map.Entry<String, DateTime> entry : indexingTimestamps.entrySet()) {
            if (now().minusMinutes(TIME_TO_LIVE_MINUTES).isAfter(entry.getValue())) {
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

    private static RAMDirectory indekser(List<Melding> meldinger) {
        try {
            RAMDirectory directory = new RAMDirectory();
            IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_4_10_2, ANALYZER));
            int i = 0;
            for (Melding melding : meldinger) {
                writer.addDocument(lagDokument(melding, i));
                i++;
            }
            writer.commit();
            return directory;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Document lagDokument(Melding melding, int id) {
        Document document = new Document();
        document.add(new StoredField(ID, id));
        document.add(new StoredField(BEHANDLINGS_ID, melding.id));
        document.add(new TextField(FRITEKST, optional(melding.fritekst).getOrElse(""), YES));
        document.add(new TextField(TEMAGRUPPE, optional(melding.temagruppeNavn).getOrElse(""), YES));
        document.add(new TextField(ARKIVTEMA, optional(melding.journalfortTemanavn).getOrElse(""), YES));
        return document;
    }

    private static String query(String tekst) {
        return "*" + (tekst.isEmpty() ? ":" : trim(tekst)) + "*";
    }

    private static Map<String, MeldingSokResultat> hentResultat(final IndexSearcher searcher, final StandardAnalyzer analyzer, final Highlighter highlighter, ScoreDoc... hits) {
        try {
            Map<String, MeldingSokResultat> resultat = new HashMap<>();
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                String behandlingsId = searcher.doc(hit.doc).get(BEHANDLINGS_ID);
                String fritekst = getHighlightedTekst(FRITEKST, doc, searcher, analyzer, highlighter);
                String temagruppe = getHighlightedTekst(TEMAGRUPPE, doc, searcher, analyzer, highlighter);
                String arkivtema = getHighlightedTekst(ARKIVTEMA, doc, searcher, analyzer, highlighter);
                resultat.put(behandlingsId, new MeldingSokResultat(fritekst, temagruppe, arkivtema));
            }
            return resultat;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getHighlightedTekst(String felt, Document doc, IndexSearcher searcher, StandardAnalyzer analyzer, Highlighter highlighter) {
        String tekst = doc.get(felt);
        try {
            int id = Integer.parseInt(doc.get(ID));
            TokenStream tokenStreamTittel = TokenSources.getAnyTokenStream(searcher.getIndexReader(), id, felt, analyzer);
            TextFragment[] fragments = highlighter.getBestTextFragments(tokenStreamTittel, tekst, false, 1);
            tekst = fragments[0].toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tekst;
    }

    private static Transformer<Melding, Melding> highlighting(final Map<String, MeldingSokResultat> resultat) {
        return new Transformer<Melding, Melding>() {
            @Override
            public Melding transform(Melding melding) {
                MeldingSokResultat meldingSokResultat = resultat.get(melding.id);
                melding.fritekst = meldingSokResultat.fritekst;
                melding.temagruppeNavn = meldingSokResultat.temagruppe;
                melding.journalfortTemanavn = meldingSokResultat.arkivtema;
                return melding;
            }
        };
    }

    private static class MeldingSokResultat {
        public final String fritekst, temagruppe, arkivtema;

        public MeldingSokResultat(String fritekst, String temagruppe, String arkivtema) {
            this.fritekst = fritekst;
            this.temagruppe = temagruppe;
            this.arkivtema = arkivtema;
        }
    }
}
