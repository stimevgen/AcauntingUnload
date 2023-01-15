package database;

import model.Cll;

import java.sql.*;
import java.util.*;

public class DataBase {
    private final String connectionString;
    private final List<String> documentName = new ArrayList<>();

    public DataBase(String connectionString) {
        this.connectionString = connectionString;
    }

    public boolean isConnection() {
        try (Connection ignored = DriverManager.getConnection(connectionString)) {
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private List<Map<String, ?>> execSql(String sqlText) {
        List<Map<String, ?>> results = new ArrayList<>();
        Properties connInfo = new Properties();
        connInfo.put("charSet", "Windows-1251");
        try (Connection connection = DriverManager.getConnection(connectionString, connInfo);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sqlText);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columns = resultSetMetaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columns; i++) {
                    row.put(resultSetMetaData.getColumnLabel(i).toUpperCase(), resultSet.getObject(i));
                }
                results.add(row);
            }
            return results;
        } catch (SQLException e) {
            System.out.println(e.getMessage() + " <" + sqlText + ">");
        }
        return null;
    }

    public List<String> getDocumentName() {
        List<Map<String, ?>> execSql = execSql("select NAME from [OL].[1C_OPERATIONS]");
        assert execSql != null;
        for (Map<String, ?> stringMap : execSql) {
            for (Map.Entry<String, ?> entry : stringMap.entrySet()) {
                documentName.add(entry.getValue().toString());
            }
        }
        return documentName;
    }

    public List<Cll> getCll() {
        List<Cll> cllList = new ArrayList<>();
        List<Map<String, ?>> execSql = execSql("set dateformat dmy select skd_value0_01 AS Cll_unicode, skd_unicode, skd_ShortName\tfrom skd");
        assert execSql != null;
        for (Map<String, ?> stringMap : execSql) {
            cllList.add(new Cll(stringMap.get("CLL_UNICODE").toString(), stringMap.get("SKD_UNICODE").toString(), stringMap.get("SKD_SHORTNAME").toString()));
        }
        return cllList;
    }

    public List<Map<String, ?>> getData(String dateBegin, String dateEnd, int cllUnicode, String name) {
        List<Map<String, ?>> execSql = execSql("SET ANSI_WARNINGS OFF set dateformat dmy select * from [OL].[UNLOAD_DATA_FOR_1C]('" + dateBegin + "','" + dateEnd + "','" + name + "'," + cllUnicode + ",0,0,0)");
        assert execSql != null;
        return execSql;
    }

    public List<Map<String, ?>> getCashOrder(String dateBegin, String dateEnd) {
        List<Map<String, ?>> execSql = execSql("SELECT * FROM OL.UNLOAD_DATA_FOR_1C_CASH_ORDER_ALL('" + dateBegin + "', '" + dateEnd + "')ORDER BY\tDOC_DATE, DOC_NUM");
        assert execSql != null;
        return execSql;
    }

}
