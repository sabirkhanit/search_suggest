package facet.facetedsearch.price;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.range.LongRange;
import org.apache.lucene.facet.range.LongRangeFacetCounts;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class PriceFacets {
	
	private static Long indexRange = 10L;
	
	private static Long searchRange = 50L;
	
	private static String searchField = "name";
	
	private static String searchFieldValue = "sabir";
	
	private static String facetField = "price";

	public static void main(String[] args) throws IOException, ParseException {
		
		PriceFacets app = new PriceFacets();
		
		File indexDir = new File("D:\\claims\\experiments");
		FSDirectory directory = FSDirectory.open(indexDir.toPath());
		
		Analyzer analyzer = new SimpleAnalyzer();
		
		//Index
		app.index(analyzer, directory);
		
		//search - gets body & facet both
		app.searchForFacets(analyzer, directory);
		
		//search with facets
		app.searchWithProvidedFacet(directory);

	}
	
	private void searchForFacets(Analyzer analyzer,FSDirectory directory) throws IOException, ParseException{
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		
		QueryParser parser = new QueryParser(searchField, analyzer);
		parser.setSplitOnWhitespace(false);
		
		//free text
		String queryStr = searchFieldValue;
		
		Query query = parser.parse(queryStr);
		
		System.out.println("Query is : "+query.toString());

		
		
		FacetsCollector fc = new FacetsCollector();
		TopDocs topDocs = FacetsCollector.search(searcher, query, 10, fc);
		 
		
		LongRangeFacetCounts facets = new LongRangeFacetCounts(facetField, fc, ranges());
		
		FacetResult result = facets.getTopChildren(0, facetField);
		for (int i = 0; i < result.childCount; i++) {
		   LabelAndValue lv = result.labelValues[i];
		   System.out.println(String.format("%s (%s)", lv.label, lv.value));
		}
		
		
		//topDocs is body & result is filter 
		
		reader.close();
		
		System.out.println("Searching Completed");
		
	}
	
	private void searchWithProvidedFacet(FSDirectory directory) throws IOException{
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
		
		//free text 
		String queryStr = searchFieldValue;
		
		finalQuery.add(new TermQuery(new Term(searchField,queryStr)), BooleanClause.Occur.SHOULD);
		finalQuery.add(NumericDocValuesField.newRangeQuery(facetField, 150L, Long.MAX_VALUE), BooleanClause.Occur.MUST);
		
		TopDocs topDocs = searcher.search(finalQuery.build(), 10);
		
		if(topDocs.totalHits <= 0 ){
			System.out.println("No Hits Found");
			return;
		}
		
		for(ScoreDoc doc:topDocs.scoreDocs){
			System.out.println("Doc Id :"+doc.doc);
		}
		
		
		reader.close();
		
		System.out.println("Range Facet Searching Completed");
		
	}

	private void index(Analyzer analyzer,FSDirectory directory) throws IOException{
		
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	
    	config.setOpenMode(OpenMode.CREATE);
    	
    	IndexWriter writer = new IndexWriter(directory, config);
    	
		
		for(Long value:prices()){
			Document doc = new Document();
			
			//Query field
			doc.add(new TextField(searchField, searchFieldValue, Field.Store.NO));

			//this will simply be indexed - not stored
			doc.add(new NumericDocValuesField(facetField, value));
			//stored field with same name
			doc.add(new StoredField(facetField, value));
			
			writer.addDocument(doc);
			
		}
		
		writer.commit();
		writer.close();
		
		System.out.println("Indexing Completed");
	}
	
	private LongRange[] ranges(){
		
	   long startRange = 0;
	   long endRange = searchRange;
		
		LongRange[] ranges = new LongRange[4];
		
		ranges[0] = new LongRange("0-50", startRange, true, endRange, false);
		
		startRange+=searchRange;
		endRange+=searchRange;
		ranges[1] = new LongRange("50-100", startRange, true, endRange, false);
		
		startRange+=searchRange;
		endRange+=searchRange;
		ranges[2] = new LongRange("100-150", startRange, true, endRange, false);
		
		startRange+=searchRange;
		endRange+=searchRange;
		ranges[3] = new LongRange(">150", startRange, true, Long.MAX_VALUE, false);
		
		return ranges;
	}
	
	private List<Long> prices(){
		
		List<Long> prices = new ArrayList<>();
		
		Long value = 0L;
		
		while(value <200L){
			prices.add(value);
			value+=indexRange;
		}
		return prices;
		
	}
}
