package com.springboot.project.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.google.common.collect.Lists;
import com.springboot.project.common.storage.SequenceResource;

@RestController
public class UploadResourceController extends BaseController {

	@PostMapping("/upload/resource")
	public ResponseEntity<?> uploadResource(MultipartFile file) throws IOException {
		var storageFileModel = this.storage.storageResource(file);
		return ResponseEntity.ok(storageFileModel.getRelativeUrl());
	}

	@PostMapping("/upload/merge")
	public ResponseEntity<?> mergeResource(@RequestBody String[] urlList) throws IOException {
		var resourceList = JinqStream.from(Lists.newArrayList(urlList)).select(url -> {
			try {
				return (Resource) new UrlResource(new URIBuilder(url).build());
			} catch (MalformedURLException | URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}).toList();
		var fileName = this.storage.getFileNameFromResource(resourceList.stream().findFirst().get());
		var storageFileModel = this.storage.storageResource(new SequenceResource(fileName, resourceList));
		return ResponseEntity.ok(storageFileModel.getRelativeUrl());
	}

}
