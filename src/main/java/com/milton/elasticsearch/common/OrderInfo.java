/**
 * 
 */
package com.milton.elasticsearch.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author MILTON
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderInfo {

	private SortOrderEnum sortOrder;

	private String sortOrderFieldName;

}
