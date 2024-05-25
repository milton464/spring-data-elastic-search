package com.milton.elasticsearch;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.milton.elasticsearch.common.ESConfig;
import com.milton.elasticsearch.common.OrderInfo;
import com.milton.elasticsearch.common.SortOrderEnum;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.SumAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.json.JsonData;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MILTON
 */
@Slf4j
public class ESQueryBuilder {

	private ESQueryBuilder() {
		log.info("ESQueryBuilderv1");
	}

	
	public static SortOptions buildOrderBy(OrderInfo orderInfo) {
		return Optional.ofNullable(orderInfo).map(order -> getOrderBy(orderInfo.getSortOrderFieldName(),
						order.getSortOrder() == SortOrderEnum.ASC ? SortOrder.Asc : SortOrder.Desc))
			
				.orElseGet(() -> getOrderBy("id", SortOrder.Desc));
	}
	
	public static SortOptions getOrderBy(final String field, SortOrder order) {
		return SortOptions.of(s -> s.field(f -> f.field(field).order(order)));
	}
	
	
	/**
	 * @author MILTON
	 * Builds a query to check the existence of a field in Elasticsearch documents.
	 * Sample note: To check the existence of a field in a nested object, use the format 'nested_object_name.field'.
	 * 
	 * @param field The name of the field to check.
	 * @return The Elasticsearch query.
	 * 
	 */
	public static Query buildFieldExistQuery(final String field) {
		return new Query.Builder().exists(e -> e.field(field)).build();
	}

	/**
	 * @author MILTON
	 * Builds a nested query to check if a field exists.
	 * @param field The field to check for existence.
	 * @return Query for checking field existence.
	 */
	public static Query buildNestedFieldExistQuery(final String path, final String field) {
		String fieldName = new StringBuilder().append(path).append(".").append(field).toString();
		return new Query.Builder().nested(n -> n.path(path).query(q -> q.exists(e -> e.field(fieldName)))).build();		

	}
	
	
	/**
	 * @author MILTON
	 * Builds an Elasticsearch term query for a specific field and value.
	 * Sample note: To check the existence of a field in a nested object, use the format 'nested_object_name.field'.
	 *
	 * @param field The name of the field to query.
	 * @param value The value to match in the specified field.
	 * @return The Elasticsearch term query.
	 */
	public static Query buildTermQuery(final String field, final BigInteger value) {
		return new Query.Builder().term(t -> t.field(field).value(value.longValue())).build();
	}
	
	
	/**
	 * @author MILTON
	 * Builds a nested Elasticsearch term query.
	 *
	 * This method constructs a nested term query to match documents where the specified field
	 * within the nested path has the given exact value.
	 *
	 * @param path   The path to the nested field (e.g., "nested_object_name").
	 * @param field  The name of the field within the nested path.
	 * @param value  The exact value to match in the specified field.
	 * @return       The Elasticsearch nested term query.
	 */
	public static Query buildNestedTermQuery(final String path, final String field, final BigInteger value) {
		String fieldName = new StringBuilder().append(path).append(".").append(field).toString();
		
		return new Query.Builder().nested(n -> n.path(path).query(q -> q.term(t -> t.field(fieldName).value(value.longValue())))).build();
	}
	
	
	
	/**
	 * @author MILTON
	 * Builds an Elasticsearch query for matching terms in a given field.
	 * Sample note: To check the existence of a field in a nested object, use the format 'nested_object_name.field'.
	 * @param field   The name of the field to match terms against.
	 * @param idList  A list of BigInteger values representing the terms to match.
	 * @return        An Elasticsearch query for matching terms in the specified field.
	 */
	public static Query buildTermsQuery(final String field, final List<BigInteger> idList) {
		List<FieldValue> fieldValueList = new ArrayList<>(idList.size());
		idList.forEach(id -> fieldValueList.add(FieldValue.of(id.toString())));

		TermsQuery termsQuery = TermsQuery.of(t -> t.field(field).terms(tr -> tr.value(fieldValueList)));
		return new Query.Builder().bool(b -> b.must(m -> m.terms(termsQuery))).build();
	}

	
	/**
	 * @author MILTON
	 * Builds a Wildcard Query for Elasticsearch to perform wildcard pattern matching.
	 * This method constructs a query that checks if a field contains a specified pattern.
	 * Sample note: To check the existence of a field in a nested object, use the format 'nested_object_name.field'.
	 * @param field The name of the field to apply the wildcard pattern.
	 * @param value The wildcard pattern to match against the field.
	 * @return The Elasticsearch query for wildcard pattern matching.
	 */
	public static Query buildWildCardQuery(final String field, final String value) {
		return new Query.Builder().wildcard(w -> w.field(field).value("*" + value + "*").caseInsensitive(true)).build();
	}
	
	/**
	 * @author MILTON
	 * Builds a nested query with a wildcard filter for Elasticsearch documents.
	 * This method constructs a query to perform a wildcard search within a nested field.
	 * 
	 *
	 * @param path  The path to the nested field.
	 * @param field The name of the field within the nested structure.
	 * @param value The wildcard pattern to match against the field values.
	 * @return The Elasticsearch query for the nested wildcard search.
	 */
	public static Query buildNestedWildCardQuery(final String path, final String field, final String value) {
		final String fieldName = new StringBuilder().append(path).append(".").append(field).toString();
		
		return new Query.Builder().nested(n -> n.path(path).query(q -> q.wildcard(w -> w.field(fieldName).value("*" + value + "*").caseInsensitive(true)))).build();
	}
	
	
	/**
	 * @author MILTON
	 * Builds a match query for a specific field and value in Elasticsearch.
	 * Sample note: To check the existence of a field in a nested object, use the format 'nested_object_name.field'.
	 *
	 * @param field The name of the field to match against.
	 * @param value The value to match within the specified field.
	 * @return The Elasticsearch match query.
	 */
	public static Query buildMatchQuery(final String field, final String value) {
		return new Query.Builder().match(m -> m.field(field).query(value)).build();
	}

		
	/**
	 * @author MILTON
	 * Builds a nested query for a match on a specific field within a nested object.
	 *
	 * @param path  The path to the nested object.
	 * @param field The field within the nested object to match against.
	 * @param value The value to match.
	 * @return The Elasticsearch query for the nested match.
	 */
	public static Query buildNestedMatchQuery(final String path, final String field, final String value) {
		final String fieldName = new StringBuilder().append(path).append(".").append(field).toString();
		
		return new Query.Builder().nested(n -> n.path(path).query(q -> q.match(m -> m.field(fieldName).query(value)))).build();
	}
	
	/**
	 * @author MILTON
	 * Builds an Elasticsearch query for matching a prefix of a phrase in a specific field.
	 * Sample note: To check the existence of a field in a nested object, use the format 'nested_object_name.field'.
	 *
	 * @param field The name of the field to perform the match in.
	 * @param value The prefix of the phrase to match.
	 * @return The Elasticsearch query for matching a phrase prefix.
	 */
	public static Query buildMatchPhrasePrefixQuery(final String field, final String value) {
		return new Query.Builder().matchPhrasePrefix(
						m -> m.field(field).query(value).maxExpansions(ESConfig.ELASTIC_MATCH_PHRASE_MAX_EXPANSIONS)).build();
	}
	
	/**
	 * @author MILTON
	 * Builds a nested Elasticsearch query for matching a prefix of a field's value within a nested object.
	 *
	 * @param path  The path to the nested object.
	 * @param field The field within the nested object to query.
	 * @param value The value to match with a prefix.
	 * @return The Elasticsearch query for the nested match phrase prefix.
	 */
	public static Query buildNestedMatchPhrasePrefixQuery(String path, String field, String value) {
		final String fieldName = new StringBuilder().append(path).append(".").append(field).toString();
		
		return new Query.Builder().nested(n -> n.path(path).query(q -> q.matchPhrasePrefix(
				m -> m.field(fieldName).query(value).maxExpansions(ESConfig.ELASTIC_MATCH_PHRASE_MAX_EXPANSIONS)))).build();

	}
	
	/**
	 * @author MILTON
	 * Builds an Elasticsearch range query for a specified field with inclusive bounds.
	 * Sample note: To check the existence of a field in a nested object, use the format 'nested_object_name.field'.
	 *
	 * @param field     The name of the field to perform the range query on.
	 * @param fromValue The lower bound of the range (inclusive).
	 * @param toValue   The upper bound of the range (inclusive).
	 * @return An Elasticsearch query representing a range query on the specified field.
	 */
	public static Query buildRangeQuery(final String field, final JsonData fromValue, JsonData toValue) {
		return new Query.Builder().range(r -> r.field(field).gte(fromValue).lte(toValue)).build();
	}

	
	/**
	 * @author MILTON
	 * Builds a nested range query for Elasticsearch.
	 * This method constructs a query to find documents where a specific field within a nested object falls within a specified range.
	 *
	 * @param path      The path to the nested object.
	 * @param field     The name of the field within the nested object.
	 * @param fromValue The lower bound of the range (inclusive).
	 * @param toValue   The upper bound of the range (inclusive).
	 * @return The Elasticsearch nested range query.
	 */
	public static Query buildNestedRangeQuery(final String path, final String field, final JsonData fromValue, JsonData toValue) {
		final String fieldName = new StringBuilder().append(path).append(".").append(field).toString();
		
		return new Query.Builder()
				.nested(n -> n.path(path).query(q -> q.range(r -> r.field(fieldName).gte(fromValue).lte(toValue)))).build();
	}
	
	/**
	 * Builds an Elasticsearch query to filter documents where the specified field's value
	 * is greater than or equal to the provided value.
	 *
	 * @param fieldName The name of the field to apply the range query on.
	 * @param fromFieldValue The value to compare against. Documents with a field value greater
	 *                       than or equal to this value will be included.
	 * @return The Elasticsearch query for the greater-than-or-equal-to condition.
	 */
	public static Query buildGreaterThanOrEqualQuery(String fieldName, JsonData fromFieldValue) {
		return new Query.Builder().range(r -> r.field(fieldName).gte(fromFieldValue)).build();
	}
	
	/**
	 * @author MILTON
	 * Builds an Elasticsearch query to match documents where the specified field
	 * is less than or equal to the provided value.
	 *
	 * @param fieldName      The name of the field to apply the less-than-or-equal condition.
	 * @param fromFieldValue The value to compare against. Documents with a field value less than or equal to this
	 *                       will be matched.
	 * @return The Elasticsearch query for less-than-or-equal condition.
	 */
	public static Query buildLessThanOrEqualQuery(String fieldName, JsonData fromFieldValue) {
		return new Query.Builder().range(r -> r.field(fieldName).lte(fromFieldValue)).build();
	}
	
	
	/**
	 * @author MILTON
	 * Builds an Elasticsearch query for documents where the specified field value is greater than a given value.
	 *
	 * @param fieldName      The name of the field to apply the greater-than condition.
	 * @param fromFieldValue The value to compare against.
	 * @return The Elasticsearch query for the greater-than condition.
	 */
	public static Query buildGreaterThanQuery(String fieldName, JsonData fromFieldValue) {
		return new Query.Builder().range(r -> r.field(fieldName).gt(fromFieldValue)).build();
	}
	
	
	/** @author MILTON
	 * Builds an Elasticsearch range query to match documents where the specified field's value
	 * is less than the provided JSON data.
	 *
	 * @param fieldName      The name of the field to perform the range query on.
	 * @param fromFieldValue The JSON data representing the value to compare against (exclusive).
	 * @return The Elasticsearch range query.
	 */
	public static Query buildLessThanQuery(String fieldName, JsonData fromFieldValue) {
		return new Query.Builder().range(r -> r.field(fieldName).lt(fromFieldValue)).build();
	}
	
	
	/**
	 * @author MILTON
	 * Builds a Terms Aggregation query for Elasticsearch.
	 * This aggregation is used to group documents by the values of a specified field.
	 * 
	 * @param fieldName The name of the field based on which the aggregation is performed.
	 * @return TermsAggregation representing the Elasticsearch Terms Aggregation query.
	 */
	public static TermsAggregation getTermAggregateQyuery(String fieldName) {
		return new TermsAggregation.Builder().field(fieldName).build();
	}
	
	/**
	 * @author MILTON
	 * Creates a SumAggregation query for Elasticsearch.
	 *
	 * @param fieldName The name of the field for which to calculate the sum.
	 * @return SumAggregation query for the specified field.
	 */
	public static SumAggregation getSumAggregateQyuery(String fieldName) {
		return new SumAggregation.Builder().field(fieldName).build();
	}
	
}
