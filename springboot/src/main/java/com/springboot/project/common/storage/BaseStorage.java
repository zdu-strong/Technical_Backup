package com.springboot.project.common.storage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.utils.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.springboot.project.model.ResourceAccessLegalModel;
import com.springboot.project.properties.StorageRootPathProperties;
import com.springboot.project.service.EncryptDecryptService;

import io.reactivex.Observable;

@Component
public class BaseStorage {

	@Autowired
	protected EncryptDecryptService encryptDecryptService;

	@Autowired
	protected StorageRootPathProperties storageRootPathProperties;

	@Autowired
	protected CloudStorageImplement cloud;

	protected String storageRootPath;

	public Observable<String> listRoots() {
		if (this.cloud.enabled()) {
			return Observable.concat(Observable.fromArray(new File(this.getRootPath()).list()),
					this.cloud.getRootList());
		} else {
			return Observable.fromArray(new File(this.getRootPath()).list());
		}
	}

	public String getRootPath() {
		if (Strings.isNullOrEmpty(storageRootPath)) {
			synchronized (this.getClass()) {
				if (Strings.isNullOrEmpty(storageRootPath)) {
					File currentFolderPath = Paths.get(new File("./").getAbsolutePath()).normalize().toFile();
					String rootPath = "";
					if (this.storageRootPathProperties.getStorageRootPath().equals("default")) {
						if (new File(currentFolderPath, ".mvn").isDirectory()) {
							rootPath = Paths.get(currentFolderPath.getAbsolutePath(), ".mvn/storage").toString();
						} else {
							rootPath = Paths.get(currentFolderPath.getAbsolutePath(), "storage").toString();
						}
					} else if (this.storageRootPathProperties.getStorageRootPath()
							.equals("defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a")) {
						if (new File(currentFolderPath, ".mvn").isDirectory()) {
							rootPath = Paths.get(currentFolderPath.getAbsolutePath(), "target/storage").toString();
						} else {
							rootPath = Paths.get(currentFolderPath.getAbsolutePath(), "storage").toString();
						}
					} else {
						if (Strings.isNullOrEmpty(this.storageRootPathProperties.getStorageRootPath().trim())) {
							throw new RuntimeException("Unsupported storage root path");
						}
						if (new File(this.storageRootPathProperties.getStorageRootPath()).isAbsolute()) {
							rootPath = this.storageRootPathProperties.getStorageRootPath();
						} else {
							rootPath = Paths.get(currentFolderPath.getAbsolutePath(), this.storageRootPathProperties
									.getStorageRootPath().replaceAll(Pattern.quote("\\"), "/")).toString();
						}
					}
					rootPath = rootPath.replaceAll("\\\\", "/");
					new File(rootPath).mkdirs();
					this.storageRootPath = rootPath;
				}
			}
		}
		return this.storageRootPath;
	}

	protected String getRelativePathFromResourcePath(String relativePathOfResource) {
		String path = "";
		if (Paths.get(relativePathOfResource.replaceAll(Pattern.quote("\\"), "/")).isAbsolute()) {
			throw new RuntimeException("Only relative path can be passed in");
		} else {
			path = relativePathOfResource;
		}

		path = Paths.get(this.getRootPath(), path.replaceAll(Pattern.quote("\\"), "/")).toString();
		path = Paths.get(path).normalize().toString().replaceAll(Pattern.quote("\\"), "/");
		if (!path.startsWith(this.getRootPath())) {
			throw new RuntimeException("Unsupported path");
		}
		if (path.equals(this.getRootPath())) {
			throw new RuntimeException("Unsupported path");
		}
		return Paths.get(this.getRootPath()).relativize(Paths.get(path)).normalize().toString()
				.replaceAll(Pattern.quote("\\"), "/");
	}

	protected String getRelativePathFromRequest(HttpServletRequest request) {
		try {
			var pathSegmentList = new URIBuilder(request.getRequestURI()).getPathSegments().stream()
					.filter(s -> !Strings.isNullOrEmpty(s)).collect(Collectors.toList());
			if (JinqStream.from(pathSegmentList).limit(1).getOnlyValue().equals("resource")) {
				pathSegmentList = JinqStream.from(pathSegmentList).skip(1).toList();
			} else if (JinqStream.from(pathSegmentList).limit(1).getOnlyValue().equals("download")
					&& JinqStream.from(pathSegmentList).skip(1).limit(1).getOnlyValue().equals("resource")) {
				pathSegmentList = JinqStream.from(pathSegmentList).skip(2).toList();
			} else {
				throw new RuntimeException("Unsupported resource path");
			}
			ResourceAccessLegalModel resourceAccessLegalModel = JSON.parseObject(new String(
					this.encryptDecryptService.decryptByBySecretKeyOfAES(
							Base64.getUrlDecoder().decode(JinqStream.from(pathSegmentList).limit(1).getOnlyValue())),
					StandardCharsets.UTF_8), ResourceAccessLegalModel.class);
			if (!resourceAccessLegalModel.getRootFolderName()
					.equals(JinqStream.from(pathSegmentList).skip(1).limit(1).getOnlyValue())) {
				throw new RuntimeException("Unsupported resource path");
			}
			return String.join("/", JinqStream.from(pathSegmentList).skip(1).toList());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String getResoureUrlFromResourcePath(String relativePathOfResource) {
		try {
			String relativePath = this.getRelativePathFromResourcePath(relativePathOfResource);
			String rootFolderName = JinqStream.from(Lists.newArrayList(relativePath.split("/"))).limit(1)
					.getOnlyValue();
			ResourceAccessLegalModel resourceAccessLegalModel = new ResourceAccessLegalModel();
			resourceAccessLegalModel.setRootFolderName(rootFolderName);
			var pathSegmentList = new ArrayList<String>();
			pathSegmentList.add("resource");
			pathSegmentList
					.add(Base64.getUrlEncoder().encodeToString(this.encryptDecryptService.encryptBySecretKeyOfAES(
							JSON.toJSONString(resourceAccessLegalModel).getBytes(StandardCharsets.UTF_8))));
			pathSegmentList.addAll(Lists.newArrayList(relativePath.split("/")));
			var url = new URIBuilder().setPathSegments(pathSegmentList).build();
			return url.toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String getFileNameFromResource(Resource resource) {
		try {
			if (resource instanceof UrlResource) {
				var pathSegments = Lists.newArrayList(new URIBuilder(resource.getFilename()).getPathSegments());
				Collections.reverse(pathSegments);
				String fileName = pathSegments.stream().findFirst().get();
				return fileName;
			} else {
				if (resource.isFile()) {
					File sourceFile = resource.getFile();
					if (!sourceFile.exists()) {
						throw new RuntimeException("Resource does not exist");
					}
					if (sourceFile.isDirectory()) {
						return null;
					} else {
						return resource.getFilename();
					}
				} else {
					return resource.getFilename();
				}
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
