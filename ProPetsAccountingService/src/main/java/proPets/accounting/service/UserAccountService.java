package proPets.accounting.service;

import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;

import proPets.accounting.dto.NewUserDto;
import proPets.accounting.dto.UserEditDto;
import proPets.accounting.dto.UserProfileDto;
import proPets.accounting.dto.UserStatesDto;
import proPets.accounting.exceptions.UserNotFoundException;

public interface UserAccountService {
	
	//Boolean checkName (String name); //optionally
	
	ResponseEntity<UserStatesDto> register(NewUserDto newUserDto) throws Exception;
	
	UserStatesDto findUser (String email) throws Exception;
	
	UserStatesDto removeUser(String email) throws InterruptedException, ExecutionException, UserNotFoundException, Exception;
	
	UserStatesDto editUser(UserEditDto userEditDto, String email);
	
	UserProfileDto getOtherUserInfo (String emailOtherUser);
	
	void addRole(String email, String role, String adminEmail);
	
	void removeRole(String email, String role, String adminEmail);
	
	void changePassword(String newPassword, String email);

	Iterable<UserStatesDto> getAllUsers(String name);
	
	void blockOrUnblockUser(String email, String adminEmail);
	
}
