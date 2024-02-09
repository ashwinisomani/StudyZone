package in.toralabs.library.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String BASE_IP_URL = "http://151.106.112.5/";
    public static final String BASE_URL = "https://toralabs.tech/";
    public static final String INVOICE_MESSAGE = "invoice_msg";
    public static final String MARKETING_MESSAGE_1 = "marketing_message_1";
    public static final String MARKETING_MESSAGE_2 = "marketing_message_2";
    public static final String MARKETING_MESSAGE_3 = "marketing_msg_3";
    public static final String MARKETING_MESSAGE_4 = "marketing_msg_4";

    public static final String MESSAGES_URL = "https://graph.facebook.com/v16.0/116630038102241/messages";

    public static List<Integer> getIntegerListFromStringArray(String[] s) {
        ArrayList<Integer> tempIntList = new ArrayList<>();
        for (int i = 0; i < s.length; i++) {
            tempIntList.add(Integer.parseInt(s[i]));
        }
        return tempIntList;
    }

    public static Timestamp getCurrentTimestampInIST() {
        return Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
    }

    public static String generateToken() {
        String[] tokenArray = "69 65 65 67 116 82 54 89 84 79 78 111 66 65 66 85 105 97 89 77 55 107 119 70 74 82 68 65 67 104 101 108 99 77 113 69 90 67 49 90 67 98 118 54 56 98 56 105 120 49 89 79 90 65 103 70 85 78 78 77 57 88 88 116 53 114 111 80 106 65 55 66 106 115 112 81 117 115 55 54 53 104 103 89 84 120 57 104 66 77 103 70 72 54 110 72 108 74 90 67 87 107 86 120 122 66 50 110 98 75 114 108 81 86 55 110 68 109 112 113 84 81 67 75 85 73 110 83 90 65 104 65 67 80 77 82 89 116 75 117 100 52 69 65 90 65 85 108 120 75 80 75 82 73 49 49 98 111 117 111 73 70 101 66 111 85 90 67 118 67 76 114 116 49 79 117 120 90 66 108 67 116 90 65 109 113 90 65 57 122 119 69 65 104 51 85 117 83 119 90 67 103 48 99 57 121 88 83 117 119 90 68 90 68".split(" ");
        StringBuilder mainToken = new StringBuilder();
        for (String token : tokenArray) {
            mainToken.append((char) Integer.parseInt(token));
        }
        return mainToken.toString();
    }
}
