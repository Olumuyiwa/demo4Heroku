package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = { "code", "modelGroup" })
public class ProductModel {

	private String code;
	private String modelGroup;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String familyCode;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String brandCode;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String productTeamCode;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String productGroupCode;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String name;
	private Boolean isBarcodeAssignmentEnabled;
	private String styleCode;
	private Set<String> colourCodes;
	private Set<String> sizeCodes;

}
