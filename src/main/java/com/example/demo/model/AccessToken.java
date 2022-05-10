package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Component
@Builder
public class AccessToken {
	private String token;
	private LocalDateTime expiryTime;

//	@JsonCreator
//	public AccessToken(@JsonProperty("access_token") String accessToken, @JsonProperty("expires_in") int expiresInSeconds) {
//		this.token = accessToken;
//		this.expiryTime = System.currentTimeMillis() + (1000 * expiresInSeconds);
//	}

	public boolean willExpireSoon() {
		return expiryTime == null || expiryTime.minusSeconds(90).isBefore(LocalDateTime.now());
		//return (expiryTime - System.currentTimeMillis() < 90000);
	}
}
