/**
 * 
 */
package com.milton.elasticsearch.response;

import com.milton.elasticsearch.Action;

/**
 * @author MILTON
 */
public class ExistIndexQueryResponse extends ElasticQueryResponse {

	private boolean exist;

	/**
	 * 
	 */
	public ExistIndexQueryResponse(String indexName) {
		super(Action.EXIST_INDEX, indexName);
	}

	public boolean indexExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

}
