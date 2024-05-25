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
public class BulkUpdateQueryResponse extends ElasticQueryResponse{
	
	/**
	 * 
	 */
	public BulkUpdateQueryResponse(String indexName) {
		super(Action.BULK_UPDATE, indexName);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("BulkUpdateResponse"); 
	}

}
