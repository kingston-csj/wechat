package pers.kinson.wechat.util;

import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.logic.chat.message.vo.EmojiVo;
import pers.kinson.wechat.logic.chat.struct.ContentElemNode;
import pers.kinson.wechat.logic.chat.struct.EmojiElemNode;
import pers.kinson.wechat.logic.chat.struct.TextElemNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageTextUiEditor {

    public static List<ContentElemNode> parseMessage(String message) {
        List<ContentElemNode> resultList = new ArrayList<>();
        // 111[好色]222[惊讶]
        // 111
        // [微笑][微笑]
        Pattern pattern = Pattern.compile("([^\\[]+)(?=\\[)|\\[([^]]+)]|([^\\[]+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                resultList.add(new TextElemNode(matcher.group(1)));
            } else if (matcher.group(2) != null) {
                resultList.add(new EmojiElemNode(getEmojiUrl(matcher.group(2))));
            } else if (matcher.group(3) != null) {
                resultList.add(new TextElemNode(matcher.group(3)));
            }
        }

        return resultList;
    }

    private static String getEmojiUrl(String label) {
        EmojiVo target = Context.chatManager.getEmojiVoMap().get(label);
        return target != null ? target.getUrl() : "";
    }
}
