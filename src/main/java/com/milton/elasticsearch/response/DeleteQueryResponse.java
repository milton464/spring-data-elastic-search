/**
 * 
 */
package com.milton.elasticsearch.response;

import com.milton.elasticsearch.Action;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.Getter;
import lombok.Setter;

/**
 * @author MILTON
 */
@Getter
@Setter
public class DeleteQueryResponse extends ElasticQueryResponse {

	private Query query;
	private long deletedCount;

	/**
	 * 
	 */
	public DeleteQueryResponse(String indexName) {
		super(Action.DELETE, indexName);
	}

}
