package com.springboot.project.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.orm.stream.JinqStream;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.springboot.project.common.mysql.MysqlFunction;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PaginationModel<T> {
	private Integer pageNum;
	private Integer pageSize;
	private Long totalRecord;
	private Integer totalPage;
	private List<T> list;

	public PaginationModel() {
	}

	public <U> PaginationModel(Integer pageNum, Integer pageSize, JinqStream<U> stream, Function<U, T> callback) {
		if (pageNum < 1) {
			throw new RuntimeException("Page num must be greater than 1");
		}
		if (pageSize < 1) {
			throw new RuntimeException("Page size must be greater than 1");
		}

		this.pageNum = pageNum;
		this.pageSize = pageSize;
		if (stream instanceof JPAJinqStream) {
			try {
				this.totalRecord = stream.count();
			} catch (IllegalArgumentException e) {
				if (this.isTestEnviroment()) {
					/*
					 * The test environment does not configure the dialect of sql, just take the
					 * total number
					 */
					this.totalRecord = stream.sequential().count();
				} else {
					/* Due to unknown reasons, the first execution returns a fixed value */
					stream.select(s -> MysqlFunction.foundTotalRowsForGroupBy()).limit(1).findFirst().orElse(0L);
					/* The second execution returns the correct value */
					this.totalRecord = stream.select(s -> MysqlFunction.foundTotalRowsForGroupBy()).limit(1).findFirst()
							.orElse(0L);
				}
			}
			this.setList(
					stream.skip((pageNum - 1) * pageSize).limit(pageSize).map(callback).collect(Collectors.toList()));
		} else {
			var dataList = stream.toList();
			this.totalRecord = Integer.valueOf(dataList.size()).longValue();
			this.setList(JinqStream.from(dataList).skip((pageNum - 1) * pageSize).limit(pageSize).map(callback)
					.collect(Collectors.toList()));
		}
		this.totalPage = Double.valueOf(Math.ceil(((double) this.totalRecord) / ((double) pageSize))).intValue();
	}

	private boolean isTestEnviroment() {
		try (InputStream input = ClassLoader.getSystemResourceAsStream("application.yml")) {
			YAMLMapper yamlMapper = new YAMLMapper();
			var text = yamlMapper.readTree(input).findValue("properties").findValue("StorageRootPath").asText();
			if ("defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a".equals(text)) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e1) {
			throw new RuntimeException(e1.getMessage(), e1);
		}
	}

}
