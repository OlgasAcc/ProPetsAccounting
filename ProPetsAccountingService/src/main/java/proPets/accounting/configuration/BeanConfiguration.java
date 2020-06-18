package proPets.accounting.configuration;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@Getter
public class BeanConfiguration {
	private String secret;
	private long expPeriodValue;
	private String message;

	public BeanConfiguration(String secret, long expPeriodValue, String message) {
		this.secret = secret;
		this.expPeriodValue = expPeriodValue;
		this.message = message;
	}
}
