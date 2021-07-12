package com.springboot.project.common.longtermtask;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.LongTermTaskService;
import io.reactivex.Observable;

@Component
public class LongTermTaskUtil {
	@Autowired
	private LongTermTaskService longTermTaskService;

	@Autowired
	private EncryptDecryptService encryptDecryptService;

	/**
	 * The return value of the executed method will be stored in the database as a
	 * json string, and will be converted into a json object or json object array
	 * and returned after success. This method will return a relative url, can call
	 * a get request to get the result.
	 * 
	 * @param runnable
	 * @return
	 */
	public ResponseEntity<String> run(Supplier<ResponseEntity<?>> supplier) {
		String idOfLongTermTask = this.longTermTaskService.createLongTermTask();
		CompletableFuture.runAsync(() -> {
			var taskOfUpdateLongTermTaskToRefreshUpdateDate = CompletableFuture.runAsync(() -> {
				Observable.interval(10, TimeUnit.SECONDS).map((s) -> {
					this.longTermTaskService.updateLongTermTaskToRefreshUpdateDate(idOfLongTermTask);
					return null;
				}).blockingSubscribe();
			});
			try {
				var result = supplier.get();
				this.longTermTaskService.updateLongTermTaskByResult(idOfLongTermTask, result);
			} catch (Throwable e) {
				this.longTermTaskService.updateLongTermTaskByErrorMessage(idOfLongTermTask, e);
			} finally {
				taskOfUpdateLongTermTaskToRefreshUpdateDate.cancel(true);
			}
		});
		try {
			var url = new URIBuilder("/long_term_task").setParameter("id",
					this.encryptDecryptService.encryptBySecretKeyOfAESToBase64String(idOfLongTermTask)).build();
			return ResponseEntity.accepted().body(url.toString());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
