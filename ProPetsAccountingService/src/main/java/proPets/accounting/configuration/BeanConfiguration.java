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
	private String messagingCleanerUrl;
	private String lostFoundCleanerUrl;
	private String searchingCleanerUrl;

	public BeanConfiguration(String secret, long expPeriodValue, String message, String messagingCleanerUrl,
			String lostFoundCleanerUrl, String searchingCleanerUrl) {
		this.secret = secret;
		this.expPeriodValue = expPeriodValue;
		this.message = message;
		this.messagingCleanerUrl = messagingCleanerUrl;
		this.lostFoundCleanerUrl = lostFoundCleanerUrl;
		this.searchingCleanerUrl = searchingCleanerUrl;
	}

}
