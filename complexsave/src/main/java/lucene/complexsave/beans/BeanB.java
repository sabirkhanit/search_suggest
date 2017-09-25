package lucene.complexsave.beans;

import java.io.Serializable;
import java.util.List;

public class BeanB implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> cities;
	
	private BeanC beanC;

	/**
	 * @return the cities
	 */
	public List<String> getCities() {
		return cities;
	}

	/**
	 * @param cities the cities to set
	 */
	public void setCities(List<String> cities) {
		this.cities = cities;
	}

	/**
	 * @return the beanC
	 */
	public BeanC getBeanC() {
		return beanC;
	}

	/**
	 * @param beanC the beanC to set
	 */
	public void setBeanC(BeanC beanC) {
		this.beanC = beanC;
	}
	
	
	
}
