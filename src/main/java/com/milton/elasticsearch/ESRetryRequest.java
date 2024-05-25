package com.milton.elasticsearch;

import com.milton.elasticsearch.common.ESConfig;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest;
import co.elastic.clients.elasticsearch.core.UpdateByQueryResponse;
import co.elastic.clients.json.JsonData;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MILTON
 */
@Slf4j
public class ESRetryRequest {

	private static final String ERROR = "ERROR:{}";
	
	public ESRetryRequest() {
		log.debug("ESRetryRequest");
	}

	public IndexResponse getInsertResponse(ElasticsearchClient client, IndexRequest<JsonData> insertRequest) {
		IndexResponse response = null;
		for (int i = 1; i <= ESConfig.ELASTIC_MAX_RETRY_COUNT; i++) {
			try {
				response = client.index(insertRequest);
				return response;
			} catch (Exception e) {
				if (i == ESConfig.ELASTIC_MAX_RETRY_COUNT) {
					log.error(ERROR, e);
				}
			}
		}
		return response;
	}

	public DeleteResponse getDeleteResponse(ElasticsearchClient client, DeleteRequest deleteRequest) {
		DeleteResponse response = null;
		for (int i = 1; i <= ESConfig.ELASTIC_MAX_RETRY_COUNT; i++) {
			try {
				response = client.delete(deleteRequest);
				return response;
			} catch (Exception e) {
				if (i == ESConfig.ELASTIC_MAX_RETRY_COUNT) {
					log.error(ERROR, e);
				}
			}
		}
		return response;
	}

	public SearchResponse<Object> getSearchResponse(ElasticsearchClient client, SearchRequest searchRequest) {
		for (int i = 1; i <= ESConfig.ELASTIC_MAX_RETRY_COUNT; i++) {
			try {
				return client.search(searchRequest, Object.class);
			} catch (Exception e) {
				if (i == ESConfig.ELASTIC_MAX_RETRY_COUNT) {
					log.error(ERROR, e);
				}
			}
		}
		return null;
	}
	
	public <T> SearchResponse<T> search(ElasticsearchClient client, SearchRequest searchRequest, Class<T> clazz) {
		for (int i = 1; i <= ESConfig.ELASTIC_MAX_RETRY_COUNT; i++) {
			try {
				return client.search(searchRequest, clazz);
			} catch (Exception e) {
				if (i == ESConfig.ELASTIC_MAX_RETRY_COUNT) {
					log.error(ERROR, e);
				}
			}
		}
		return null;
	}

	public CountResponse getCountResponse(ElasticsearchClient client, CountRequest countRequest) {
		for (int i = 1; i <= ESConfig.ELASTIC_MAX_RETRY_COUNT; i++) {
			try {
				return client.count(countRequest);
			} catch (Exception e) {
				if (i == ESConfig.ELASTIC_MAX_RETRY_COUNT) {
					log.error(ERROR, e);
				}
			}
		}
		return null;
	}

	public UpdateByQueryResponse getUpdateByQueryResponse(ElasticsearchClient client, UpdateByQueryRequest updateRequest) {
		UpdateByQueryResponse response = null;
		for (int i = 1; i <= ESConfig.ELASTIC_MAX_RETRY_COUNT; i++) {
			try {
				response = client.updateByQuery(updateRequest);
				return response;
			} catch (Exception e) {
				if (i == ESConfig.ELASTIC_MAX_RETRY_COUNT) {
					log.error(ERROR, e);
				}
			}
		}
		return response;
	}

	public  DeleteByQueryResponse getDeleteByQueryResponse(ElasticsearchClient client, DeleteByQueryRequest deleteRequest) {
		DeleteByQueryResponse response = null;
		for (int i = 1; i <= ESConfig.ELASTIC_MAX_RETRY_COUNT; i++) {
			try {
				response = client.deleteByQuery(deleteRequest);
				return response;
			} catch (Exception e) {
				if (i == ESConfig.ELASTIC_MAX_RETRY_COUNT) {
					log.error(ERROR, e);
				}
			}
		}
		return response;
	}

}
