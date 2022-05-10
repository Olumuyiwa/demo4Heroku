package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Barcode {
	private String parent;
	private String size;
	private String colour;
	private String ean13;
	private String brand;
}
