package pers.kinson.wechat.logic.file;

import javafx.scene.input.ClipboardContent;
import jforgame.commons.Pair;
import lombok.extern.slf4j.Slf4j;
import pers.kinson.wechat.util.IdFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
public class ClipboardUtil {

    public static final byte TYPE_STRING = 1;
    public static final byte TYPE_IMAGE = 2;
    public static final byte TYPE_FILE = 3;


    public static Pair<Byte, Object> getFromClipboard() {
        try {
            // 获取系统剪贴板
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            DataFlavor stringFlavor = new DataFlavor("text/plain;representationclass=java.lang.String");
            if (contents != null) {
                DataFlavor[] flavors = contents.getTransferDataFlavors();
                for (DataFlavor flavor : flavors) {
                    // 处理普通文本
                    if (flavor.equals(stringFlavor)) {
                        if (flavor.equals(stringFlavor)) {
                            Object data = contents.getTransferData(flavor);
                            if (data instanceof String) {
                                String text = (String) data;
                                return new Pair<>(TYPE_STRING, text);
                            } else if (data instanceof InputStream) {
                                InputStream inputStream = (InputStream) data;
                                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                StringBuilder stringBuilder = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    stringBuilder.append(line);
                                }
                                return new Pair<>(TYPE_STRING, stringBuilder.toString());
                            }
                        }
                    }

                    // java.awt.datatransfer.DataFlavor[mimetype=image/x-java-image;representationclass=java.awt.Image]
                    if (flavor.getMimeType().contains("image/x-java-image")) {
                        Image awtImage = (Image) contents.getTransferData(DataFlavor.imageFlavor);
                        // 创建一个字节数组输出流
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        // 将java.awt.Image转换为PNG格式的字节数组并写入输出流
                        ImageIO.write((RenderedImage) awtImage, "png", baos);
                        // 从字节数组输出流获取字节数组
                        byte[] imageBytes = baos.toByteArray();
                        // 使用字节数组创建BufferedImage
                        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                        // 生成一个临时文件路径，你也可以根据需求指定具体的保存路径和文件名
                        File outputFile = File.createTempFile("clipboard_image" + IdFactory.nextUUId(), ".png");
                        // 将BufferedImage保存为文件
                        ImageIO.write(bufferedImage, "png", outputFile);
                        // 关闭字节数组输出流
                        baos.close();
                        return new Pair<>(TYPE_IMAGE, outputFile);
                    }

                    if (flavor.getMimeType().contains("application/x-java-file-list")) {
                        // 尝试从文件列表格式中获取图像文件路径
                        Object data = contents.getTransferData(flavor);
                        if (data instanceof java.util.List) {
                            java.util.List<?> fileList = (List<?>) data;
                            for (Object item : fileList) {
                                if (item instanceof File) {
                                    File file = (File) item;
                                    if (file.isFile()) {
                                        if (isImageFile(file)) {
                                            return new Pair<>(TYPE_IMAGE, file);
                                        } else {
                                            return new Pair<>(TYPE_FILE, file);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return new Pair<>((byte) 0, null);
    }

    private static boolean isImageFile(File file) {
        String fileName = file.getName();
        String[] imageExtensions = {".png", ".jpg", ".jpeg", ".gif", ".bmp"};
        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 将内容复制到剪贴板
     */
    public static void copyToClipboard(Object content) {
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        if (content instanceof String) {
            clipboardContent.putString((String) content); // 将内容放入剪贴板
        } else if (content instanceof javafx.scene.image.Image) {
            clipboardContent.putImage((javafx.scene.image.Image) content);
        }
        clipboard.setContent(clipboardContent);
    }
}
