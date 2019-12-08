package com.syl.util;


/**
 * @author syl
 * @create 2018-06-22 15:07
 **/
public class StringUtils {

    /**
     * 第一个字符大写
     * @param string
     * @return
     */
    public static String firstCharUpper(String string){
        return String.valueOf(string.charAt(0)).toUpperCase() + string.substring(1);
    }

    /**
     * 第一个字符小写
     * @param string
     * @return
     */
    public static String firstCharLower(String string){
        return String.valueOf(string.charAt(0)).toLowerCase() + string.substring(1);
    }

    /**
     *
     * convert string from slash style to camel style, such as ab_cd  or ab-cd will convert to AbCd
     *
     * @param str 要转换的字符
     * @param initial 是否首字母大写
     * @return
     */
    public static String underlineToCamel(String str,boolean initial) {
        if (str != null) {
            StringBuilder sb = new StringBuilder();
            if(initial)
                sb.append(String.valueOf(str.charAt(0)).toUpperCase());
            else
                sb.append(String.valueOf(str.charAt(0)).toLowerCase());
            char[] param = str.toCharArray();
            for (int i = 1; i < str.length(); i++) {
                char c = str.charAt(i);
                if ('_' == c) {
                    if (++i < str.length()) {
                        sb.append(Character.toUpperCase(param[i]));
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * Does the string have a lowercase character?
     *
     * @param s the string to test.
     * @return true if the string has a lowercase character, false if not.
     */
    public static boolean hasLowerCaseChar(String s) {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Does the string have an uppercase character?
     *
     * @param s the string to test.
     * @return true if the string has an uppercase character, false if not.
     */
    public static boolean hasUpperCaseChar(String s) {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param s
     * @param token
     * @return
     */
    public static String after(String s, String token) {
        int i = s.indexOf(token);
        if (i == -1) {
            return null;
        }

        return s.substring(i + token.length());
    }

}
