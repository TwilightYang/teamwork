package utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * 验证码工具类  生成随机验证码
 */
public class CodeUtil {
    public static String getCode(){
        ArrayList<Character> list = new ArrayList<>(); // 创建一个集合 52个字符 索引0-51

        // 添加字符 a-z A-Z
        for (int i = 0; i < 26; i++) {
            list.add((char)('a' + i)); // 添加a-z
            list.add((char)('A' + i)); // 添加A-Z
        }

        // 生成随机字符
        String result = "";
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(list.size()); // 获取随机索引
            char c = list.get(index);
            result =result + c; // 添加到结果字符串
        }

        int number = random.nextInt(10); // 生成0-9的随机数
        result = result + number; // 随机数添加到结果字符串

        char[] chars = result.toCharArray(); // 将结果字符串转换为字符数组
        int index = random.nextInt(chars.length); //在字符数组中生成一个随机索引

        // 交换索引4的字符和随机索引的字符
        char temp = chars[4];
        chars[4] = chars[index];
        chars[index] = temp;

        String code = new String(chars); // 将字符数组转换为字符串

        return code;
    }
}
