package com.milton.elasticsearch;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.milton.elasticsearch.common.AggregationInfo;
import com.milton.elasticsearch.common.ESConfig;
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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MILTON
 */
@Service
@Slf4j
public class ESHelperImpl implements ESHelper {

	private static final String ERROR = "ERROR:{}";
	
	private ElasticsearchClient client;
	
	public ESHelperImpl(ElasticsearchClient elasticsearchClient) {
		this.client = elasticsearchClient;
	}

	
	@Override
	public BooleanResponse ping() {
		BooleanResponse response = new BooleanResponse(false);
		
		try {
			log.debug("client :"+client); 
			return new BooleanResponse(client.indices().getAlias(a -> a.allowNoIndices(false)).result().size() > 0);
		} catch (Exception e) {
			log.error(ERROR, e);
		}
		return response;
	}
	
	@Override
	public ExistIndexQueryResponse existIndex(String indexName) {
		ExistIndexQueryResponse response = new ExistIndexQueryResponse(indexName);

		try {
			if (client == null) {
				return response;
			}

			ExistsRequest existRequest = ExistsRequest.of(c -> c.index(indexName).local(false).includeDefaults(true));
			BooleanResponse booleanResponse = client.indices().exists(existRequest);

			log.debug("exist :{}", booleanResponse.value());
			response.setSuccess(true);
			response.setExist(booleanResponse.value());
		} catch (IOException e) {
			log.error(ERROR, e);
			response.setErrorMessage(e.getMessage());
		}

		return response;
	}
	
	@Override
	public CreateIndexQueryResponse createIndex(String indexName) {
		CreateIndexQueryResponse response = new CreateIndexQueryResponse(indexName);
		
		try {

			if (client == null) {
				return response;
			}
			CreateIndexRequest indexRequest = CreateIndexRequest.of(c -> c.index(indexName)
					.settings(s -> s.numberOfShards(ESConfig.NUMBER_OF_SHARDS + "").numberOfReplicas(ESConfig.NUMBER_OF_REPLICAS + "")));
			
			CreateIndexResponse indexResponse = client.indices().create(indexRequest);
			response.setSuccess(indexResponse.acknowledged());
		} catch (Exception e) {
			log.error(ERROR);
		}
		
		return response;
	}
	
	
	@Override
	public ExistQueryResponse exist(String indexName, BigInteger id) {

		ExistQueryResponse response = new ExistQueryResponse(indexName);

		try {

			if (client == null) {
				return response;
			}

			Query query = new Query.Builder().match(m -> m.field("id").query(id.toString())).build();
			CountRequest countRequest = CountRequest.of(c -> c.index(indexName).query(query));
	
			ESRetryRequest esRetryRequest = new ESRetryRequest();
			CountResponse countResponse = esRetryRequest.getCountResponse(client, countRequest);
			
			response.setSuccess(countResponse.count() > 0);
			response.setExist(countResponse.count() > 0);

		} catch (Exception e) {
			log.error(ERROR, e);
		}

		return response;
	}	
	
	
	@Override
	public CountQueryResponse count(String indexName, Query query) {
		CountQueryResponse response = new CountQueryResponse(indexName);

		try {
			if (client == null) {
				return response;
			}

			CountRequest countRequest = CountRequest.of(c -> c.index(indexName).query(query));
			ESRetryRequest esRetryRequest = new ESRetryRequest();
			CountResponse countResponse = esRetryRequest.getCountResponse(client, countRequest);
			
			response.setSuccess(countResponse.count() > 0);
			response.setCount(countResponse.count());
		} catch (Exception e) {
			log.error(ERROR, e);
		}

		return response;
	}
	
	@Override
	public BulkInsertQueryResponse bulkInsert(List<ESAddRequest> requests) {
		BulkInsertQueryResponse response = new BulkInsertQueryResponse("");

		if (requests == null || requests.isEmpty()) {
			return response;
		}
		log.info("requests :{}", requests.size());
		
		try {
			List<BulkOperation> listOperations = new ArrayList<>(requests.size());
			
			for (ESAddRequest request : requests) {
				String idValue = request.getIndexPK();
				
				if(StringUtils.isNotBlank(idValue)) {
					String idString = idValue;
					listOperations.add(BulkOperation.of(blk -> blk.create(c -> c.index(request.getIndexName()).id(idString).document(request.getData()))));  
				}else {
					listOperations.add(BulkOperation.of(blk -> blk.create(c -> c.index(request.getIndexName()).document(request.getData()))));
				}
			}

			BulkRequest bulkRequest = BulkRequest.of(c -> c.operations(listOperations).refresh(Refresh.WaitFor)
							.timeout(t -> t.time(ESConfig.ELASTIC_REQUEST_TIMEOUT_MIN)));

			BulkResponse bulkResponse = client.bulk(bulkRequest);

			Set<String> insertedIds = new HashSet<>(listOperations.size());
			for (BulkResponseItem item : bulkResponse.items()) {
				insertedIds.add(item.id());
				log.info("status :{}", item.status());
				if (null != item.error() && null != item.error().causedBy()) {
					log.info("error :{}", item.error().causedBy().reason());
				}
			}

			log.info("insertedIds :{}", insertedIds.size());
			response.setSuccess(!bulkResponse.errors());
			response.setIndexIdList(insertedIds);

		} catch (IOException e) {
			log.error(ERROR, e);
		}
		return response;
	}
	
	@Override
	public BulkUpdateResponse bulkUpsert(List<ESUpdateRequest> requests) {
		
		BulkUpdateResponse response = new BulkUpdateResponse("");
		if (requests == null || requests.isEmpty() || client == null) {
			return response;
		}
		
		log.debug("requests :"+requests.size());
		try {
			
			List<BulkOperation> listOperations = new ArrayList<>(requests.size());
			
			for (ESUpdateRequest request : requests) {
				String idValue = request.getIndexPK();
				log.debug("idValue :"+idValue);	
				if(StringUtils.isNotBlank(idValue)) {
					listOperations.add(BulkOperation.of(blk -> blk.update(c -> c.index(request.getIndexName()).id(idValue)
							.retryOnConflict(ESConfig.RETRY_ON_CONFLICT_COUNT).action(ac -> ac.docAsUpsert(true).doc(request.getData()).upsert(request.getData())))));
				}
				
			}

			BulkRequest bulkRequest = BulkRequest.of(c -> c.operations(listOperations).refresh(Refresh.WaitFor).timeout(t -> t.time(ESConfig.ELASTIC_REQUEST_TIMEOUT_MIN)));

			BulkResponse bulkResponse = client.bulk(bulkRequest);
			Set<String> insertedIds = new HashSet<>(listOperations.size());
			
			for (BulkResponseItem item : bulkResponse.items()) {
				insertedIds.add(item.id());
				log.debug("status :"+ item.status());
				if(null!=item.error() && null != item.error().causedBy()) {
					log.debug("error :"+ item.error().causedBy().reason());
				}
			}

			log.debug("insertedIds :"+insertedIds.size());
			response.setSuccess(!bulkResponse.errors());
			response.setIndexIdList(insertedIds);

		} catch (IOException e) {
			log.error(ERROR, e);
		} 
		
		return response;
	}
	
	@Override
	public BulkDeleteQueryResponse bulkDelete(List<ESDeleteRequest> requests) {
		
		BulkDeleteQueryResponse response = new BulkDeleteQueryResponse("");
		if (null == requests || requests.isEmpty() || client == null) {
			return response;
		}
		
		try {	
			List<BulkOperation> listOperations = new ArrayList<>(requests.size());
			for (ESDeleteRequest request : requests) {
				listOperations.add(BulkOperation.of(blk -> blk.delete(d -> d.index(request.getIndexName()).id(request.getIndexPK())))); 
			}

			BulkRequest bulkRequest = BulkRequest.of(c -> c.operations(listOperations).refresh(Refresh.True)
					.timeout(t -> t.time(ESConfig.ELASTIC_REQUEST_TIMEOUT_MIN)));

			BulkResponse bulkResponse = client.bulk(bulkRequest);
			Set<String> insertedIds = new HashSet<>(listOperations.size());

			for (BulkResponseItem item : bulkResponse.items()) {
				insertedIds.add(item.id());
				log.debug("status :"+ item.status());
				if (null != item.error()) {
					log.info("error :" + item.error().causedBy().reason());
				}
			}

			log.debug("insertedIds :"+insertedIds.size());
			response.setSuccess(!bulkResponse.errors());
		
		} catch (Exception e) {
			log.error(ERROR, e);
			response.setErrorMessage(e.getMessage());
		}
		return response;
	}
	
	@Override
	public DeleteQueryResponse delete(String indexName, Query query) {
		DeleteQueryResponse response = new DeleteQueryResponse(indexName);
		
		try {
			if (client == null) {
				return response;
			}
			
			DeleteByQueryRequest deleteByQueryRequest = DeleteByQueryRequest.of(d-> d.index(indexName).query(query));
			ESRetryRequest esRetryRequest = new ESRetryRequest();
			
			DeleteByQueryResponse deleteByQueryResponse = esRetryRequest.getDeleteByQueryResponse(client, deleteByQueryRequest);
			response.setSuccess(deleteByQueryResponse.deleted() > 0);
			response.setDeletedCount(deleteByQueryResponse.deleted());
			
		} catch (Exception e) {
			log.error(ERROR, e);
		}
		
		return response;
	}
	
	@Override
	public <T> ESSearchResponse<T> search(String indexName, Query query, Class<T> clazz) {
		ESSearchResponse<T> response = new ESSearchResponse<>(indexName);
		
		try {
			if (client == null) {
				return response;
			}
			
			SearchRequest searchRequest = SearchRequest.of(s -> s.index(indexName).query(query).timeout(ESConfig.ELASTIC_REQUEST_TIMEOUT_MIN));
			ESRetryRequest esRetryRequest = new ESRetryRequest();
			SearchResponse<T> searchResponse = esRetryRequest.search(client, searchRequest, clazz);
			
			Optional.ofNullable(searchResponse).ifPresent(s-> s.hits().hits().forEach(hit-> response.getDatalist().add(hit.source())));
			
		} catch (Exception e) {
			log.error(ERROR, e);
		}
		
		return response;
	}
	
	
	@Override
	public <T> ESSearchResponse<T> search(String indexName, Query query, SortOptions sorts, Class<T> clazz) {
		ESSearchResponse<T> response = new ESSearchResponse<>(indexName);

		try {
			if (client == null) {
				return response;
			}
			
			SearchRequest searchRequest = SearchRequest.of(s-> s.index(indexName).query(query).sort(sorts).timeout(ESConfig.ELASTIC_REQUEST_TIMEOUT_MIN));
			ESRetryRequest esRetryRequest = new ESRetryRequest();
			SearchResponse<T> searchResponse = esRetryRequest.search(client, searchRequest, clazz);
			
			Optional.ofNullable(searchResponse).ifPresent(data -> data.hits().hits().forEach(hit -> response.getDatalist().add(hit.source())));
			
		} catch (Exception e) {
			log.error(ERROR, e);
		}
		
		return response;
	}
	
	@Override
	public <T> ESSearchResponse<T> search(String indexName, Query query, SortOptions sorts, PaginationInfo paginationInfo, Class<T> clazz) {
		ESSearchResponse<T> response = new ESSearchResponse<>(indexName);
		
		
		try {
			if(client == null) {
				return response;
			}
			
			if(paginationInfo == null) {
				paginationInfo = PaginationInfo.builder().pageNo(1).pageSize(20).build();
			}
			response.setCurrentPage(paginationInfo.getPageNo());

			int offset =  (paginationInfo.getPageNo() - 1) * paginationInfo.getPageSize();
			int dataSize = paginationInfo.getPageSize();
			
			SearchRequest searchRequest = SearchRequest.of(s-> s.index(indexName).query(query).sort(sorts).from(offset).size(dataSize).timeout(ESConfig.ELASTIC_REQUEST_TIMEOUT_MIN));
			ESRetryRequest esRetryRequest = new ESRetryRequest();
			SearchResponse<T> searchResponse = esRetryRequest.search(client, searchRequest, clazz);
			Optional.ofNullable(searchResponse).ifPresent(data -> data.hits().hits().forEach(hit -> response.getDatalist().add(hit.source())));
			
			CountRequest countRequest = CountRequest.of(c->c.index(indexName).query(query));
			CountResponse countResponse =  esRetryRequest.getCountResponse(client, countRequest);
			response.setTotal(countResponse.count());
			
		} catch (Exception e) {
			log.error(ERROR, e);
		}
		
		return response;
	}
	
	@Override
	public <T> ESSearchResponse<T> searchWithAggregation(String indexName, Query query, SortOptions sort, Map<String, Aggregation> aggMap, AggregationInfo aggregationInfo, Class<T> clazz) {
		ESSearchResponse<T> response = new ESSearchResponse<>(indexName);

		try {

			if (client == null) {
				return response;
			}

			SearchRequest searchRequest = SearchRequest.of(b -> b.index(indexName).sort(sort).query(query)
					.aggregations(aggMap).timeout(ESConfig.ELASTIC_REQUEST_TIMEOUT_MIN));

			ESRetryRequest esRetryRequest = new ESRetryRequest();
			SearchResponse<Object> searchResponse = esRetryRequest.getSearchResponse(client, searchRequest);
			Optional.ofNullable(searchResponse).ifPresent(data -> {
						response.setAggregationDataList(data.aggregations().get(aggregationInfo.getAggregationName()).sterms().buckets().array());
						response.setTotal(searchResponse.hits().hits().size());
			});

		} catch (Exception e) {
			log.error(ERROR, e);
			response.setErrorMessage(e.getMessage());
		}

		return response;
	}
}
