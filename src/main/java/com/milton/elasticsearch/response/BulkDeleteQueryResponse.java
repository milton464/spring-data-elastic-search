/**
 * 
 */
package com.milton.elasticsearch.response;

import com.milton.elasticsearch.Action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MILTON
 */
@Slf4j
@Getter
@Setter
public class BulkDeleteQueryResponse extends ElasticQueryResponse {

	/**
	 * 
	 */
	public BulkDeleteQueryResponse(String indexName) {
		super(Action.BULK_DELETE, indexName);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("BulkDeleteResponse");
	}

}
