package set.work.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hugh on 2016/11/17.
 */
public class StringUtil {
    public static String PATTERN_NUM = "[+-]?[1-9]+[0-9]*(\\.[0-9]+)?";
    public static String PATTERN_LETTER = "[a-zA-Z]+";
    // 中日韩文字 ^[\u2E80-\u9FFF]*$
    public static String PATTERN_CHINESE = "[\u4e00-\u9fa5]";

    public static String[][] DEFAULT_REPLACE_ARRAY = new String[][]{{"\\\\", "\\"}, {"\\｜", "|"}};

    /**
     * 是否是空字符串
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 是否包含中文字
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile(PATTERN_CHINESE);
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }


    /**
     * 比较字符串
     * @param str1
     * @param str2
     * @return
     */
    public static int compareString(String str1, String str2) {
        boolean str1Empty = false;
        if(str1 == null || str1.equals("")) {
            str1Empty = true;
        }
        boolean str2Empty = false;
        if(str2 == null || str2.equals("")) {
            str2Empty = true;
        }

        if (str1Empty && str2Empty) {
            return 0;
        }

        if (!str1Empty && str2Empty) {
            return 1;
        }

        if (str1Empty && !str2Empty) {
            return -1;
        }
        int len1 = str1.length();
        int len2 = str2.length();

        if(len1 > len2) {
            return 1;
        }

        if(len1 < len2) {
            return -1;
        }

        return str1.compareTo(str2);
    }

    /**
     * target 是否在arrayStr 数组中
     * @param arrayStr
     * @param target
     * @param regualStr
     * @return
     */
    public static boolean targetInArray(String arrayStr ,String target, String regualStr) {
        String[] array = arrayStr.split(regualStr);
        for (int i = 0 ; i < array.length; i++ ) {
            if (array[i].equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * 转换字符串
     *
     * DEFAULT_REPLACE_ARRAY 按照其装换字符串
     * @param org
     * @return
     */
    public static String convertString(String org) {
        String[][] replaceMap = DEFAULT_REPLACE_ARRAY;
        String remainString = "";
        char[] orgChar = org.toCharArray();
//        System.out.println("" + orgChar.length);
        boolean isConvert = false;
        boolean isFound = false;
        String foundReplaceStr = "";
        String tempStr = "";
        for (int i = 0; i < orgChar.length; i++) {
//            System.out.println("" + orgChar[i]);
            boolean isSubConvert = false;
            for (int j = 0; j < replaceMap.length; j++) {
                String convertStr = replaceMap[j][0];
                if (convertStr.startsWith(tempStr + orgChar[i])) {// 找到了
                    isSubConvert = true;
                    if (convertStr.equals(tempStr + orgChar[i])) {
                        isFound = true;
                    } else { // 继续进行查找替换
                        tempStr = tempStr + orgChar[i];
                    }
                }
                if (isFound) {
                    foundReplaceStr = replaceMap[j][1];
                    break;
                } else {
                    if (isSubConvert) {
                        break;
                    }
                }
            }

            if (!isSubConvert) {
                if (isConvert) {
                    remainString = remainString + tempStr;
                }
                isConvert = false;
                tempStr = "";
            } else {
                isConvert = true;
            }

            if (isFound) {
                remainString = remainString + foundReplaceStr;
                //TODO 重置
                isFound = false;
                isConvert = false;
                tempStr = "";
                foundReplaceStr = "";
            } else {
                if (!isConvert) {
                    isFound = false;
                    remainString = remainString + orgChar[i];
                }
            }
        }
        return remainString;
    }

    public static void main(String[] args) throws Exception {
//		String strSrc = "\\|A\\\\|B\\\\｜|C\\\\\\｜";
//		String strSrc = "\\\\\\\\\\｜\\｜\\｜\\\\\\｜\\｜\\｜\\\\\\｜";
        String strSrc = "\\\\\\\\\\｜\\｜\\｜\\\\\\｜\\｜\\｜\\\\｜";
        System.out.println(convertString(strSrc));
    }
}
