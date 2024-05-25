/**
 * 
 */
package com.milton.elasticsearch.response;

import com.milton.elasticsearch.Action;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MILTON
 */
@Slf4j
@Getter
@Setter
public class CountQueryResponse extends ElasticQueryResponse {

	private Query query;
	private long count;

	/**
	 * 
	 */
	public CountQueryResponse(String indexName) {
		super(Action.COUNT, indexName);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("CountResponse");
	}

}
