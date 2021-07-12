package com.springboot.project.common.mysql;

/**
 * In order to call database-specific functions
 * 
 * @author zdu
 *
 */
public class MysqlFunction {
	/**
	 * It accepts two parameters, and if the first parameter is not NULL, it returns
	 * the first parameter. Otherwise, the IFNULL function returns the second
	 * parameter.
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static Long ifnull(Long value, int defaultValue) {
		throw new RuntimeException();
	}

	/**
	 * It accepts two parameters, and if the first parameter is not NULL, it returns
	 * the first parameter. Otherwise, the IFNULL function returns the second
	 * parameter.
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static Long ifnull(Integer value, int defaultValue) {
		throw new RuntimeException();
	}

	/**
	 * It accepts two parameters, and if the first parameter is not NULL, it returns
	 * the first parameter. Otherwise, the IFNULL function returns the second
	 * parameter.
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static Long ifnull(Double value, int defaultValue) {
		throw new RuntimeException();
	}

	/**
	 * This method is only available in mysql, not in the h2 database. In order to
	 * obtain the total number of entries during paging, compatibility processing
	 * has been done at the place of call. Please do not call it.
	 * 
	 * @return
	 */
	public static Long foundTotalRowsForGroupBy() {
		throw new RuntimeException();
	}

	public static Boolean isChildOrganize(String childOrganizeId, String parentOrganizeId) {
		throw new RuntimeException();
	}

    public static Boolean isSortAtBefore(String textOne, String textTwo){
        throw new RuntimeException();
    }
}
