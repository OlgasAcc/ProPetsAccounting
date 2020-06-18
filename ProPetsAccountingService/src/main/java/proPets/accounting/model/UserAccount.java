package proPets.accounting.model;

import java.util.Date;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = { "email" })
@Builder
@Document(collection = "all_users")

public class UserAccount {
	@Id
	String email;
	String password;
	String name;
	String phone;
	String fb_link;
	String avatar;
	Boolean isBlocked;
	@Singular                     // позволяет добавлять в сет по одному
	Set<String> roles;
	 
	Date expDate;
	
	public boolean addRole(String role) {
		return roles.add(role);
	}

	public boolean removeRole(String role) {
		return roles.remove(role);
	}


}
