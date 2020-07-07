package proPets.accounting.service;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import proPets.accounting.configuration.JWTConfiguration;
import proPets.accounting.dao.UserAccountRepository;
import proPets.accounting.dto.NewUserDto;
import proPets.accounting.dto.UserEditDto;
import proPets.accounting.dto.UserProfileDto;
import proPets.accounting.dto.UserRemoveDto;
import proPets.accounting.dto.UserStatesDto;
import proPets.accounting.exceptions.ForbiddenException;
import proPets.accounting.exceptions.UserConflictException;
import proPets.accounting.exceptions.UserExistsException;
import proPets.accounting.exceptions.UserNotFoundException;
import proPets.accounting.exceptions.WrongDataFormatException;
import proPets.accounting.model.UserAccount;

@Service
public class UserAccountServiceImpl implements UserAccountService {

	@Autowired
	UserAccountRepository accountRepository;
	
	@Autowired
	JWTConfiguration jwtConfiguration;
	
	@Autowired
	JwtService jwtService;

	@Override
	public ResponseEntity<UserStatesDto> register(NewUserDto newUserDto) throws Exception {

		if (accountRepository.existsById(newUserDto.getEmail())) {
			throw new UserExistsException();
		}
		String hashPassword = BCrypt.hashpw(newUserDto.getPassword(), BCrypt.gensalt());
		UserAccount userAccount = UserAccount.builder().email(newUserDto.getEmail()).password(hashPassword)
				.name(newUserDto.getName())
				.avatar("https://c7.hotpng.com/preview/918/598/466/paw-logo-clip-art-animal-footprint.jpg")
				.phone("none")
				.fb_link("none")
				.isBlocked(false)
				.role("ROLE_USER").build();
		accountRepository.save(userAccount);
		String roles = convertSetOfRolesToString(userAccount.getRoles());

		String newToken = jwtService.createAuthenticationToken(userAccount.getEmail(), roles);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("X-token", newToken);

		return ResponseEntity.ok().headers(responseHeaders).body(userAccountToUserStatesDto(userAccount));
	}


	@Override
	public UserStatesDto findUser(String email) throws Exception {
		UserAccount userAccount = accountRepository.findById(email).get();
		return userAccountToUserStatesDto(userAccount);
	}

	@Override
	public UserStatesDto removeUser(String email) throws Exception  {
		System.out.println("1");
		UserAccount userAccount = accountRepository.findById(email).orElseThrow(()->new UserNotFoundException());
		System.out.println("2");
		UserStatesDto res = findUser(email);
		System.out.println(userAccount.getEmail());
		accountRepository.deleteById(email);
		UserRemoveDto dto = new UserRemoveDto(email);
		// String url = "https://propets-.../message/v1/post/cleaner";
		String urlMessaging = "http://localhost:8083/message/v1/post/cleaner";
		// String url = "https://propets-.../lost/v1/post/cleaner";
		String urlLostFound = "http://localhost:8081/lost/v1/post/cleaner"; //
		// String url = "https://propets-.../search/v1/cleaner";
		String urlSearching = "http://localhost:8085/search/v1/cleaner"; //
		try {
			Boolean res1=removeUserDataInExternalService(email, dto, urlMessaging).get();
			System.out.println(res);
			removeUserDataInExternalService(email, dto, urlLostFound);
			removeUserDataInExternalService(email, dto, urlSearching);
				//return userAccountToUserStatesDto(userAccount);
			return res;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;

	}		
	
	@Async("processExecutor")
	private CompletableFuture<Boolean> removeUserDataInExternalService(String email, UserRemoveDto userRemoveDto,String url)  {
		RestTemplate restTemplate = jwtConfiguration.restTemplate();
		BodyBuilder requestBodyBuilder = RequestEntity.method(HttpMethod.DELETE, URI.create(url));
		RequestEntity<UserRemoveDto> request = requestBodyBuilder.body(userRemoveDto);
		ResponseEntity<String> newResponse = restTemplate.exchange(request, String.class);
		return CompletableFuture.completedFuture(newResponse.getStatusCode() == HttpStatus.OK);
	}
	


	@Override
	public UserStatesDto editUser(UserEditDto userEditDto, String email) {

		UserAccount userAccount = accountRepository.findById(email).get();
		if (userEditDto.getName() != null) {
			userAccount.setName(userEditDto.getName());
		}
		if (userEditDto.getPhone() != null) {
			userAccount.setPhone(userEditDto.getPhone());
		}
		if (userEditDto.getAvatar() != null) {
			userAccount.setAvatar(userEditDto.getAvatar());
		}
		if (userEditDto.getFb_link() != null) {
			userAccount.setFb_link(userEditDto.getFb_link());
		}
		accountRepository.save(userAccount);

		return userAccountToUserStatesDto(userAccount);

	}

	@Override
	public UserProfileDto getOtherUserInfo(String emailOtherUser) {
		UserAccount otherUserAccount = accountRepository.findById(emailOtherUser)
				.orElseThrow(() -> new UserConflictException());
		return UserProfileDto.builder().name(otherUserAccount.getName()).avatar(otherUserAccount.getAvatar())
				.phone(otherUserAccount.getPhone()).email(otherUserAccount.getEmail())
				.fb_link(otherUserAccount.getFb_link()).build();
	}

	@Override
	public void addRole(String email, String role, String adminEmail) {
		UserAccount admin = accountRepository.findById(adminEmail).get();
		UserAccount userToAddRole = accountRepository.findById(email).orElseThrow(() -> new UserConflictException());
		if (role.equalsIgnoreCase("moderator")) {
			if (admin.getRoles().contains("ROLE_ADMIN")) {
				userToAddRole.addRole("ROLE_" + role.toUpperCase());
			} else
				throw new ForbiddenException();
		} else
			throw new WrongDataFormatException();
		accountRepository.save(userToAddRole);
	}

	@Override
	public void removeRole(String email, String role, String adminEmail) {
		UserAccount admin = accountRepository.findById(adminEmail).get();
		UserAccount userToAddRole = accountRepository.findById(email).orElseThrow(() -> new UserConflictException());
		if (role.equalsIgnoreCase("moderator")) {
			if (admin.getRoles().contains("ROLE_ADMIN")) {
				userToAddRole.removeRole("ROLE_" + role.toUpperCase());
			} else
				throw new ForbiddenException();
		} else
			throw new WrongDataFormatException();
		accountRepository.save(userToAddRole);
	}

	@Override
	public void changePassword(String password, String email) {
		UserAccount userAccount = accountRepository.findById(email).get();
		String newHashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		userAccount.setPassword(newHashPassword);
		accountRepository.save(userAccount);
	}

	@Override
	public Iterable<UserStatesDto> getAllUsers(String email) {
		UserAccount admin = accountRepository.findById(email).get();
		List<UserAccount> list = accountRepository.findAll();
		if (admin.getRoles().contains("ROLE_ADMIN")) {
			return list.stream().map(this::userAccountToUserStatesDto).collect(Collectors.toList());
		} else
			throw new ForbiddenException();
	}

	@Override
	public void blockOrUnblockUser(String email, String adminEmail) {
		UserAccount userToBlockUnblock = accountRepository.findById(email)
				.orElseThrow(() -> new UserConflictException());
		UserAccount admin = accountRepository.findById(adminEmail).get();
		if (admin.getRoles().contains("ROLE_ADMIN")) {
			userToBlockUnblock.setIsBlocked(!userToBlockUnblock.getIsBlocked());
			accountRepository.save(userToBlockUnblock);
		} else
			throw new ForbiddenException();
	}
	
	
	
	
	// UTILS:
	//__________________________________
	
	private UserStatesDto userAccountToUserStatesDto(UserAccount userAccount) {
		return UserStatesDto.builder().email(userAccount.getEmail()).name(userAccount.getName())
				.avatar(userAccount.getAvatar()).phone(userAccount.getPhone()).fb_link(userAccount.getFb_link())
				.isBlocked(userAccount.getIsBlocked()).roles(userAccount.getRoles())
				.build();
	}

	private String convertSetOfRolesToString(Set<String> roles) {
		String[] userRoles = roles.stream().map((r) -> r.toUpperCase()).toArray(String[]::new);
		String res = String.join(",", userRoles);
		return res;
	}

//	@Override
//	public Boolean checkName(String name) {
//		List<UserAccount> list = accountRepository.findByName(name).collect(Collectors.toList());
//		return list.isEmpty();
//	}

}
