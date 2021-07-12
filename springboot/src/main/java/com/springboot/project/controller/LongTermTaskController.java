package com.springboot.project.controller;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LongTermTaskController extends BaseController {
	private Duration tempWaitDuration = Duration.ofSeconds(30);

	/**
	 * Because some requests take a long time to execute, so provide this
	 * asynchronous task api. Call them first, like this: (
	 * this.longTermTaskUtil.run(()->{});). And then call this api for polling to
	 * obtain the execution results.
	 * 
	 * @param id
	 * @return
	 * @throws InterruptedException
	 */
	@GetMapping("/long_term_task")
	public ResponseEntity<?> getLongTermTask(@RequestParam String id) throws InterruptedException {

		this.longTermTaskService
				.checkIsExistLongTermTaskById(this.encryptDecryptService.decryptByBySecretKeyOfAESByBase64String(id));

		Calendar calendarOfWait = Calendar.getInstance();
		calendarOfWait.setTime(new Date());
		calendarOfWait.add(Calendar.MILLISECOND, Long.valueOf(this.tempWaitDuration.toMillis()).intValue());
		Date expireDate = calendarOfWait.getTime();

		while (new Date().before(expireDate)) {
			var result = this.longTermTaskService
					.getLongTermTask(this.encryptDecryptService.decryptByBySecretKeyOfAESByBase64String(id));
			if (result.getBody().getIsDone()) {
				return result;
			}

			Thread.sleep(1000);
		}

		return this.longTermTaskService
				.getLongTermTask(this.encryptDecryptService.decryptByBySecretKeyOfAESByBase64String(id));
	}
}
