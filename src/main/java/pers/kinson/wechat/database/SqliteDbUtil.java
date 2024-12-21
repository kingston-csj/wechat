package pers.kinson.wechat.database;

import jforgame.commons.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.struct.Resource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Slf4j
public class SqliteDbUtil {


    private static final String INSERT_EMOJI_SQL = "INSERT INTO resource (label,type, url) VALUES (?,?,?)";

    private static final String URL = "jdbc:sqlite:local.db";

    public static Connection getConnection() throws SQLException {
        // 加载SQLite JDBC驱动（在较新版本的Java和JDBC中可能不是必需的，但保险起见可以加上）
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        return DriverManager.getConnection(URL);
    }

    public static void createTable(String sql) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            QueryRunner queryRunner = new QueryRunner();
            queryRunner.update(connection, sql);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            throw e;
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }

    private static final String INSERT_MESSAGE_SQL = "INSERT INTO message (id, content, channel, receiver, sender, date ,type) VALUES (?,?,?,?,?,?,?)";

    public static void insertMessage(long id, String content, byte channel, long receiver, long sender, long date, byte type) {
        Connection connection = null;
        try {
            connection = getConnection();
            QueryRunner queryRunner = new QueryRunner();
            queryRunner.update(connection, INSERT_MESSAGE_SQL, id, content, channel, receiver, sender, date
                    , type);
        } catch (SQLException e) {
            System.err.println("Error inserting data: " + e.getMessage());
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }

    public static void clearExpiredMessage() {
        long currentTimestamp = System.currentTimeMillis();
        // 计算1天前的时间戳，作为过期时间的边界
        long expirationTimestamp = currentTimestamp - TimeUtil.MILLIS_PER_DAY;
        String sql = "DELETE FROM message WHERE date <?";
        Connection connection = null;
        try {
            connection = getConnection();
            QueryRunner queryRunner = new QueryRunner();
            int affectedRows = queryRunner.update(connection, sql, expirationTimestamp);
            if (affectedRows > 0) {
                log.info("删除{}条过期消息", affectedRows);
            }
        } catch (SQLException e) {
            System.err.println("Error querying messages: " + e.getMessage());
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }

    public static List<ChatMessage> queryPrivateMessages(long userId, long friendId, long maxSeq) {
        String sql = "SELECT * FROM message WHERE  ((sender = {0} and receiver = {1}) or  (sender = {1} and receiver = {0}))  AND id < {2} ORDER BY id DESC  LIMIT 10;";
        sql = sql.replace("{0}", userId + "");
        sql = sql.replace("{1}", friendId + "");
        sql = sql.replace("{2}", maxSeq + "");
        Connection connection = null;
        try {
            connection = getConnection();
            QueryRunner queryRunner = new QueryRunner();
            ResultSetHandler<List<ChatMessage>> resultSetHandler = new BeanListHandler<>(ChatMessage.class);
            return queryRunner.query(connection, sql, resultSetHandler);
        } catch (SQLException e) {
            System.err.println("Error querying messages: " + e.getMessage());
            return null;
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }

    public static List<Resource> queryEmoijResource() {
        String sql = "SELECT * FROM resource;";
        Connection connection = null;
        try {
            connection = getConnection();
            QueryRunner queryRunner = new QueryRunner();
            ResultSetHandler<List<Resource>> resultSetHandler = new BeanListHandler<>(Resource.class);
            return queryRunner.query(connection, sql, resultSetHandler);
        } catch (SQLException e) {
            System.err.println("Error querying messages: " + e.getMessage());
            return null;
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }

    public static void insertFace(String label, String url) {
        Connection connection = null;
        try {
            connection = getConnection();
            QueryRunner queryRunner = new QueryRunner();
            queryRunner.update(connection, INSERT_EMOJI_SQL, label, "emoji", url);
        } catch (SQLException e) {
            System.err.println("Error inserting data: " + e.getMessage());
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }
}
