/**
 * 
 */
package com.milton.elasticsearch.response;

import java.util.List;
import java.util.Set;

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
public class BulkUpdateResponse extends ElasticQueryResponse {

	private List<String> dataList;
	private Set<String> indexIdList;
	private boolean partialSuccess;

	/**
	 * 
	 */
	public BulkUpdateResponse(String indexName) {
		super(Action.BULK_UPDATE, indexName);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("BulkUpdateResponse");
	}

}
