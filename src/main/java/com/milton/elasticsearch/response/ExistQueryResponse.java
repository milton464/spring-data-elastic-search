/**
 * 
 */
package com.milton.elasticsearch.response;


import java.math.BigInteger;

import com.milton.elasticsearch.Action;

import lombok.Getter;
import lombok.Setter;

/**
 * @author MILTON
 */
@Getter
@Setter
public class ExistQueryResponse extends ElasticQueryResponse{
	
	private boolean isExist;
	
	private BigInteger id;
	
	/**
	 * 
	 */
	public ExistQueryResponse(String indexName) {
		super(Action.EXIST, indexName);
	}
	
	public boolean isExist() {
		return isExist;
	}
	
	public void setExist(boolean exist) {
		this.isExist = exist;
	}


}
