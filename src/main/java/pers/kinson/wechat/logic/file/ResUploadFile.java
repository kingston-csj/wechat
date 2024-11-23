package pers.kinson.wechat.logic.file;

import lombok.Data;

@Data
public class ResUploadFile {

    private String id;

    /**
     * 真正的cdn地址
     */
    private String cdnUrl;

    /**
     * 上传预签名地址
     */
    private String presignedUrl;

    private long time;

    private String name;

}