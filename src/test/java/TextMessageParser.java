import pers.kinson.wechat.logic.chat.struct.ContentElemNode;
import pers.kinson.wechat.logic.chat.struct.EmojiElemNode;
import pers.kinson.wechat.logic.chat.struct.TextElemNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextMessageParser {

    public static void main(String[] args) {
        String message = "[微笑][微笑]";

        List<ContentElemNode> parsedList = parseMessage(message);

        System.out.println("解析结果:");
        for (ContentElemNode item : parsedList) {
            System.out.println(item);
        }
    }

    public static List<ContentElemNode> parseMessage(String message) {
        List<ContentElemNode> resultList = new ArrayList<>();

        Pattern pattern = Pattern.compile("([^\\[]+)(?=\\[)|\\[([^]]+)]|([^\\[]+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            if (matcher.group(1)!= null) {
                resultList.add(new TextElemNode(matcher.group(1)));
            } else if (matcher.group(2)!= null) {
                resultList.add(new EmojiElemNode(matcher.group(2)));
            } else if (matcher.group(3)!= null) {
                resultList.add(new TextElemNode(matcher.group(3)));
            }
        }
        return resultList;
    }

}