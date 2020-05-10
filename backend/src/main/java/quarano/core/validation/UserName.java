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
package quarano.core.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;

/**
 * @author Oliver Drotbohm
 * @author Felix Schultze
 */
@ReportAsSingleViolation
@Pattern(regexp = Strings.USERNAME)
@Constraint(validatedBy = {})
@Documented
@Target(FIELD)
@Retention(RUNTIME)
public @interface UserName {
	String ERROR_MSG = "{UserName}";

	String message() default ERROR_MSG;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}