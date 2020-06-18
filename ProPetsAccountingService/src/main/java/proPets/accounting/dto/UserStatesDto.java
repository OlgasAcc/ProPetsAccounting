package proPets.accounting.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class UserStatesDto {

	String email;
	String name;
	String phone;
	String fb_link;
	String avatar;
	Boolean isBlocked;
	@Singular
	Set<String> roles;

//	@Singular
//	Set<String> favorites;
//	@Singular
//	Set<String> hiddenPosts;
}
