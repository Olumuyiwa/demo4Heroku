package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VariantValues {

	private String identifier;
	private String parent;
	private Boolean enabled;
	private String created;
	private String updated;
	@JsonProperty("_links")
	private Map<String, Object> links;
	private Map<String, Object> values;

}
