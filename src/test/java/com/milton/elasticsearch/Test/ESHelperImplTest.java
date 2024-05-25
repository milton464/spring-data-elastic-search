package com.milton.elasticsearch.Test;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import com.milton.elasticsearch.ESAddRequest;
import com.milton.elasticsearch.ESHelper;
import com.milton.elasticsearch.ESHelperImpl;
import com.milton.elasticsearch.ESQueryBuilder;
import com.milton.elasticsearch.common.OrderInfo;
import com.milton.elasticsearch.common.PaginationInfo;
import com.milton.elasticsearch.common.SortOrderEnum;
import com.milton.elasticsearch.config.ElasticsearchConfig;
import com.milton.elasticsearch.response.BulkInsertQueryResponse;
import com.milton.elasticsearch.response.CountQueryResponse;
import com.milton.elasticsearch.response.CreateIndexQueryResponse;
import com.milton.elasticsearch.response.DeleteQueryResponse;
import com.milton.elasticsearch.response.ESSearchResponse;
import com.milton.elasticsearch.response.ExistIndexQueryResponse;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SpringBootTest(classes = {ElasticsearchConfig.class, ESHelperImpl.class})
@SpringBootConfiguration
public class ESHelperImplTest {

	private static String INDEX_NAME = "test_3";
	
	@Autowired
	private ESHelper helper;
	
	
	@ParameterizedTest
	@CsvSource({
		"null, false",
		"pypepro_test_200, false",
		"pypepro_test_3, true",
	})
	void existIndexTest(String indexName, boolean expected) {
		ExistIndexQueryResponse existIndexQueryResponse = helper.existIndex(indexName);
		assertEquals(expected, existIndexQueryResponse.indexExist());
	}
	
	@Test
	void createIndexTest() {
		CreateIndexQueryResponse response = helper.createIndex("pypepro_test_3");
		System.out.println(response.isSuccess());
	}
	
	
	@ParameterizedTest
	@CsvSource({
		"null, null, 0",
		"user_id, 3, 0",
		"user_id, 2, 1",
	})
	void testCount(String fieldName, String fieldValue, int expectedValue) {
		CountQueryResponse countResponse = helper.count(INDEX_NAME, ESQueryBuilder.buildMatchQuery(fieldName, fieldValue));
		System.out.println(countResponse.getCount());
		assertEquals(expectedValue, countResponse.getCount());
	}
	
	@Test
	void bulkInsertTest() {
		ESAddRequest esAddRequest = ESAddRequest.builder().data(prepareContactData()).indexName(INDEX_NAME).indexPK(BigInteger.valueOf(1).toString()).build();
		BulkInsertQueryResponse bulkInsertQueryResponse = helper.bulkInsert(Arrays.asList(esAddRequest));
		assertEquals(bulkInsertQueryResponse.isSuccess(), true);
	}
	
	@Test
	void deleteTest() {
		String fieldName = "user_id";
		String fieldValue = "2";
		Query query = ESQueryBuilder.buildMatchQuery(fieldName, fieldValue);
		DeleteQueryResponse deleteQueryResponse = helper.delete(INDEX_NAME, query);
		System.out.println(deleteQueryResponse.getDeletedCount());
	}
	

	@ParameterizedTest
	@CsvSource({
		"null, null, 0",
		"user_id, 1, 0",
		"email_type, work, 2",
	})
	void searchTest(String fieldName, String fieldValue, int expected){
		OrderInfo orderInfo = OrderInfo.builder().sortOrderFieldName("id").sortOrder(SortOrderEnum.ASC).build();
		SortOptions sort = ESQueryBuilder.buildOrderBy(orderInfo);
//		Query query = ESQueryBuilderv1.buildWildCardQuery("contact_address."+fieldName, fieldValue);
//		Query query = ESQueryBuilderv1.buildMatchPhrasePrefixQuery(fieldName, fieldValue);
		
		String path = "contact_communications";
		Query query = ESQueryBuilder.buildNestedMatchPhrasePrefixQuery(path, fieldName, fieldValue);
 
		
		PaginationInfo paginationInfo = PaginationInfo.builder().pageNo(1).pageSize(1).build();
		
		ESSearchResponse<Contact> dataList = helper.search(INDEX_NAME, query, Contact.class);
		System.out.println("dataList: "+dataList);
		System.out.println("total: "+ dataList.getTotal());
		System.out.println("totalPages: "+dataList.getTotalPages());
		System.out.println("nextPages: "+dataList.getNextPage());
		System.out.println("currentPage: "+dataList.getCurrentPage());
//		assertEquals(expected, dataList.getDatalist().size());
	}
	
	
	private Contact prepareContactData() {
		List<ContactCommunication> contactCommunicationList = new ArrayList<>();
		ContactCommunication contactCommunication = new ContactCommunication();
		ContactAddress contactAddress = new ContactAddress();
		
		contactCommunication.setUid("b8089930-c0ce-11ee-b31a-7c70db13c4c8");
		contactCommunication.setEmail_address("bronson@iwon.com");
		contactCommunication.setEmail_label("PRIMARY");
		contactCommunication.setEmail_type("PERSONAL");
		contactCommunication.setPhone_number("13025400691");
		contactCommunication.setPhone_label("PRIMARY");
		contactCommunication.setPhone_type("PERSONAL");
		contactCommunicationList.add(contactCommunication);
		
		contactCommunication = new ContactCommunication();
		contactCommunication.setUid("800dd7ff-c0cf-11ee-b31a-7c70db13c4c8");
		contactCommunication.setEmail_address("dj@iwon.com");
		contactCommunication.setEmail_label("SECONDARY");
		contactCommunication.setEmail_type("WORK");
		contactCommunication.setPhone_number("13023681785");
		contactCommunication.setPhone_label("SECONDARY");
		contactCommunication.setPhone_type("WORK");
		contactCommunicationList.add(contactCommunication);
		
		contactAddress.setUid("4020d158-c0d0-11ee-b31a-7c70db13c4c8");
		contactAddress.setStreet_address("757 Plumtree Lane");
		contactAddress.setCity("Claymont");
		contactAddress.setState("DE");
		contactAddress.setZip_code("19703");
		contactAddress.setPostal_code("19704");
		contactAddress.setAddress_type("HOME");
		contactAddress.setAddress_label("PRIMARY");
		
		
		return Contact.builder()
				.id(BigInteger.valueOf(1))
				.uid("dcc09c8f-0a8c-4562-b756-231519384620")
				.user_id(BigInteger.valueOf(1))
				.team_id(BigInteger.valueOf(1))
				.agency_id(BigInteger.valueOf(1))
				.contact_source_id(BigInteger.valueOf(105990))
				.first_name("Dj")
				.last_name("Bronson")
				.status("active")
				.birth_date(LocalDate.now())
				.anniversary_date(LocalDate.now())
				.company_name("Platinum Hits Entertainment Inc")
				.created_at(LocalDateTime.now())
				.updated_at(LocalDateTime.now())
				.tags(List.of("PONTIAC", "CARD-CAMPAIGN","UNSUBSCRIBE-TEST"))
				.collaborators(List.of(BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3)))
				.contact_communications(contactCommunicationList)
				.contact_address(contactAddress)
				.build();
	
		
	}
	
	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class Contact {
		
		private String _id;
		private BigInteger id;
	    private String uid;
	    private BigInteger user_id;
	    private BigInteger team_id;
	    private BigInteger agency_id;
	    private BigInteger contact_source_id;
	    private String first_name;
	    private String last_name;
	    private String status;
	    private LocalDate birth_date;
	    private LocalDate anniversary_date;
	    private String company_name;
	    private LocalDateTime created_at;
	    private LocalDateTime updated_at;
		private List<String> tags;
		private List<BigInteger> collaborators;
		private List<ContactCommunication> contact_communications;
		private ContactAddress contact_address;
	}
	
	
	@Getter
	@Setter
	@ToString
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ContactCommunication {
		private String uid;
		private String phone_number;
		private String phone_label;
	    private String phone_type;
		private String email_address;
		private String email_label;
		private String email_type;
	}

	@Getter
	@Setter
	@ToString
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ContactAddress {
		    private String uid;
		    private String street_address;
		    private String city;
		    private String state;
		    private String postal_code;
		    private String zip_code;
		    private String country;
		    private String address_type;
		    private String address_label;
		    private String full_address;
	}
	
	
}
