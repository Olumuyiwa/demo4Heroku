package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductModelValue {

	@JsonProperty("refresh_date_time")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<StringValueBody> refreshDateTime;

	@JsonProperty("commoditycode_gb")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<StringValueBody> commodityCode;

	@JsonProperty("ean13")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<StringValueBody> ean13;

	public ProductModelValue(List<StringValueBody> refreshDateTime) {
		this.refreshDateTime = refreshDateTime;
	}

}
