package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import no.nav.modig.lang.collections.ReduceUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static org.apache.commons.lang3.StringUtils.*;

public class HjelpetekstIndex {
    public static final String ID = "id";
    public static final String INNHOLD = "innhold";
    public static final String TITTEL = "tittel";
    public static final String TAGS = "tags";
    public static final String TAGS_FILTER = "tags-filter";

    public static final String HIGHLIGHTED_BEGIN = "<em>";
    public static final String HIGHLIGHTED_END = "</em>";

    public static final String[] FIELDS = new String[]{TITTEL, TAGS, INNHOLD};

    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private RAMDirectory directory;
    private MultiFieldQueryParser queryParser = new MultiFieldQueryParser(FIELDS, analyzer);

    public HjelpetekstIndex() {
        queryParser.setDefaultOperator(Operator.AND);
        queryParser.setAllowLeadingWildcard(true);
    }

    public void indekser(List<Hjelpetekst> hjelpetekster) {
        try {
            directory = new RAMDirectory();
            IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer));
            int i = 0;
            for (Hjelpetekst hjelpetekst : hjelpetekster) {
                if (hjelpetekst.isValid()) {
                    writer.addDocument(lagDokument(hjelpetekst, i));
                    i++;
                }
            }
            writer.commit();
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

    private static Document lagDokument(Hjelpetekst hjelpetekst, int id) {
        Document document = new Document();
        document.add(new StoredField(ID, id));
        document.add(new TextField(TITTEL, hjelpetekst.tittel, Store.YES));
        document.add(new TextField(INNHOLD, hjelpetekst.getDefaultLocaleInnhold().get(), Store.NO));
        document.add(new TextField(TAGS, join(hjelpetekst.tags, " "), Store.YES));

        for (Map.Entry<String, String> localeHjelpetekst : hjelpetekst.innhold.entrySet()) {
            document.add(new StoredField(INNHOLD + "_" + localeHjelpetekst.getKey(), localeHjelpetekst.getValue()));
        }
        for (String tag : hjelpetekst.tags) {
            document.add(new StringField(TAGS_FILTER, tag, Store.NO));
        }
        return document;
    }

    public List<Hjelpetekst> sok(String frisok, List<String> tags) {
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
            TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);

            Query query = queryParser.parse(query(frisok, tags));
            searcher.search(query, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter(HIGHLIGHTED_BEGIN, HIGHLIGHTED_END);
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new NullFragmenter());

            return lagHjelpetekster(searcher, analyzer, highlighter, hits);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Hjelpetekst> sok(String frisok, String... tags) {
        return sok(frisok, asList(tags));
    }

    private static String query(String frisok, List<String> tags) {
        String frisokQuery;
        if (frisok.isEmpty()) {
            frisokQuery = "*:*";
        } else {
            frisokQuery = "*" + trim(frisok) + "*";
        }
        String tagsQuery = on(tags).map(new Transformer<String, String>() {
            @Override
            public String transform(String tag) {
                return TAGS_FILTER + ":" + tag;
            }
        }).reduce(ReduceUtils.join(" AND "));
        return frisokQuery + (isBlank(tagsQuery) ? "" : (" AND " + tagsQuery));
    }

    private static List<Hjelpetekst> lagHjelpetekster(final IndexSearcher searcher, final StandardAnalyzer analyzer, final Highlighter highlighter, ScoreDoc... hits) {
        return on(hits).map(new Transformer<ScoreDoc, Hjelpetekst>() {
            @Override
            public Hjelpetekst transform(ScoreDoc hit) {
                try {
                    Document doc = searcher.doc(hit.doc);
                    String tittel = getHighlightedTekst(TITTEL, doc, searcher, analyzer, highlighter);
                    HashMap<String, String> innhold = new HashMap<>();
                    for (IndexableField field : doc) {
                        if (field.name().startsWith(INNHOLD + "_")) {
                            innhold.put(field.name().replace(INNHOLD + "_", ""), field.stringValue());
                        }
                    }
                    return new Hjelpetekst(tittel, innhold, split(doc.get(TAGS), " "));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).collect();
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
}
