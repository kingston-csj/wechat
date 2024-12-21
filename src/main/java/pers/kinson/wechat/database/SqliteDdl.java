package pers.kinson.wechat.database;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqliteDdl {

    public static void createDatabase() {
        // 消息表
        String sql1 = " CREATE TABLE IF NOT EXISTS `message` (\n" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `content` TEXT NULL DEFAULT NULL,\n" +
                "  `channel` INTEGER NULL DEFAULT NULL,\n" +
                "  `receiver` INTEGER NULL DEFAULT NULL,\n" +
                "  `sender` INTEGER NULL DEFAULT NULL,\n" +
                "  `date` INTEGER NULL DEFAULT NULL,\n" +
                "  `type` INTEGER NULL DEFAULT NULL\n" +
                ")";
        try {
            SqliteDbUtil.createTable(sql1);
        } catch (Exception e) {
            log.error("", e);
        }
        // 图片表
        String sql2 = " CREATE TABLE IF NOT EXISTS `resource` (\n" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `label` TEXT NULL DEFAULT NULL,\n" +
                "  `type` TEXT NULL DEFAULT NULL,\n" +
                "  `url` INTEGER NULL DEFAULT NULL" +
                ")";
        try {
            SqliteDbUtil.createTable(sql2);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
