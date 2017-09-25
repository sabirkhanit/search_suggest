package lucene.complexsave.beans;

import java.io.Serializable;
import java.util.List;

public class BeanA implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> names;
	
	private BeanB beanB;

	/**
	 * @return the names
	 */
	public List<String> getNames() {
		return names;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(List<String> names) {
		this.names = names;
	}

	/**
	 * @return the beanB
	 */
	public BeanB getBeanB() {
		return beanB;
	}

	/**
	 * @param beanB the beanB to set
	 */
	public void setBeanB(BeanB beanB) {
		this.beanB = beanB;
	}
	
	
}
