package com.milton.elasticsearch.common;

import java.math.BigInteger;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MILTON
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class INQueryInfo {

	private String field;

	private List<BigInteger> searchValues;

	public static void main(String[] args) {
		log.debug("TermsQueryInfo");
	}
}
