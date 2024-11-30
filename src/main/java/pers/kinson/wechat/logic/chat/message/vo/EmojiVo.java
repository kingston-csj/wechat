package pers.kinson.wechat.logic.chat.message.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmojiVo {

    private String label;

    private String url;

    /**
     * 把图片缓存起来,避免外网网络加载慢
     */
    @JsonIgnore
    private Image image;

}