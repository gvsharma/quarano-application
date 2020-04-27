/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package quarano.tracking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import quarano.core.DataInitializer;
import quarano.tracking.Address.HouseNumber;
import quarano.tracking.TrackedPerson.TrackedPersonIdentifier;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Oliver Drotbohm
 * @author Patrick Otto
 */
@Order(400)
@Component
@RequiredArgsConstructor
@Slf4j
public class TrackedPersonDataInitializer implements DataInitializer {

	private final TrackedPersonRepository trackedPeople;

	public final static TrackedPersonIdentifier VALID_TRACKED_PERSON1_ID_DEP1 = TrackedPersonIdentifier
			.of(UUID.fromString("738d3d1f-a9f1-4619-9896-2b5cb3a89c22"));
	public final static TrackedPersonIdentifier VALID_TRACKED_PERSON2_ID_DEP1 = TrackedPersonIdentifier
			.of(UUID.fromString("0c434624-7dbe-11ea-bc55-0242ac130003"));
	public final static TrackedPersonIdentifier VALID_TRACKED_PERSON4_ID_DEP1 = TrackedPersonIdentifier
			.of(UUID.fromString("a8bd1d2d-b824-4989-ad9f-73be224654d6"));
	public final static TrackedPersonIdentifier VALID_TRACKED_PERSON5_ID_DEP1 = TrackedPersonIdentifier
			.of(UUID.fromString("29206992-84f0-4a0e-9267-ed0a2b5b7507"));
	public final static TrackedPersonIdentifier VALID_TRACKED_PERSON6_ID_DEP1 = TrackedPersonIdentifier
			.of(UUID.fromString("29206992-84f0-4a0e-9267-ed0a2b5b7508"));
	public final static TrackedPersonIdentifier VALID_TRACKED_PERSON3_ID_DEP2 = TrackedPersonIdentifier
			.of(UUID.fromString("1d5ce370-7dbe-11ea-bc55-0242ac130003"));

	/**
	 * A persona with all contact details submitted, ready to take the next steps in enrollment.
	 *
	 * @return
	 */
	public static TrackedPerson createTanja() {

		return new TrackedPerson(VALID_TRACKED_PERSON1_ID_DEP1, "Tanja", "Mueller",
				EmailAddress.of("tanja.mueller@testtest.de"), PhoneNumber.of("0621111155"), LocalDate.of(1975, 8, 3))
						.setAddress(new Address("Hauptstr. 4", HouseNumber.of("3"), "Mannheim", ZipCode.of("68259")));
	}

	/**
	 * A persona without contact details completed.
	 *
	 * @return
	 */
	public static TrackedPerson createMarkus() {
		return new TrackedPerson(VALID_TRACKED_PERSON2_ID_DEP1, "Markus", "Hanser",
				EmailAddress.of("markus.hanser@testtest.de"), PhoneNumber.of("0621222255"), LocalDate.of(1990, 1, 1));
	}

	/**
	 * A persona fully enrolled.
	 *
	 * @return
	 */
	public static TrackedPerson createSandra() {

		return new TrackedPerson(VALID_TRACKED_PERSON3_ID_DEP2, "Sandra", "Schubert",
				EmailAddress.of("sandra.schubert@testtest.de"), PhoneNumber.of("0621222255"), LocalDate.of(1990, 1, 1))
						.setAddress(new Address("Wingertstraße", HouseNumber.of("71"), "Mannheim", ZipCode.of("68199")));
	}

	/**
	 * A persona without contact details completed.
	 *
	 * @return
	 */
	public static TrackedPerson createGustav() {
		return new TrackedPerson(VALID_TRACKED_PERSON4_ID_DEP1, "Gustav", "Meier",
				EmailAddress.of("gustav.meier@testtest.de"), PhoneNumber.of("06214455684"), LocalDate.of(1980, 1, 1))
						.setAddress(new Address("Am Aubuckel", HouseNumber.of("12"), "Mannheim", ZipCode.of("68259")));
	}

	/**
	 * A persona without contact details completed.
	 *
	 * @return
	 */
	public static TrackedPerson createNadine() {
		return new TrackedPerson(VALID_TRACKED_PERSON5_ID_DEP1, "Nadine", "Ebert",
				EmailAddress.of("nadine.ebert@testtest.de"), PhoneNumber.of("0621 88 334"), LocalDate.of(1980, 1, 1))
						.setAddress(new Address("Seckenheimer Landstr.", HouseNumber.of("50"), "Mannheim", ZipCode.of("68199")));
	}

	/**
	 * A persona without account created.
	 *
	 * @return
	 */
	public static TrackedPerson createHarry() {
		return new TrackedPerson(VALID_TRACKED_PERSON6_ID_DEP1, "Harry", "Hirsch", EmailAddress.of("harry@hirsch.de"), null,
				null);
	}

	/*
	 * (non-Javadoc)
	 * @see quarano.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {

		// create a fixed set of tracked people for testing and integration if it is not present yet
		if (this.trackedPeople.findById(VALID_TRACKED_PERSON1_ID_DEP1).isPresent()) {
			log.info("Initial client data already exists, skipping test data generation");
			return;
		}

		trackedPeople.save(createTanja());
		trackedPeople.save(createMarkus());
		trackedPeople.save(createGustav());
		trackedPeople.save(createNadine());
		trackedPeople.save(createSandra());
		trackedPeople.save(createHarry());

		log.info("Test data: Generated 6 tracked persons.");
	}
}
