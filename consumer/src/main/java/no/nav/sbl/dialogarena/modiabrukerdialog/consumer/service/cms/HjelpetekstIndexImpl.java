package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import no.nav.modig.lang.collections.ReduceUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static org.apache.commons.lang3.StringUtils.*;

public class HjelpetekstIndexImpl implements HjelpetekstIndex {
    public static final String TITTEL = "tittel";
    public static final String TAGS = "tags";
    public static final String TAGS_FILTER = "tags-filter";
    public static final String INNHOLD = "innhold";

    public static final String[] FIELDS = new String[]{TITTEL, TAGS, INNHOLD};

    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private RAMDirectory directory;
    private MultiFieldQueryParser queryParser = new MultiFieldQueryParser(FIELDS, analyzer);

    public HjelpetekstIndexImpl() {
        queryParser.setDefaultOperator(Operator.AND);
        queryParser.setAllowLeadingWildcard(true);
    }

    @Override
    public void indekser(List<Hjelpetekst> hjelpetekster) {
        try {
            directory = new RAMDirectory();
            IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer));
            for (Hjelpetekst hjelpetekst : hjelpetekster) {
                writer.addDocument(lagDokument(hjelpetekst));
            }
            writer.commit();
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

    @Override
    public List<Hjelpetekst> sok(String frisok, List<String> tags) {
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
            TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);

            searcher.search(queryParser.parse(query(frisok, tags)), collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            return lagHjelpetekster(searcher, hits);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Hjelpetekst> sok(String frisok, String ... tags) {
        return sok(frisok, asList(tags));
    }

    private static String query(String frisok, List<String> tags) {
        String frisokQuery = "*" + trim(frisok) + "*";
        String tagsQuery = on(tags).map(new Transformer<String, String>() {
            @Override
            public String transform(String tag) {
                return TAGS_FILTER + ":" + tag;
            }
        }).reduce(ReduceUtils.join(" AND "));
        return frisokQuery + (isBlank(tagsQuery) ? "" : (" AND " + tagsQuery));
    }

    private static Document lagDokument(Hjelpetekst hjelpetekst) {
        Document document = new Document();
        document.add(new TextField(TITTEL, hjelpetekst.tittel, Store.YES));
        document.add(new TextField(INNHOLD, hjelpetekst.innhold, Store.YES));
        document.add(new TextField(TAGS, join(hjelpetekst.tags, " "), Store.YES));
        for (String tag : hjelpetekst.tags) {
            document.add(new StringField(TAGS_FILTER, tag, Store.NO));
        }
        return document;
    }

    private static List<Hjelpetekst> lagHjelpetekster(final IndexSearcher searcher, ScoreDoc... hits) {
        return on(hits).map(new Transformer<ScoreDoc, Hjelpetekst>() {
            @Override
            public Hjelpetekst transform(ScoreDoc hit) {
                try {
                    Document doc = searcher.doc(hit.doc);
                    return new Hjelpetekst(doc.get(TITTEL), doc.get(INNHOLD), StringUtils.split(doc.get(TAGS), " "));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).collect();
    }
}
