package lucene.complexsave.beans;

import java.io.Serializable;
import java.util.List;

public class StartBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> categories;
	
	private BeanA beanA;
	
	private String indexable;
	
	private String brand;

	/**
	 * @return the categories
	 */
	public List<String> getCategories() {
		return categories;
	}

	/**
	 * @param categories the categories to set
	 */
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	/**
	 * @return the beanA
	 */
	public BeanA getBeanA() {
		return beanA;
	}

	/**
	 * @param beanA the beanA to set
	 */
	public void setBeanA(BeanA beanA) {
		this.beanA = beanA;
	}

	/**
	 * @return the indexable
	 */
	public String getIndexable() {
		return indexable;
	}

	/**
	 * @param indexable the indexable to set
	 */
	public void setIndexable(String indexable) {
		this.indexable = indexable;
	}
	
	
	
	/**
	 * @return the brand
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * @param brand the brand to set
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		builder.append("indexable = "+indexable);
		
		builder.append(" categories = "+categories);
		
		builder.append(" beanA names = "+beanA.getNames());
		
		return builder.toString();
	}
	
}
