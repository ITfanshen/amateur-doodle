package YouChat.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    /**
     * 获取当前系统时间
     *
     * @return 当前系统时间的字符串表示，格式为 yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentTime() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 创建日期时间格式化对象，指定格式为 "yyyy-MM-dd HH:mm:ss"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 使用格式化对象将当前时间格式化为指定格式的字符串
        return now.format(formatter);
    }

}

