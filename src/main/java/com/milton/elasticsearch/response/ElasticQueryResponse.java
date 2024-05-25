/**
 * 
 */
package com.milton.elasticsearch.response;

import com.milton.elasticsearch.Action;

import lombok.Getter;
import lombok.Setter;

/**
 * @author MILTON
 */
@Setter
@Getter
public abstract class ElasticQueryResponse {

	private String indexName;
	private boolean success;
	private Action action;
	private String errorMessage;

	/**
	 * 
	 */
	public ElasticQueryResponse(Action action, String indexName) {
		this.action = action;
		this.indexName = indexName;
	}

	/**
	 * 
	 */
	public ElasticQueryResponse(Action action) {
		this.action = action;
	}

}
