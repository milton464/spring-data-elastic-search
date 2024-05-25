package com.milton.elasticsearch.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.milton.elasticsearch.Action;

import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MILTON
 */
@Slf4j
@ToString
public class ESSearchResponse<T> extends ElasticQueryResponse {

	@Getter
	@Setter
	private Query query;

	@Setter
	private List<T> datalist;

	@Getter
	@Setter
	private long total;

	@Getter
	@Setter
	private int currentPage;

	@Setter
	private int totalPages;

	@Setter
	private int pageSize;

	@Setter
	private int nextPage;

	@Setter
	@Getter
	private int lastPage;
	
	@Getter
	@Setter
	private List<StringTermsBucket> aggregationDataList;

	/**
	 * 
	 */
	public ESSearchResponse(String indexName) {
		super(Action.SEARCH, indexName);
		datalist = new ArrayList<>();
	}

	public List<T> getDatalist() {
		return datalist == null ? Collections.emptyList() : datalist;
	}

	public int getNextPage() {
		log.info("totalPages :" + getTotalPages());
		if (getTotalPages() <= 0) {
			nextPage = 0;
		} else if (getTotalPages() <= currentPage) {
			nextPage = getTotalPages();
		} else {
			nextPage = currentPage + 1;
		}
		log.info("nextPage :" + nextPage);
		return nextPage;
	}

	public int getPreviousPage() {
		int previousPage = 0;

		if (getTotalPages() <= 0) {
			previousPage = 0;
		} else if (getTotalPages() < currentPage) {
			previousPage = getTotalPages();
		} else {
			previousPage = currentPage - 1;
		}
		log.debug("previousPage :" + previousPage);
		return previousPage;
	}

	public int getTotalPages() {
		return (int) Math.ceil((float) total / pageSize);
	}

	public int getPageSize() {
		return pageSize;
	}

}
