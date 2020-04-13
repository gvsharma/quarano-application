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
package quarano.department;

import org.modelmapper.ModelMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import quarano.tracking.TrackedPersonDataInitializer;

/**
 * @author Oliver Drotbohm
 */
@Component
@RequiredArgsConstructor
@Order(660)
class TrackedCaseDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

	private final ModelMapper mapper;

	private final TrackedCaseRepository cases;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {

			var case1 = new TrackedCase();
			case1.setTrackedPerson(TrackedPersonDataInitializer.INDEX_PERSON1_NOT_REGISTERED);
			case1.setDepartment(DepartmentDataInitializer.DEPARTMENT_1);
			cases.save(case1);
			
			var case2 = new TrackedCase();
			case2.setTrackedPerson(TrackedPersonDataInitializer.INDEX_PERSON2_IN_ENROLLMENT);
			case2.setDepartment(DepartmentDataInitializer.DEPARTMENT_1);
			case2.setEnrollment(new Enrollment());
			case2.setInitialReport(new InitialReport());
			cases.save(case2);
			
			var case3 = new TrackedCase();
			case3.setTrackedPerson(TrackedPersonDataInitializer.INDEX_PERSON3_WITH_ACTIVE_TRACKING);
			case3.setDepartment(DepartmentDataInitializer.DEPARTMENT_2);
			Enrollment enrollment = new Enrollment();
			enrollment.markDetailsSubmitted();
			
			InitialReport report  = new InitialReport();
			report.setBelongToLaboratoryStaff(true);
			report.setBelongToMedicalStaff(true);
			report.setBelongToNursingStaff(true);
			report.setDirectContactWithLiquidsOfC19pat(false);
			report.setFamilyMember(true);
			report.setFlightCrewMemberWithC19Pat(false);
			report.setFlightPassengerCloseRowC19Pat(true);
			report.setMin15MinutesContactWithC19Pat(true);
			report.setNursingActionOnC19Pat(false);
			case3.submitQuestionnaire(report);
			
			//case3.markEnrollmentContactsSubmitted();
			case3.setEnrollment(enrollment);
			cases.save(case3);
	}
}