package com.example.demo.notification;

import com.example.demo.entity.Task;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@ConditionalOnProperty(name = "notifications.sms.enabled", havingValue = "true")
public class SmsNotificationService {

	private final String fromPhoneNumber;

	public SmsNotificationService(
			@Value("${twilio.account-sid}") String accountSid,
			@Value("${twilio.auth-token}") String authToken,
			@Value("${twilio.phone-number}") String fromPhoneNumber) {
		Twilio.init(accountSid, authToken);
		this.fromPhoneNumber = fromPhoneNumber;
	}

	public void sendReminder(Task task) {
		if (!StringUtils.hasText(task.getPhoneNumber())) {
			return;
		}

		Message.creator(
				new PhoneNumber(task.getPhoneNumber()),
				new PhoneNumber(fromPhoneNumber),
				"Todo reminder: " + task.getTitle()
		).create();
	}
}
