package lucene.complexsave;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import lucene.complexsave.beans.BeanA;
import lucene.complexsave.beans.BeanB;
import lucene.complexsave.beans.BeanC;
import lucene.complexsave.beans.StartBean;

/**
 * This demo class demonstrates saving and retrieval of a complex object to lucene index 
 * as well as retrival in one go using serialization / deserialization 
 *
 */
public class App 
{
	
	private static String indexField1= "content";
	private static String indexField_brand= "Brand";
	private static String facetField1= "facetField_1";
	
	
    public static void main( String[] args ) throws IOException, ClassNotFoundException, ParseException
    {
    	
    	App app = new App();
        
        StartBean startBean = app.buildHierarcy();
        
        File indexDir = new File("D:\\claims\\experiments");
        
        File taxoLoc = new File("D:\\claims\\taxonomy");
        
        FSDirectory directory = FSDirectory.open(indexDir.toPath());
        FSDirectory taxoDir = FSDirectory.open(taxoLoc.toPath());
        
        Analyzer analyzer = new SimpleAnalyzer();
        
        
        
        FacetsConfig config = new FacetsConfig();
        config.setIndexFieldName(facetField1, indexField1);
        
        app.index(directory, taxoDir,analyzer,startBean,config);
        
        System.out.println("Starting Searching");
        
        app.search(directory, analyzer,taxoDir,config);
        
        System.out.println("Searching Completed");
        
        
    }
    
    private StartBean buildHierarcy(){
    	
    	List<StartBean> list = new ArrayList<>();
    	
    	BeanC beanC = new BeanC();
    	beanC.setCategory("Kids");
    	
		BeanB beanB = new BeanB();
    	List<String> cities = new ArrayList<>();
    	cities.add("Noida");
    	cities.add("Gurgaon");
    	beanB.setCities(cities);
    	beanB.setBeanC(beanC);
    	
    	
    	BeanA beanA = new BeanA();
    	List<String> names = new ArrayList<>();
    	names.add("Michael");
    	names.add("John");
    	beanA.setNames(names);
    	beanA.setBeanB(beanB);
    	
    	StartBean startBean = new  StartBean();
    	
    	List<String> categories = new ArrayList<>();
    	categories.add("Male");
    	categories.add("Female");
    	
    	startBean.setIndexable("Sabir Khan");
    	startBean.setCategories(categories);
    	startBean.setBeanA(beanA);
    	
    	return startBean;
    	
    	
    }
    
    private void index(FSDirectory directory,FSDirectory taxoDir,Analyzer analyzer,StartBean startBean,FacetsConfig facetConfig) throws IOException{
    	
    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	
    	config.setOpenMode(OpenMode.CREATE);
    	
    	IndexWriter writer = new IndexWriter(directory, config);
    	
    	TaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);
    	
    	Document doc = new Document();
    	doc.add(new TextField(indexField1, startBean.getIndexable(), Field.Store.NO));
    	
    	doc.add(new FacetField(facetField1, startBean.getIndexable()));
    	
    	ByteArrayOutputStream serData=new ByteArrayOutputStream(); 
    	ObjectOutputStream out=new ObjectOutputStream(serData);
    	
    	try { 
            out.writeObject(startBean); 
    	} finally  { 
            out.close(); 
            serData.close(); 
    	} 
    	
    	FieldType filedType = new FieldType();
    	filedType.setStored(true);
    	filedType.setTokenized(false);
    	
    	doc.add(new Field("storedcontent",serData.toByteArray(),filedType));
    	
    	writer.addDocument(facetConfig.build(taxoWriter, doc));
    	
    	writer.commit();
    	taxoWriter.commit();
    	
    	writer.close();
    	taxoWriter.close();
    	
    	System.out.println("Indexing Completed");
    }
    
    private void search(FSDirectory directory,Analyzer analyzer,FSDirectory taxoDir,FacetsConfig facetConfig) throws IOException, ParseException, ClassNotFoundException{
    	IndexReader reader = DirectoryReader.open(directory);
    	IndexSearcher searcher = new IndexSearcher(reader);
    	
    	TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);
    	
    	FacetsCollector fc = new FacetsCollector();


    	QueryParser parser = new QueryParser(indexField1, analyzer);
    	parser.setSplitOnWhitespace(false);
    	
    	String queryStr = "sabir";
    	
    	Query query = parser.parse(queryStr);
    	
    	System.out.println("Query is : "+query.toString());

    	TopDocs topDocs = searcher.search(query, 1);
    	
    	System.out.println("Hit Count "+topDocs.totalHits);
    	
    	ScoreDoc[] hits = topDocs.scoreDocs;
    	
    	if (null == hits || hits.length <= 0) {
    		return ;
    	 }
    	
    	
    	for (ScoreDoc hit : hits) {
    		
    		Document doc = searcher.doc(hit.doc);
    		
    		StartBean startBean = getStartBean(doc);
    		
    		System.out.println("Start Bean Indexable Field = "+startBean.getIndexable());
    		System.out.println("Start Bean toString = "+startBean);
    		
    	}
    	
    	System.out.println("Starting Facet Collector Search");
    	
    	FacetsCollector.search(searcher, query, 10, fc);
    	
    	List<FacetResult> results = new ArrayList<>();

    	Facets field1 = new FastTaxonomyFacetCounts(indexField1, taxoReader, facetConfig, fc);
        results.add(field1.getTopChildren(10, facetField1));
    	
        System.out.println(results.get(0));
        
    	reader.close();
    	taxoReader.close();
    }
    
    private StartBean getStartBean(Document document) throws ClassNotFoundException, IOException{
    	
    	StartBean startBean = null;
    	
    	BytesRef binaryValue = document.getBinaryValue("storedcontent");
    	
    	if(null != binaryValue){
    		System.out.println("binaryValue is non null ");
    		
    		byte[] bytes = binaryValue.bytes;
    		
    		ObjectInputStream in=new ObjectInputStream(new 
    				ByteArrayInputStream(bytes)); 
    				        try { 
    				        	startBean = (StartBean) in.readObject(); 
    				        } finally  { 
    				                in.close(); 
    				        } 
    	}
    	
    	return startBean;
    	
    }

}
