package org.alex.db.db;

import javafx.beans.property.SimpleStringProperty;
import org.alex.db.entity.ConnItem;
import org.alex.db.entity.TableField;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author alex
 * @version 1.0.0
 * @since 2023-06-23 11:13
 */
public class DbConnUtils {

    private static Connection connection;
    private static Statement statement;
    public static String USE_DATABASE;

    public static void generateConn(ConnItem connItem, String database) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }

        String url = String.format("jdbc:mysql://%s:%s/%s",
                connItem.getConnItemDetail().getConnIp(),
                connItem.getConnItemDetail().getPort(),
                database == null ? "" : database);
        try {
            connection = DriverManager.getConnection(url,
                    connItem.getConnItemDetail().getUsername(),
                    connItem.getConnItemDetail().getPassword());
            statement = connection.createStatement();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public static List<Map<String, String>> doQuery(String sqlStatementString, List<TableField> tableFieldList) {

        var resultList = new ArrayList<Map<String, String>>();
        try {
            ResultSet resultSet = statement.executeQuery(sqlStatementString);

            while (resultSet.next()) {
                var resultMap = new HashMap<String, String>();
                for (var field : tableFieldList) {
                    resultMap.put(field.getColumnName(), resultSet.getString(field.getColumnName()));
                }
                resultList.add(resultMap);
            }
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return resultList;
    }

    //获取当前连接下的所有数据库名称
    public static List<String> getDatabases() {
        List<String> dbNameList = new ArrayList<>();
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getCatalogs();
            while (resultSet.next()) {
                String dbName = resultSet.getString("TABLE_CAT");
                dbNameList.add(dbName);
            }
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return dbNameList;
    }

    //获取当前连接下的所有数据表数据
    public static List<String> getTables(String dbName) {
        List<String> tableNameList = new ArrayList<>();
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            System.out.println(connection.getCatalog());
            ResultSet resultSet = databaseMetaData.getTables(dbName, null, null, null);
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                tableNameList.add(tableName);
            }
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return tableNameList;
    }

    //获取当前表的所有字段数据
    public static List<TableField> getFields(String tableName) {
        List<TableField> tableFieldsList = new ArrayList<>();
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getColumns(null, null, tableName, "%");
            while (resultSet.next()) {
                var fieldItem = new TableField()
                        .setColumnName(resultSet.getString("COLUMN_NAME"))
                        .setRemarks(resultSet.getString("REMARKS"))
                        .setTypeName(resultSet.getString("TYPE_NAME"));
                tableFieldsList.add(fieldItem);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tableFieldsList;
    }

}
