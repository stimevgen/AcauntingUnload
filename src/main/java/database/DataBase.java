package database;

import DTO.AnswerDTO;
import model.Cll;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

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

    private AnswerDTO execSql(String sqlText) {
        Properties connInfo = new Properties();
        connInfo.put("charSet", "Windows-1251");
        try (Connection connection = DriverManager.getConnection(connectionString, connInfo);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sqlText);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            List<Map<Class<?>, String>> columns = new ArrayList<>();
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                Map<Class<?>, String> coll = new LinkedHashMap<>();
                coll.put(sqlTypeToClass(resultSetMetaData.getColumnType(i)), resultSetMetaData.getColumnLabel(i).toUpperCase());
                columns.add(coll);
            }
            List<List<?>> rows = new ArrayList<>();
            while (resultSet.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    Object data = resultSet.getObject(i);
                    row.add(data);
                }
                rows.add(row);
            }
            return new AnswerDTO(columns, rows);
        } catch (SQLException e) {
            System.out.println(e.getMessage() + " <" + sqlText + ">");
        }
        return null;
    }

    public List<String> getDocumentName() {
        AnswerDTO execSql = execSql("select NAME from [OL].[1C_OPERATIONS]");
        assert execSql != null;
        for (List<?> row : execSql.getRow()) {
            documentName.add(row.get(0).toString());
        }
        return documentName;
    }

    public List<Cll> getCll() {
        List<Cll> cllList = new ArrayList<>();
        AnswerDTO execSql = execSql("set dateformat dmy select skd_value0_01 AS Cll_unicode, skd_unicode, skd_ShortName\tfrom skd");
        assert execSql != null;
        for (List<?> row : execSql.getRow()) {
            cllList.add(new Cll(row.get(0).toString(), row.get(1).toString(), row.get(2).toString()));
        }
        return cllList;
    }

    public AnswerDTO getData(String dateBegin, String dateEnd, int cllUnicode, String name) {
        AnswerDTO execSql = execSql("SET ANSI_WARNINGS OFF set dateformat dmy select * from [OL].[UNLOAD_DATA_FOR_1C_auto]('" + dateBegin + "','" + dateEnd + "','" + name + "'," + cllUnicode + ",0,0,0) order by OPER_CODE, STORE_id, DOC_ID,DOC_DATE ");
        assert execSql != null;
        return execSql;
    }

    public AnswerDTO getCashOrder(String dateBegin, String dateEnd) {
        AnswerDTO execSql = execSql("SELECT * FROM OL.UNLOAD_DATA_FOR_1C_CASH_ORDER_ALL('" + dateBegin + "', '" + dateEnd + "')ORDER BY\tDOC_DATE, DOC_NUM");
        assert execSql != null;
        return execSql;
    }

    public static Class<?> sqlTypeToClass(int type) {
        Class<?> result = switch (type) {
            case Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR -> String.class;
            case Types.NUMERIC, Types.DECIMAL -> BigDecimal.class;
            case Types.BIT -> Boolean.class;
            case Types.TINYINT -> Byte.class;
            case Types.SMALLINT -> Short.class;
            case Types.INTEGER -> Integer.class;
            case Types.BIGINT -> Long.class;
            case Types.REAL, Types.FLOAT -> Float.class;
            case Types.DOUBLE -> Double.class;
            case Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY -> Byte[].class;
            case Types.DATE -> Date.class;
            case Types.TIME -> Time.class;
            case Types.TIMESTAMP -> Timestamp.class;
            default -> Class.class;
        };

        return result;
    }

}
