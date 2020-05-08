package com.viettel.dms.helper;

import android.content.Context;
import android.text.TextUtils;

import com.viettel.dms.R;

import java.util.List;

public class StringUtils {
	
	/** codau. */
	static char codau[] = { 'à', 'á', 'ả', 'ã', 'ạ', 'ă', 'ằ', 'ắ', 'ẳ', 'ẵ', 'ặ', 'â', 'ầ', 'ấ', 'ẩ', 'ẫ', 'ậ', 'À',
			'Á', 'Ả', 'Ã', 'Ạ', 'Ă', 'Ằ', 'Ắ', 'Ẳ', 'Ẵ', 'Ặ', 'Â', 'Ầ', 'Ấ', 'Ẩ', 'Ẫ', 'Ậ', 'è', 'é', 'ẻ', 'ẽ', 'ẹ',
			'ê', 'ề', 'ế', 'ể', 'ễ', 'ệ', 'È', 'É', 'Ẻ', 'Ẽ', 'Ẹ', 'Ê', 'Ề', 'Ế', 'Ể', 'Ễ', 'Ệ', 'ì', 'í', 'ỉ', 'ĩ',
			'ị', 'Ì', 'Í', 'Ỉ', 'Ĩ', 'Ị', 'ò', 'ó', 'ỏ', 'õ', 'ọ', 'ô', 'ồ', 'ố', 'ổ', 'ỗ', 'ộ', 'ơ', 'ờ', 'ớ', 'ở',
			'ỡ', 'ợ', 'Ò', 'Ó', 'Ỏ', 'Õ', 'Ọ', 'Ô', 'Ồ', 'Ố', 'Ổ', 'Ỗ', 'Ộ', 'Ơ', 'Ờ', 'Ớ', 'Ở', 'Ỡ', 'Ợ', 'ù', 'ú',
			'ủ', 'ũ', 'ụ', 'ư', 'ừ', 'ứ', 'ử', 'ữ', 'ự', 'Ù', 'Ú', 'Ủ', 'Ũ', 'Ụ', 'ỳ', 'ý', 'ỷ', 'ỹ', 'ỵ', 'Ỳ', 'Ý',
			'Ỷ', 'Ỹ', 'Ỵ', 'đ', 'Đ', 'Ư', 'Ừ', 'Ử', 'Ữ', 'Ứ', 'Ự' };
	/** khongdau. */
	static char khongdau[] = { 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
			'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'e', 'e', 'e', 'e',
			'e', 'e', 'e', 'e', 'e', 'e', 'e', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'i', 'i', 'i',
			'i', 'i', 'I', 'I', 'I', 'I', 'I', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
			'o', 'o', 'o', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'u',
			'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'U', 'U', 'U', 'U', 'U', 'y', 'y', 'y', 'y', 'y', 'Y',
			'Y', 'Y', 'Y', 'Y', 'd', 'D', 'U', 'U', 'U', 'U', 'U', 'U' };

    public static final String getString(Context c, int stringRes) {
        return getStringOrNull(c, stringRes);
    }
    
    public static final String getStringOrNull(Context c, int stringRes) {
        return getStringOrDefault(c, stringRes, null);
    }

    public static final String getStringOrEmpty(Context c, int stringRes) {
        return getStringOrDefault(c, stringRes, "");
    }

    public static final String getStringOrDefault(Context c, int stringRes, String defaultStr) {
        if (c != null) {
            return c.getResources().getString(stringRes);
        }
        return defaultStr;
    }
    
    public static final String join(List<?> list, String delimiter){
    	return join(list.toArray(), delimiter);
    }
    
    public static final String join(Object[] list, String delimiter) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < list.length; i++) {
            Object obj = list[i];
            s.append(delimiter).append(obj.toString());
        }
        
        s.delete(0, delimiter.length() - 1);
        
        return s.toString();
    }
    
    public static boolean isNullOrEmpty(String aString) {
		return (aString == null) || (aString.trim().length()==0);
	}
    
    public static String getEngStringFromUnicodeString(String input) {
		if (isNullOrEmpty(input)) {
			return "";
		}
		input = input.trim();

		for (int i = 0; i < codau.length; i++) {
			input = input.replace(codau[i], khongdau[i]);
		}
		return input;
	}

    public static String wrapIntoParentheses(Context ctx, String stringToWrap) {
        return getStringOrDefault(ctx, R.string.symbol_parentheses_left, "(")
                + stringToWrap
                + getStringOrDefault(ctx, R.string.symbol_parentheses_right, ")");
    }

    public static String joinWithSlash(Context ctx, String firstString, String secondString) {
        if (TextUtils.isEmpty(firstString)) {
            if (TextUtils.isEmpty(secondString)) {
                return "";
            }
            return secondString;
        }
        if (TextUtils.isEmpty(secondString)) {
            return firstString;
        }
        return firstString + getSlashSymbol(ctx) + secondString;
    }

    public static String joinWithDashes(Context ctx, String firstString, String secondString) {
        if (TextUtils.isEmpty(firstString)) {
            if (TextUtils.isEmpty(secondString)) {
                return "";
            }
            return secondString;
        }
        if (TextUtils.isEmpty(secondString)) {
            return firstString;
        }
        return firstString + " " + getDashesSymbol(ctx) + " " + secondString;
    }

    public static String getSlashSymbol(Context ctx) {
        return getStringOrDefault(ctx, R.string.symbol_slash, "/");
    }

    public static String getPercentageSymbol(Context ctx) {
        return getStringOrDefault(ctx, R.string.symbol_percentage, "%");
    }

    public static String getDashesSymbol(Context ctx) {
        return getStringOrDefault(ctx, R.string.symbol_dashes, "-");
    }
}
