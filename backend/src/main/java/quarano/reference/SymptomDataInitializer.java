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
package quarano.reference;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import quarano.core.DataInitializer;

import java.io.InputStream;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Oliver Drotbohm
 */
@Component
@RequiredArgsConstructor
@Order(10)
@Slf4j
public class SymptomDataInitializer implements DataInitializer {

	private static final String MASTER_DATA = "classpath:masterdata/symptoms.json";

	private final @NonNull SymptomRepository repository;
	private final @NonNull ModelMapper mapper;
	private final @NonNull ResourceLoader resources;
	private final @NonNull ObjectMapper objectMapper;

	/*
	 * (non-Javadoc)
	 * @see quarano.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {

		try (InputStream in = resources.getResource(MASTER_DATA).getInputStream()) {

			if (this.repository.count() > 0) {
				log.info("Symptom data already exists, skipping JSON import");
				return;
			}

			log.info("Importing symptoms from JSON file: " + MASTER_DATA);

			this.repository.saveAll(objectMapper.readValue(in, new TypeReference<List<Symptom>>() {}));

			log.info("Importing symptoms done!");

		} catch (Exception e) {

			log.warn(e.toString());

			throw new IllegalStateException("Unable to parse masterdata file", e);
		}
	}
}
