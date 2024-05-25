/**
 * 
 */
package com.milton.elasticsearch;

import java.math.BigInteger;

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
public class ESUpdateRequest {

	private String indexName;

	private String indexPK;

	private Object data;

	private BigInteger priority;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("ESUpdateRequest");
	}

}
