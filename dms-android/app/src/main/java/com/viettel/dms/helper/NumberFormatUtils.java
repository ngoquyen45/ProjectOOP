package com.viettel.dms.helper;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;

import static java.math.BigDecimal.ROUND_FLOOR;

@SuppressLint({"SimpleDateFormat", "DefaultLocale"})
public class NumberFormatUtils {
    private static BigDecimal ONE_BILLION = new BigDecimal(1e9);
    private static BigDecimal TEN_BILLION = new BigDecimal(1e10);
    private static BigDecimal ONE_MILLION = new BigDecimal(1e6);
    private static BigDecimal TEN_MILLION = new BigDecimal(1e7);
    private static BigDecimal ONE_HUNDRED_MILLION = new BigDecimal(1e8);

    public static String formatNumber(BigDecimal number) {

        return formatNumber(number, false);
    }

    public static String formatNumber(BigDecimal number, boolean isRound) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(0);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');

        df.setDecimalFormatSymbols(otherSymbols);

        BigDecimal tempNumber = null;
        try {
            if (isRound) {
                if (number.compareTo(ONE_BILLION) >= 0) {
                    tempNumber = number.compareTo(TEN_BILLION) >= 0 ? number.divide(ONE_BILLION, 0, ROUND_FLOOR) : number.divide(ONE_BILLION, 1, ROUND_FLOOR);
                    return df.format(tempNumber) + "B";
                } else if (number.compareTo(ONE_MILLION) >= 0) {
                    tempNumber = number.compareTo(TEN_MILLION) >= 0 ? number.divide(ONE_MILLION, 0, ROUND_FLOOR) : number.divide(ONE_MILLION, 1, ROUND_FLOOR);
                    return df.format(tempNumber) + "M";
                } else {
                    return df.format(number);
                }
            } else {
                if (number.compareTo(ONE_BILLION) >= 0) {
                    tempNumber =  number.divide(ONE_MILLION, 1, ROUND_FLOOR);
                    return df.format(tempNumber) + "M";
                }
                return df.format(number);
            }
        } catch (Exception ex) {
            Log.e("NumberFormat", "Cannot format [" + number + "] to String value");
            return null;
        }
    }

    public static String formatMoneyWithRepresent(BigDecimal number, String currencyCode) {
        String formated = formatNumber(number);
        if (currencyCode != null && "VND".equalsIgnoreCase(currencyCode.trim())) {
            return formated + getCurrencyRepresent(currencyCode);
        }
        return getCurrencyRepresent(currencyCode) + formated;
    }

    public static String getCurrencyRepresent(String currencyCode) {
        if (TextUtils.isEmpty(currencyCode) || currencyCode == null) {
            return "";
        }

        currencyCode = currencyCode.trim();

        if ("ERO".equalsIgnoreCase(currencyCode) || "EUR".equalsIgnoreCase(currencyCode)) {
            return "€";
        } else if ("VND".equalsIgnoreCase(currencyCode)) {
            return "₫";
        } else if ("GBP".equalsIgnoreCase(currencyCode)) {
            return "₤";
        } else if ("USD".equalsIgnoreCase(currencyCode)) {
            return "$";
        }

        Currency c = null;
        try {
            c = Currency.getInstance(currencyCode.toUpperCase());
            return c.getSymbol();
        } catch (Exception e) {
            Log.e("Currency", "Cannot get currency code", e);
        }

        return currencyCode;
    }
}
