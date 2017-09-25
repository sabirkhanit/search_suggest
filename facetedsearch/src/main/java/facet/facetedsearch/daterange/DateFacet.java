package facet.facetedsearch.daterange;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
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
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
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

public class DateFacet {
	
	private static final String facetField = "BILL_DT";
	
	private static String searchField = "name";
	
	private static String searchFieldValue = "sabir";
	
	//Lets assume a gap of 3 days
	private static Long searchRange = 3*( Date.valueOf("2017-09-10").getTime() - Date.valueOf("2017-09-09").getTime());
	
	Date minimumDate = Date.valueOf("2017-09-13");

	public static void main(String[] args) throws IOException, ParseException {
		
		DateFacet app = new DateFacet();
		
		
		File indexDir = new File("D:\\claims\\experiments");
		FSDirectory directory = FSDirectory.open(indexDir.toPath());
		
		Analyzer analyzer = new SimpleAnalyzer();
		
		//Index
		app.index(analyzer, directory);
				
		//search
		app.searchForFacets(analyzer, directory);
		
		//search with provided facets
		app.searchWithProvidedFacet(directory);

	}
	
	private void searchForFacets(Analyzer analyzer,FSDirectory directory) throws IOException, ParseException{
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		
		QueryParser parser = new QueryParser(searchField, analyzer);
		parser.setSplitOnWhitespace(false);
		
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
		
		
		//I guess , we need to simply append facet field clauses to free text search query
		// run a normal searcher query - replace body response only - not original facet response 
		// response consists of two parts - body & facets
		// two end points - one for facet & one for body or a composed object in response 
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
		
		//free text 
		String queryStr = searchFieldValue;
		
		finalQuery.add(new TermQuery(new Term(searchField,queryStr)), BooleanClause.Occur.SHOULD);
		finalQuery.add(NumericDocValuesField.newRangeQuery(facetField, Date.valueOf("2017-09-21").getTime(), Long.MAX_VALUE), BooleanClause.Occur.MUST);
		
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
	
	private LongRange[] ranges(){
		
		   	long startRange = minimumDate.getTime();
		   	long endRange = startRange+searchRange;
			
			LongRange[] ranges = new LongRange[4];
			
			ranges[0] = new LongRange("1st", startRange, true, endRange, false);
			
			startRange+=searchRange;
			endRange+=searchRange;
			ranges[1] = new LongRange("2nd", startRange, true, endRange, false);
			
			startRange+=searchRange;
			endRange+=searchRange;
			ranges[2] = new LongRange("3rd", startRange, true, endRange, false);
			
			startRange+=searchRange;
			endRange+=searchRange;
			ranges[3] = new LongRange(">3rd", startRange, true, Long.MAX_VALUE, false);
			
			return ranges;
		}

	
	private void index(Analyzer analyzer,FSDirectory directory) throws IOException{
		
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	
    	config.setOpenMode(OpenMode.CREATE);
    	
    	IndexWriter writer = new IndexWriter(directory, config);
    	
		
		for(Date date:dates()){
			Document doc = new Document();
			
			//Query field
			doc.add(new TextField(searchField, "sabir", Field.Store.NO));

			//this will simply be indexed - not stored
			doc.add(new NumericDocValuesField(facetField, date.getTime()));
			//stored field with same name
			doc.add(new StoredField(facetField, date.getTime()));
			
			writer.addDocument(doc);
			
		}
		
		writer.commit();
		writer.close();
		
		System.out.println("Indexing Completed");
	}
	
	private List<Date> dates(){
		
		List<Date> dates = new ArrayList<>();
		
		//1
		Date date = Date.valueOf("2017-09-22");
		dates.add(date);
		
		//2
		date = Date.valueOf("2017-09-21");
		dates.add(date);
		
		//3
		date = Date.valueOf("2017-09-20");
		dates.add(date);
		
		//4
		date = Date.valueOf("2017-09-19");
		dates.add(date);
		
		//5
		date = Date.valueOf("2017-09-18");
		dates.add(date);
		
		//6
		date = Date.valueOf("2017-09-17");
		dates.add(date);
		
		//7
		date = Date.valueOf("2017-09-16");
		dates.add(date);
		
		//8
		date = Date.valueOf("2017-09-15");
		dates.add(date);
		
		//9
		date = Date.valueOf("2017-09-14");
		dates.add(date);
				
		//10
		date = Date.valueOf("2017-09-13");
		dates.add(date);
		
		return dates;
		
	}
	
	
}
