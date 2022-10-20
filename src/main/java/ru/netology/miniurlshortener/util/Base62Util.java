package ru.netology.miniurlshortener.util;

import java.math.BigInteger;

public class Base62Util {

    private static final BigInteger BASE_BIG_INTEGER = BigInteger.valueOf(62);

    public static String encodeBigDecimal(BigInteger value) {
        StringBuilder sb = new StringBuilder();
        String base62Chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        while (value.compareTo(BigInteger.ZERO) > 0) {
            sb.append(base62Chars.charAt(value.mod(BASE_BIG_INTEGER).intValue()));
            value = value.divide(BASE_BIG_INTEGER);
        }
        return sb.reverse().toString();
    }

    public static BigInteger decodeString(String value) {
        BigInteger bigInteger = BigInteger.valueOf(0);
        BigInteger baseBigInteger = BigInteger.valueOf(62);
        for (int i = 0; i < value.length(); i++) {
            if ('a' <= value.charAt(i) && value.charAt(i) <= 'z') {
                bigInteger = bigInteger.multiply(baseBigInteger).add(BigInteger.valueOf(value.charAt(i) - 'a'));
            }
            if ('A' <= value.charAt(i) && value.charAt(i) <= 'Z') {
                bigInteger = bigInteger.multiply(baseBigInteger).add(BigInteger.valueOf(value.charAt(i) - 'A' + 26));
            }
            if ('0' <= value.charAt(i) && value.charAt(i) <= '9') {
                bigInteger = bigInteger.multiply(baseBigInteger).add(BigInteger.valueOf(value.charAt(i) - '0' + 52));
            }
        }
        return bigInteger;
    }
}
