/**
 * 
 */
package com.milton.elasticsearch.common;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author MILTON
 *
 */
@Builder
@AllArgsConstructor
@ToString
public class SearchInfo {

	@Getter
	@Setter
	private String searchText;

	@Setter
	private List<String> searchFields;

	public SearchInfo() {
		this.searchFields = new ArrayList<>();
	}

	public List<String> getSearchFields() {
		return null == searchFields ? new ArrayList<>() : searchFields;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
