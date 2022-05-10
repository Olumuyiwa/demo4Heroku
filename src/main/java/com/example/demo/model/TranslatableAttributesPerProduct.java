package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TranslatableAttributesPerProduct {

	private String attributeId;
	private String productId;
	private String code;
	private String value;

}
