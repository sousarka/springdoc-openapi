/*
 *
 *  *
 *  *  *
 *  *  *  * Copyright 2019-2022 the original author or authors.
 *  *  *  *
 *  *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *  * you may not use this file except in compliance with the License.
 *  *  *  * You may obtain a copy of the License at
 *  *  *  *
 *  *  *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *  *  *
 *  *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *  * See the License for the specific language governing permissions and
 *  *  *  * limitations under the License.
 *  *  *
 *  *
 *
 */

package test.org.springdoc.api.v30.app145;

import org.junit.jupiter.api.Test;
import org.springdoc.core.Constants;
import test.org.springdoc.api.v30.AbstractSpringDocActuatorV30Test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT,
		properties = { "management.endpoints.web.exposure.include:*",
				"server.port=55556",
				"springdoc.use-management-port=true",
				"springdoc.group-configs[0].group=users",
				"springdoc.group-configs[0].packages-to-scan=test.org.springdoc.api.v30.app145",
				"management.server.port=9091",
				"management.endpoints.web.base-path=/application" })
public class SpringDocApp145Test extends AbstractSpringDocActuatorV30Test {

	@SpringBootApplication
	static class SpringDocTestApp {}

	@Test
	public void testApp() throws Exception {
		mockMvc.perform(get(Constants.DEFAULT_API_DOCS_URL + "/users"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testApp1() throws Exception {
		try {
			actuatorRestTemplate.getForObject("/application/openapi", String.class);
			fail();
		}
		catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND)
				assertTrue(true);
			else
				fail();
		}
	}

	@Test
	public void testApp2() throws Exception {
		String result = actuatorRestTemplate.getForObject("/application/openapi/users", String.class);
		String expected = getContent("results/3.0.1/app145.json");
		assertEquals(expected, result, true);
	}

	@Test
	public void testApp3() throws Exception {
		try {
			actuatorRestTemplate.getForObject("/application/openapi"+  "/"+Constants.DEFAULT_GROUP_NAME, String.class);
			fail();
		}
		catch (HttpStatusCodeException ex) {
			// TODO: Currently obtain status 500 on MVC... Webflux obtain 404... 
			if (ex.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
				assertTrue(true);
			else
				fail();
		}
	}
}
