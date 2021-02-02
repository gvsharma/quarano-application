package quarano.tracking;

import static org.apache.commons.lang3.ObjectUtils.*;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import quarano.core.Address;
import quarano.core.Address.HouseNumber;
import quarano.core.EmailAddress;
import quarano.core.PhoneNumber;
import quarano.core.QuaranoAggregate;
import quarano.core.ZipCode;
import quarano.tracking.ContactPerson.ContactPersonIdentifier;
import quarano.tracking.TrackedPerson.TrackedPersonIdentifier;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.jmolecules.ddd.types.Identifier;
import org.jmolecules.event.types.DomainEvent;

/**
 * @author Oliver Drotbohm
 * @author Michael J. Simons
 * @author Felix Schultze
 */
@Entity
@Table(name = "contact_people")
@Data
@EqualsAndHashCode(callSuper = true, of = {})
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class ContactPerson extends QuaranoAggregate<ContactPerson, ContactPersonIdentifier> {

	private String firstName, lastName;
	private @Setter(AccessLevel.NONE) EmailAddress emailAddress;

	@AttributeOverride(name = "value", column = @Column(name = "mobilePhoneNumber"))
	private @Setter(AccessLevel.NONE) PhoneNumber mobilePhoneNumber;
	private @Setter(AccessLevel.NONE) PhoneNumber phoneNumber;
	private Address address;

	private @Enumerated(EnumType.STRING) TypeOfContract typeOfContract;
	private String remark;
	private @Setter(AccessLevel.NONE) String identificationHint;

	private @Getter @Setter Boolean isHealthStaff;
	private @Getter @Setter Boolean isSenior;
	private @Getter @Setter Boolean hasPreExistingConditions;
	private @Getter @Setter boolean anonymized;

	@Column(nullable = false)
	@AttributeOverride(name = "trackedPersonId", column = @Column(name = "tracked_person_id"))
	private TrackedPersonIdentifier ownerId;

	public ContactPerson(String firstName, String lastName, ContactWays contactWays) {

		this.id = ContactPersonIdentifier.of(UUID.randomUUID());
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = contactWays.getEmailAddress();
		this.phoneNumber = contactWays.getPhoneNumber();
		this.mobilePhoneNumber = contactWays.getMobilePhoneNumber();
		this.identificationHint = contactWays.getIdentificationHint();
	}

	public String getFullName() {
		return String.format("%s %s", firstName, lastName);
	}

	public ContactPerson assignOwner(TrackedPerson person) {

		this.ownerId = person.getId();

		return this;
	}

	public boolean belongsTo(TrackedPerson person) {
		return this.ownerId.equals(person.getId());
	}

	public boolean isVulnerable() {
		return getIsSenior() == Boolean.TRUE || getHasPreExistingConditions() == Boolean.TRUE;
	}

	public ContactPerson contactWays(ContactWays contactWays) {

		this.emailAddress = contactWays.getEmailAddress();
		this.phoneNumber = contactWays.getPhoneNumber();
		this.mobilePhoneNumber = contactWays.getMobilePhoneNumber();
		this.identificationHint = contactWays.getIdentificationHint();

		return this;
	}

	/**
	 * anonymized personal data
	 * 
	 * @return
	 * @since 1.4
	 */
	ContactPerson anonymize() {

		firstName = "###";
		lastName = "###";
		phoneNumber = null;
		mobilePhoneNumber = null;
		emailAddress = null;
		identificationHint = "###";
		remark = "###";

		if (address != null) {
			address.setStreet(null);
			address.setHouseNumber(null);
		}

		anonymized = true;

		return this;
	}

	/**
	 * @since 1.4
	 */
	ContactPerson fillSampleData() {

		firstName = defaultIfNull(firstName, "firstName");
		lastName = defaultIfNull(lastName, "lastName");
		phoneNumber = defaultIfNull(phoneNumber, PhoneNumber.of("12345"));
		mobilePhoneNumber = defaultIfNull(mobilePhoneNumber, PhoneNumber.of("67890"));
		emailAddress = defaultIfNull(emailAddress, EmailAddress.of("email@address.xx"));
		identificationHint = defaultIfNull(identificationHint, "identificationHint");
		remark = defaultIfNull(remark, "remark");

		address = defaultIfNull(address, new Address());
		address.setCity(defaultIfNull(address.getCity(), "city"));
		address.setZipCode(defaultIfNull(address.getZipCode(), ZipCode.of("11111")));
		address.setStreet(defaultIfNull(address.getStreet(), "street"));
		address.setHouseNumber(defaultIfNull(address.getHouseNumber(), HouseNumber.of("111")));

		return this;
	}

	public enum TypeOfContract {

		O("O"), S("S"), P("P"), AE("Ä"), Aer("Aer"), Mat("Mat"), And("And");

		private final @Getter String label;

		TypeOfContract(final String label) {
			this.label = label;
		}
	}

	public enum TypeOfProtection {
		Zero("0"), M1("M1"), M2("M2"), K("K"), H("H"), S("S");

		private final @Getter String label;

		TypeOfProtection(final String label) {
			this.label = label;
		}
	}

	@Value(staticConstructor = "of")
	public static class ContactPersonAdded implements DomainEvent {
		ContactPerson contactPerson;
	}

	@Embeddable
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of")
	@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
	public static class ContactPersonIdentifier implements Identifier, Serializable {

		private static final long serialVersionUID = -8869631517068092437L;

		final UUID contactPersonId;

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return contactPersonId.toString();
		}
	}
}
