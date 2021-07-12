package com.springboot.project.test.common.MysqlFunction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.springboot.project.common.mysql.MysqlFunction;
import com.springboot.project.test.BaseTest;

public class MysqlFunctionIfnullTest extends BaseTest {

	@Test
	public void test() {
		Assertions.assertThrows(RuntimeException.class, () -> {
			MysqlFunction.ifnull(Long.valueOf(1), 0);
		});
		Assertions.assertThrows(RuntimeException.class, () -> {
			MysqlFunction.ifnull(Integer.valueOf(1), 0);
		});
		Assertions.assertThrows(RuntimeException.class, () -> {
			MysqlFunction.ifnull(Double.valueOf(1), 0);
		});
	}

}
