package facet.facetedsearch.brands;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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




import facet.facetedsearch.beans.Product;



public class BrandFacet {
	
	private static String dellProductName = "DellLaptop";
	private static String dellBrand = "Dell";
	
	private static String acerProductName = "AcerLaptop";
	private static String acerBrand = "Acer";
	
	private static String hpProductName = "HPLaptop";
	private static String hpBrand = "HP";
	
	private static String appleProductName = "AppleLaptop";
	private static String appleBrand = "Apple";
	
	private static String indexField_productName= "ProductName";
	private static String indexField_brand= "Brand";
	private static String facetField_brand= "Brand";

	public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {
		
		BrandFacet app = new BrandFacet();
        
        List<Product> products = app.getProducts();
        
        File indexDir = new File("D:\\claims\\experiments");
        
        File taxoLoc = new File("D:\\claims\\taxonomy");
        
        FSDirectory directory = FSDirectory.open(indexDir.toPath());
        FSDirectory taxoDir = FSDirectory.open(taxoLoc.toPath());
        
        Analyzer analyzer = new SimpleAnalyzer();
        
        FacetsConfig config = new FacetsConfig();
        config.setIndexFieldName(facetField_brand, indexField_brand);
        
        
        
        app.index(directory, taxoDir,analyzer,products,config);
        
        
        System.out.println("Starting Searching");
        
        app.search(directory, analyzer,taxoDir,config);
        
        System.out.println("Searching Completed");
        
        

	}
	
private void index(FSDirectory directory,FSDirectory taxoDir,Analyzer analyzer,List<Product> products,FacetsConfig facetConfig) throws IOException{
    	
    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	
    	config.setOpenMode(OpenMode.CREATE);
    	
    	IndexWriter writer = new IndexWriter(directory, config);
    	
    	TaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);
    	
    	for(Product product: products){
    		Document doc = new Document();
    		doc.add(new TextField(indexField_productName, product.getProductName(), Field.Store.NO));
    		doc.add(new TextField(indexField_brand, product.getBrand(), Field.Store.NO));
    		doc.add(new FacetField(facetField_brand, product.getBrand()));
    		writer.addDocument(facetConfig.build(taxoWriter, doc));
    	}
    	
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


	QueryParser parser = new QueryParser(indexField_brand, analyzer);
	parser.setSplitOnWhitespace(false);
	
	String queryStr = "dell";
	
	Query query = parser.parse(queryStr);
	
	System.out.println("Query is : "+query.toString());

	/*
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
	*/
	
	System.out.println("Starting Facet Collector Search");
	
	TopDocs facetTopDocs = FacetsCollector.search(searcher, query, 10, fc);
	
System.out.println("Hit Count "+facetTopDocs.totalHits);
	
	ScoreDoc[] hits = facetTopDocs.scoreDocs;
	
	if (null == hits || hits.length <= 0) {
		return ;
	 }
	
	
	for (ScoreDoc hit : hits) {
		
		Document doc = searcher.doc(hit.doc);
		
		System.out.println("Doc Id "+hit.doc);
		
		/*
		StartBean startBean = getStartBean(doc);
		
		System.out.println("Start Bean Indexable Field = "+startBean.getIndexable());
		System.out.println("Start Bean toString = "+startBean);
		*/
		
	}
	
	List<FacetResult> results = new ArrayList<>();

	Facets field1 = new FastTaxonomyFacetCounts(indexField_brand, taxoReader, facetConfig, fc);
    results.add(field1.getTopChildren(10, facetField_brand));
	
    System.out.println(results.get(0));
    
    
    
    fc = new FacetsCollector();
    queryStr = "apple";
    query = parser.parse(queryStr);
	System.out.println("Query is : "+query.toString());
	
	
	System.out.println("Starting Facet Collector Search");
	
	FacetsCollector.search(searcher, query, 10, fc);
	
	results.clear(); 

	field1 = new FastTaxonomyFacetCounts(indexField_brand, taxoReader, facetConfig, fc);
    results.add(field1.getTopChildren(10, facetField_brand));
	
    System.out.println(results.get(0));

	reader.close();
	taxoReader.close();
}


	
	private List<Product> getProducts(){
		
		List<Product> products = new ArrayList<>();
		String[] suffixes = {"AAA","BBB","CCC","DDD"};
		
		for(int i= 0;i<4;i++){
			
			Product product = new Product();
			
			product.setProductName(dellProductName+suffixes[i]);
			product.setBrand(dellBrand);
			
			products.add(product);
			
		}
		
		for(int i= 0;i<4;i++){
			
			Product product = new Product();
			
			product.setProductName(acerProductName+suffixes[i]);
			product.setBrand(acerBrand);
			
			products.add(product);
			
		}
		
		for(int i= 0;i<4;i++){
			
			Product product = new Product();
			
			product.setProductName(hpProductName+suffixes[i]);
			product.setBrand(hpBrand);
			
			products.add(product);
			
		}
		
		for(int i= 0;i<4;i++){
			
			Product product = new Product();
			
			product.setProductName(appleProductName+suffixes[i]);
			product.setBrand(appleBrand);
			
			products.add(product);
			
		}
		
		
		return products;
	}

}
