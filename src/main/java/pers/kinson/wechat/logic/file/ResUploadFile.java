package pers.kinson.wechat.logic.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class ResUploadFile {

    private String id;

    private String url;

    private long time;

    private String name;

}
