package lucene.productversion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;


public class App 
{
	private static String versionField = "version";
	
	public static void main( String[] args ) throws IOException
    {
		App app = new App();
		
		System.out.println( "Staring Product Version!" );
    	
    	File indexDir = new File("D:\\claims\\experiments");
		FSDirectory directory = FSDirectory.open(indexDir.toPath());
		
		Analyzer analyzer = new SimpleAnalyzer();
		
		app.index(analyzer, directory);
		
		app.search(analyzer, directory);
		
		System.out.println( "End Product Version!" );
		
    }
	
	private void search(Analyzer analyzer,FSDirectory directory) throws IOException{
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		Query query =  TermRangeQuery.newStringRange(versionField, "1.0", "2.1-RC2", true, true);
		
		TopDocs topDocs  = searcher.search(query, 10);
		
		
		if(topDocs.totalHits <= 0 ){
			System.out.println("No Hits Found");
			return;
		}
		
		for(ScoreDoc doc:topDocs.scoreDocs){
			System.out.println("Doc Id :"+doc.doc+" Version Number :"+searcher.doc(doc.doc).get(versionField));
		}
		
		reader.close();
		
		System.out.println("Searching Completed");
	}
    
	private void index(Analyzer analyzer,FSDirectory directory) throws IOException{
		
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	
    	config.setOpenMode(OpenMode.CREATE);
    	
    	IndexWriter writer = new IndexWriter(directory, config);
    	
		
		for(String version:versions()){
			Document doc = new Document();
		
			doc.add(new StringField(versionField,version,Store.YES));
			
			writer.addDocument(doc);
			
		}
		
		writer.commit();
		writer.close();
		
		System.out.println("Indexing Completed");
	}

	private List<String> versions(){
		
		List<String> versions = new ArrayList<>();
		
		versions.add("1.0");
		versions.add("1.0-RC1");
		versions.add("1.1");
		versions.add("1.2");
		versions.add("2.1-RC1");
		versions.add("2.1-RC2");
		versions.add("2.1-RC3");
		versions.add("3.1-RC1");
		versions.add("3.1");
		
		return versions;
	}
}
