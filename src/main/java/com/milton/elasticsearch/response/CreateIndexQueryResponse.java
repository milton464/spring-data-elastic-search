/**
 * 
 */
package com.milton.elasticsearch.response;

import com.milton.elasticsearch.Action;

import lombok.Setter;

/**
 * @author MILTON
 */
@Setter
public class CreateIndexQueryResponse extends ElasticQueryResponse {

	/**
	 * 
	 */
	public CreateIndexQueryResponse(String indexName) {
		super(Action.CRATE_INDEX, indexName);
	}

}
