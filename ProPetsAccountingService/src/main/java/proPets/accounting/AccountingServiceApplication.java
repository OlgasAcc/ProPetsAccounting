package proPets.accounting;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import proPets.accounting.dao.UserAccountRepository;
import proPets.accounting.model.UserAccount;

@SpringBootApplication
public class AccountingServiceApplication implements CommandLineRunner {
	
	@Autowired
	UserAccountRepository userAccountRepository;

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
