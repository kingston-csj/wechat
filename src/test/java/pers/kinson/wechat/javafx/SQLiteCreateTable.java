package pers.kinson.wechat.javafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteCreateTable {
    public static void main(String[] args) {
        // SQLite数据库文件路径
        String dbFile = "database.db";

        try {
            // 加载SQLite JDBC驱动
            Class.forName("org.sqlite.JDBC");

            // 建立连接
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile)) {
                // 创建Statement
                try (Statement statement = connection.createStatement()) {
                    // 创建表
                    String sql = "CREATE TABLE IF NOT EXISTS my_table (" +
                            "id INTEGER PRIMARY KEY, " +
                            "name TEXT NOT NULL, " +
                            "age INTEGER" +
                            ")";
                    statement.execute(sql);
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL error!");
            e.printStackTrace();
        }
    }
}