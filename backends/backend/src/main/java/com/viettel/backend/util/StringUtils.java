package com.viettel.backend.util;

public class StringUtils {
	
	/** codau. */
	private static char VIET_CHARS[] = { 'à', 'á', 'ả', 'ã', 'ạ', 'ă', 'ằ', 'ắ', 'ẳ', 'ẵ', 'ặ', 'â', 'ầ', 'ấ', 'ẩ', 'ẫ', 'ậ', 'À',
			'Á', 'Ả', 'Ã', 'Ạ', 'Ă', 'Ằ', 'Ắ', 'Ẳ', 'Ẵ', 'Ặ', 'Â', 'Ầ', 'Ấ', 'Ẩ', 'Ẫ', 'Ậ', 'è', 'é', 'ẻ', 'ẽ', 'ẹ',
			'ê', 'ề', 'ế', 'ể', 'ễ', 'ệ', 'È', 'É', 'Ẻ', 'Ẽ', 'Ẹ', 'Ê', 'Ề', 'Ế', 'Ể', 'Ễ', 'Ệ', 'ì', 'í', 'ỉ', 'ĩ',
			'ị', 'Ì', 'Í', 'Ỉ', 'Ĩ', 'Ị', 'ò', 'ó', 'ỏ', 'õ', 'ọ', 'ô', 'ồ', 'ố', 'ổ', 'ỗ', 'ộ', 'ơ', 'ờ', 'ớ', 'ở',
			'ỡ', 'ợ', 'Ò', 'Ó', 'Ỏ', 'Õ', 'Ọ', 'Ô', 'Ồ', 'Ố', 'Ổ', 'Ỗ', 'Ộ', 'Ơ', 'Ờ', 'Ớ', 'Ở', 'Ỡ', 'Ợ', 'ù', 'ú',
			'ủ', 'ũ', 'ụ', 'ư', 'ừ', 'ứ', 'ử', 'ữ', 'ự', 'Ù', 'Ú', 'Ủ', 'Ũ', 'Ụ', 'ỳ', 'ý', 'ỷ', 'ỹ', 'ỵ', 'Ỳ', 'Ý',
			'Ỷ', 'Ỹ', 'Ỵ', 'đ', 'Đ', 'Ư', 'Ừ', 'Ử', 'Ữ', 'Ứ', 'Ự' 
	};
	
	/** khongdau. */
	private static char NORMAL_CHARS[] = { 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
			'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'e', 'e', 'e', 'e',
			'e', 'e', 'e', 'e', 'e', 'e', 'e', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'i', 'i', 'i',
			'i', 'i', 'I', 'I', 'I', 'I', 'I', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
			'o', 'o', 'o', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'u',
			'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'U', 'U', 'U', 'U', 'U', 'y', 'y', 'y', 'y', 'y', 'Y',
			'Y', 'Y', 'Y', 'Y', 'd', 'D', 'U', 'U', 'U', 'U', 'U', 'U' 
	};
	
	public static String toOracleSearchLikeSuffix(String searchText) {
		return toOracleSearchLike(searchText);
	}
	
	public static String toOracleSearchLike(String searchText) {
		String escapeChar = "/";
		String[] arrSpPat = {"/", "%", "_"};
		
		for (String str: arrSpPat) {
			if (!StringUtils.isNullOrEmpty(searchText)) {
				searchText = searchText.replaceAll(str, escapeChar + str);
			}
		}
		searchText = "%" + searchText + "%";
		return searchText;
	}
	
 	public static boolean isNullOrEmpty(final String s) {
 		return isNullOrEmpty(s, true);
 	}

 	public static boolean isNullOrEmpty(final String s, boolean trim) {
 		if (s == null || "".equals(s)) {
 			return true;
 		}

 		return (trim && "".equals(s.trim()));
 	}

 	public static String arrayToDelimitedString(String[] arr, String delim) {
		if (arr == null || arr.length == 0) {
			return "";
		}
		
		if (arr.length == 1) {
			return ((arr[0] == null) ? "" : arr[0]);
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			if (arr[i] != null) {
				sb.append(arr[i]);
			}
		}
		
		return sb.toString();
	}
 	
 	public static String getSearchableString(String input) {
		if (isNullOrEmpty(input)) {
			return "";
		}
		
		input = input.trim();
		for (int i = 0; i < VIET_CHARS.length; i++) {
			input = input.replace(VIET_CHARS[i], NORMAL_CHARS[i]);
		}
		
		return input;
	}
}
