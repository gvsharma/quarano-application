package quarano.account;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import quarano.account.Account.AccountIdentifier;
import quarano.account.Account.PasswordResetToken;
import quarano.account.Department.DepartmentIdentifier;
import quarano.account.Password.EncryptedPassword;
import quarano.account.Password.UnencryptedPassword;
import quarano.core.EmailAddress;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Transactional
@Component
@RequiredArgsConstructor
@Slf4j
public class AccountService {

	private final @NonNull PasswordEncoder passwordEncoder;
	private final @NonNull AccountRepository accounts;
	private final @NonNull RoleRepository roles;
	private final @NonNull AuthenticationManager authentication;

	/**
	 * creates a new account, encrypts the password and stores it
	 *
	 * @param username
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param departmentId
	 * @param clientId
	 * @param roleType
	 * @return
	 */
	public Account createTrackedPersonAccount(String username, UnencryptedPassword password, String firstname,
			String lastname, EmailAddress emailAddress, DepartmentIdentifier departmentId) {

		var role = roles.findByType(RoleType.ROLE_USER);
		var account = accounts
				.save(new Account(username, encrypt(password), firstname, lastname, emailAddress, departmentId, List.of(role)));

		return account;
	}

	public Account createStaffAccount(String username, UnencryptedPassword password, String firstname, String lastname,
			EmailAddress email, DepartmentIdentifier departmentId, List<RoleType> roleTypes) {

		var roleList = roleTypes.stream()
				.map(it -> roles.findByType(it))
				.collect(Collectors.toList());

		var account = accounts
				.save(new Account(username, encrypt(password), firstname, lastname, email, departmentId, roleList));

		log.info("Created staff account for " + username);

		return account;
	}

	public Account saveStaffAccount(Account account) {

		var storedAccount = accounts.save(account);

		log.info("Updated staff account for " + storedAccount.getUsername());

		return storedAccount;
	}

	public Account createStaffAccount(String username, UnencryptedPassword password, String firstname, String lastname,
			EmailAddress email, DepartmentIdentifier departmentId, RoleType roleType) {
		return createStaffAccount(username, password, firstname, lastname, email, departmentId, List.of(roleType));
	}

	public boolean isValidUsername(String candidate) {

		if (!isUsernameAvailable(candidate)) {
			return false;
		}
		// check username pattern
		return true;
	}

	public Optional<Account> findByUsername(String username) {
		return accounts.findByUsername(username);
	}

	public boolean isUsernameAvailable(String username) {
		return accounts.isUsernameAvailable(username);
	}

	public boolean matches(@Nullable UnencryptedPassword candidate, EncryptedPassword existing) {

		Assert.notNull(existing, "Existing password must not be null!");

		return Optional.ofNullable(candidate)
				.map(c -> passwordEncoder.matches(c.asString(), existing.asString()))
				.orElse(false);
	}

	public List<Account> findStaffAccountsFor(DepartmentIdentifier departmentId) {
		return accounts.findAccountsFor(departmentId)
				.filter(it -> hasDepartmentRoles(it))
				.collect(Collectors.toList());
	}

	public Optional<Account> findById(AccountIdentifier accountId) {
		return accounts.findById(accountId);
	}

	public void deleteAccount(AccountIdentifier accountIdToDelete) {

		accounts.deleteById(accountIdToDelete);

		log.info("Account with accountId " + accountIdToDelete + " has been deleted.");
	}

	/**
	 * Changes the password of the given {@link Account} to the given {@link UnencryptedPassword} password.
	 *
	 * @param password must not be {@literal null}.
	 * @param account must not be {@literal null}.
	 * @return the account with the new password applied.
	 */
	public Account changePassword(UnencryptedPassword password, Account account) {

		if (account.isTrackedPerson() && !isPasswordChangeBySamePerson(account)) {
			throw new IllegalArgumentException("Tracked people can only change their passwords themselves!");
		}

		log.debug("Reset password for account {}.", account.getUsername());

		return accounts.save(account.setPassword(encrypt(password, account)));
	}

	/**
	 * Requests the passwort reset for the account with the given {@link EmailAddress} and username.
	 *
	 * @param emailAddress must not be {@literal null}.
	 * @param username must not be {@literal null} or empty.
	 * @return a {@link PasswordResetToken} if the combination of parameters identifies an {@link Account} properly.
	 * @since 1.4
	 */
	public Optional<PasswordResetToken> requestPasswordReset(EmailAddress emailAddress, String username) {

		return accounts.findByEmailAddress(emailAddress)
				.filter(it -> it.getUsername().equals(username))
				.findFirst()
				.map(account -> {

					var token = account.requestPasswordReset(LocalDateTime.now().plusHours(2));

					accounts.save(account);

					return token;
				});
	}

	/**
	 * Resets the password for which the given password reset token is set and valid. Also accepts additional
	 * verifications so that the state of an {@link Account} can be verified before the password will be changed
	 * eventually.
	 *
	 * @param password must not be {@literal null}.
	 * @param token must not be {@literal null}.
	 * @param verifications must not be {@literal null}.
	 * @return the {@link Account} if the password change was successful or an empty {@link Optional} if neither the token
	 *         was valid, expired nor any of the given verifications failed.
	 * @since 1.4
	 */
	public Optional<Account> resetPasswort(UnencryptedPassword password, UUID token, Predicate<Account> verifications) {

		return accounts.findByResetToken(token)
				.filter(verifications.and(Account::hasValidPasswordResetToken))
				.map(it -> it.setPassword(encrypt(password, it)))
				.map(accounts::save);
	}

	/**
	 * Check if the account has at least one role that is a department-role
	 *
	 * @param account
	 * @return
	 */
	private boolean hasDepartmentRoles(Account account) {

		return account.getRoles()
				.stream()
				.filter(it -> it.getRoleType().isDepartmentRole())
				.findFirst()
				.isPresent();
	}

	private boolean isPasswordChangeBySamePerson(Account account) {
		return authentication.getCurrentUser().map(account::equals).orElse(false);
	}

	/**
	 * Creates a {@link EncryptedPassword} that's only expired if the current user is an admin.
	 *
	 * @param password must not be {@literal null}.
	 * @return will never be {@literal null}.
	 */
	private EncryptedPassword encrypt(UnencryptedPassword password) {
		return encrypt(password, authentication.getCurrentUser().map(Account::isAdmin).orElse(false));
	}

	/**
	 * Creates an {@link EncryptedPassword} for the given {@link UnencryptedPassword}. The expiry settings will be derived
	 * from the given target {@link Account} the password is to be encrypted for. This will be the case if it's not
	 * someone changing their own password but only if an admin changes the password for someone else.
	 *
	 * @param password must not be {@literal null}.
	 * @param account must not be {@literal null}.
	 * @return will never be {@literal null}.
	 */
	private EncryptedPassword encrypt(UnencryptedPassword password, Account account) {

		Assert.notNull(password, "Password must not be null!");
		Assert.notNull(account, "Account must not be null!");

		var expire = authentication.getCurrentUser()
				.filter(it -> !account.equals(it))
				.map(Account::isAdmin)
				.orElse(false);

		return encrypt(password, expire);
	}

	/**
	 * Encrypts the given {@link UnencryptedPassword} and marks it as expired if needed.
	 *
	 * @param password must not be {@literal null}.
	 * @param expirePassword whether to mark the encrypted password as expired.
	 * @return will never be {@literal null}.
	 */
	private EncryptedPassword encrypt(UnencryptedPassword password, boolean expirePassword) {

		Assert.notNull(password, "Password must not be null!");

		var encoded = passwordEncoder.encode(password.asString());

		return expirePassword
				? EncryptedPassword.of(encoded, LocalDateTime.now())
				: EncryptedPassword.of(encoded);
	}
}
