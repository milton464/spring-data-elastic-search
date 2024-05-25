/**
 * 
 */
package com.milton.elasticsearch.common;

/**
 * @author MILTON
 *
 */
public enum SortOrderEnum {

	ASC("asc"),

	DESC("desc"),

	;

	private final String value;

	SortOrderEnum(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

}
