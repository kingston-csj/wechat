package pers.kinson.wechat.ui.controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

    private final DoubleProperty progressProperty = new SimpleDoubleProperty(this, "progress", -1);

    public void updateTransferred(long changed) {
        this.progress += changed;
        double rate = (double) progress / maximum;
        progressProperty.set(rate);
    }

}