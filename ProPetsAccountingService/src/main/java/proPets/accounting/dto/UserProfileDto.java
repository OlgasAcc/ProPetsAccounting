package proPets.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter

public class UserProfileDto {
	
	String name;
	String phone;
	String avatar;
	String fb_link;
	String email;
}
