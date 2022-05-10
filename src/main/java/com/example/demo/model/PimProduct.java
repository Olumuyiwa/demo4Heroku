package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class PimProduct implements Serializable {

	private static final long serialVersionUID = -3900832430394748842L;
	@JsonProperty("_links")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, Object> links;
	private String identifier;
	private Boolean enabled;
	private String family;
	private List<String> categories;
	private List<String> groups;
	private String parent;
	private Map<String, Object> values;
	private String created;
	private String updated;
	private Map<String, Object> associations;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<VariantValues> variants;
	private Map<String, Object> metadata;
}
