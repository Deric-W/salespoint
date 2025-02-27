/*
 * Copyright 2017-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.salespointframework.support;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;

/**
 * Unit tests for {@link RecordingMailSender}.
 * 
 * @author Oliver Gierke
 */
class RecordingMailSenderUnitTests {

	@Test // #149
	void exposesSentEmails() {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("me");
		message.setTo("you");
		message.setSubject("subject");
		message.setText("text");

		RecordingMailSender sender = new RecordingMailSender();
		sender.send(message);

		assertThat(sender.getSentMessages()).containsExactly(message);
	}
}
