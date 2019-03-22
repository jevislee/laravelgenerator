package com.utility.laravelgenerator.util;

public class CamelCaseUtil {

    /**
     * 转换为下划线 
     *
     * @param camelCaseName
     * @return
     */
    public static String underscoreName(String camelCaseName) {
        StringBuilder result = new StringBuilder();
        if (camelCaseName != null && camelCaseName.length() > 0) {
            result.append(camelCaseName.substring(0, 1).toLowerCase());
            for (int i = 1; i < camelCaseName.length(); i++) {
                char ch = camelCaseName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    result.append("_");
                    result.append(Character.toLowerCase(ch));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }

    /**
     * 转换为驼峰 
     *
     * @param underscoreName
     * @return
     */
    public static String camelCaseName(String underscoreName, boolean toUpperCaseFirstLetter) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.length() > 0) {
            boolean flag = false;

            if(toUpperCaseFirstLetter) {
                result.append(Character.toUpperCase(underscoreName.charAt(0)));
            } else {
                result.append(underscoreName.charAt(0));
            }
            
            for (int i = 1; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ('_' == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }
}
