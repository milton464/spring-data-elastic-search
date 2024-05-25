package com.milton.elasticsearch.common;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MILTON
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
public class DateRangeInfo {

	private Date startDate;

	private Date endDate;

	private List<String> dateRangeFields;

	public DateRangeInfo(Date fromDate, List<String> dateRangeFields) {
		this.startDate = fromDate;
		this.dateRangeFields = dateRangeFields;
	}

	public static void main(String[] args) {
		log.debug("DateRangeInfo");
	}

}
