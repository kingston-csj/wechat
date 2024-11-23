package pers.kinson.wechat.ui.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgressMonitor {

    /**
     * 当前进度
     */
    private long progress;

    /**
     * 总大小
     */
    private long maximum;

    public void updateTransferred(long changed) {
        this.progress += changed;
    }

}