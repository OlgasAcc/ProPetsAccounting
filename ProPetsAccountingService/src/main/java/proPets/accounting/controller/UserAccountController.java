package proPets.accounting.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proPets.accounting.configuration.AccountConfiguration;
import proPets.accounting.configuration.BeanConfiguration;
import proPets.accounting.configuration.JWTConfiguration;
import proPets.accounting.dto.NewUserDto;
import proPets.accounting.dto.UserEditDto;
import proPets.accounting.dto.UserProfileDto;
import proPets.accounting.dto.UserStatesDto;
import proPets.accounting.exceptions.UserNotFoundException;
import proPets.accounting.service.UserAccountService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/account/v1")
public class UserAccountController {

	@Autowired
	UserAccountService userAccountService;

	@Autowired
	JWTConfiguration jwtConfiguration;

	@Autowired
	AccountConfiguration accountConfiguration;

	// to test Config service
	@RefreshScope
	@GetMapping("/config")
	public BeanConfiguration getRefreshedData() {
		return new BeanConfiguration(jwtConfiguration.getSecret(), jwtConfiguration.getExpPeriodValue(),
				jwtConfiguration.getMessage(), accountConfiguration.getMessagingCleanerUrl(),
				accountConfiguration.getLostFoundCleanerUrl(), accountConfiguration.getSearchingCleanerUrl());
	}

	@PostMapping("/sign_up")
	public ResponseEntity<UserStatesDto> register(@RequestBody NewUserDto newUserDto) throws Exception {
		return userAccountService.register(newUserDto);
	}

	@PostMapping("/sign_in")
	public UserStatesDto login(Principal principal)
			throws Exception {
		UserStatesDto user = userAccountService.findUser(principal.getName());
		return user;
	}

	@GetMapping("/user/refresh_page")
	public UserStatesDto refreshPage(Principal principal)
			throws Exception {
		UserStatesDto user = userAccountService.findUser(principal.getName());
		return user;
	}

	@DeleteMapping("/user")
	public UserStatesDto removeUser(Principal principal) throws UserNotFoundException, Exception {
		return userAccountService.removeUser(principal.getName());
	}

	@PutMapping("/user")
	public UserStatesDto editUser(@RequestBody UserEditDto userEditDto, Principal principal) {
		return userAccountService.editUser(userEditDto, principal.getName());
	}

	@GetMapping("/user/{other_user_id}")
	public UserProfileDto getOtherUserInfo(@RequestHeader(value = "Authorization") String authorization,
			@PathVariable String other_user_id) {
		return userAccountService.getOtherUserInfo(other_user_id);
	}

	@PostMapping("/user/role/{id}/{role}")
	public void addRole(@PathVariable String id, @PathVariable String role, Principal principal) {
		userAccountService.addRole(id, role, principal.getName());
	}

	@DeleteMapping("/user/role/{id}/{role}")
	public void removeRole(@PathVariable String id, @PathVariable String role, Principal principal) {
		userAccountService.removeRole(id, role, principal.getName());
	}

	@PutMapping("/user/password")
	public void changePassword(@RequestHeader("X-Password") String newPassword, Principal principal) {
		userAccountService.changePassword(newPassword, principal.getName());
	}

	@GetMapping("/user/users")
	public Iterable<UserStatesDto> getAllUsers(Principal principal) {
		return userAccountService.getAllUsers(principal.getName());
	}

	@PutMapping("/user/{id}")
	public void blockOrUnblockUser(@PathVariable String id, Principal principal) {
		userAccountService.blockOrUnblockUser(id, principal.getName());
	}
}
