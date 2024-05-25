/**
 * 
 */
package com.milton.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MILTON
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Data
public class ESAddRequest {
	
	private String indexName;
	
	private String indexPK;
	
	private Object data;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("ESUpdateRequest");
	}

}
