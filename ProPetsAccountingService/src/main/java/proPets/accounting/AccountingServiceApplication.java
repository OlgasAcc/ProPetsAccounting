package proPets.accounting;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import proPets.accounting.dao.UserAccountRepository;
import proPets.accounting.model.UserAccount;

//@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync
public class AccountingServiceApplication implements CommandLineRunner {
	
	@Autowired
	UserAccountRepository userAccountRepository;
	
	@Bean(name="processExecutor")
    public TaskExecutor workExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("Async-");
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(20);
        threadPoolTaskExecutor.setQueueCapacity(600);
        threadPoolTaskExecutor.afterPropertiesSet();
        return threadPoolTaskExecutor;
    }

	public static void main(String[] args) {
		SpringApplication.run(AccountingServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (!userAccountRepository.existsById("admin")) {
			String hashPassword = BCrypt.hashpw("admin", BCrypt.gensalt());
			UserAccount admin = UserAccount.builder()
					.email("admin")
					.name("Admin")					
					.password(hashPassword)
					.role("ROLE_USER")
					.role("ROLE_MODERATOR")
					.role("ROLE_ADMIN")
					.build();
			userAccountRepository.save(admin);
		}
	}
}
