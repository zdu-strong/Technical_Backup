package com.springboot.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StorageFileModel {
	private String folderName;

	/**
	 * 当存储一个文件夹时, fileName为null. 当存储文件时, fileName不能为null, 也不能为空字符串.
	 */
	private String fileName;

	private long folderSize;

	private String relativePath;

	private String relativeUrl;

	private String relativeDownloadUrl;

	public StorageFileModel() {

	}

	public StorageFileModel(String folderName, String fileName) {
		this.folderName = folderName;
		this.fileName = fileName;
	}

	public StorageFileModel(String folderName) {
		this.folderName = folderName;
	}

}
