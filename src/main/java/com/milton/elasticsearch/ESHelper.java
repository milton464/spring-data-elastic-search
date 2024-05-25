package com.milton.elasticsearch;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.milton.elasticsearch.common.AggregationInfo;
import com.milton.elasticsearch.common.PaginationInfo;
import com.milton.elasticsearch.response.BulkDeleteQueryResponse;
import com.milton.elasticsearch.response.BulkInsertQueryResponse;
import com.milton.elasticsearch.response.BulkUpdateResponse;
import com.milton.elasticsearch.response.CountQueryResponse;
import com.milton.elasticsearch.response.CreateIndexQueryResponse;
import com.milton.elasticsearch.response.DeleteQueryResponse;
import com.milton.elasticsearch.response.ESSearchResponse;
import com.milton.elasticsearch.response.ExistIndexQueryResponse;
import com.milton.elasticsearch.response.ExistQueryResponse;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.transport.endpoints.BooleanResponse;

/**
 * @author MILTON
 */
public interface ESHelper {

	BooleanResponse ping();
	
	ExistIndexQueryResponse existIndex(String indexName);
	
	CreateIndexQueryResponse createIndex(String indexName);
	
	ExistQueryResponse exist(String indexName, BigInteger id);
	
	CountQueryResponse count(String indexName, Query query);
	
	DeleteQueryResponse delete(String indexName, Query query);
	
	BulkInsertQueryResponse bulkInsert(List<ESAddRequest> requests);
	
	BulkUpdateResponse bulkUpsert(List<ESUpdateRequest> requests);
	
	BulkDeleteQueryResponse bulkDelete(List<ESDeleteRequest> requests);
	
	<T> ESSearchResponse<T> search(String indexName, Query query, Class<T> clazz);

	<T> ESSearchResponse<T> search(String indexName, Query query, SortOptions sorts, Class<T> clazz);

	<T> ESSearchResponse<T> search(String indexName, Query query, SortOptions sorts, PaginationInfo paginationInfo, Class<T> clazz);
	
	<T> ESSearchResponse<T> searchWithAggregation(String indexName, Query query, SortOptions sorts, Map<String, Aggregation> aggregations, AggregationInfo aggregationInfo, Class<T> clazz);
	
}
