package proPets.accounting.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import proPets.accounting.model.UserAccount;

public interface UserAccountRepository extends MongoRepository<UserAccount, String> {

	List<UserAccount> findAll();
	
	//Stream<UserAccount>findByName (String name); // optionally
}
