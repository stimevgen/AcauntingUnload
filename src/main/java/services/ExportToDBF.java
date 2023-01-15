package services;

import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class ExportToDBF {
    public void create(List<Map<String, ?>> data, String fillName) throws FileNotFoundException {
        if (data == null) {
            return;
        }
        if (data.size() == 0) {
            return;
        }
        System.out.print("Create file " + fillName + ".dbf" + "");
        Charset w1251 = Charset.forName("cp866");
        DBFWriter writer = new DBFWriter(new FileOutputStream(fillName + ".dbf"), w1251);
        DBFField[] fields = new DBFField[data.get(0).keySet().size()];
        Map<String, ?> rowsName = data.get(0);
        int key = 0;

        for (String rowName : rowsName.keySet()) {
            fields[key] = new DBFField();
            fields[key].setName(rowName);
            fields[key].setType(getType(rowsName.get(rowName)));
            fields[key].setLength(fields[key].getType().getMaxSize());
            if (fields[key].getType() == DBFDataType.FLOATING_POINT || fields[key].getType() == DBFDataType.NUMERIC) {
                fields[key].setDecimalCount(4);
            }
            key++;
        }
        writer.setFields(fields);
        for (Map<String, ?> rowMap : data) {
            writer.addRecord(rowMap.values().toArray());
        }
        writer.close();
        System.out.println(" - Successful!!!");
    }

    private DBFDataType getType(Object o) {
        if (o != null) {
            if (o.getClass().getSimpleName().equals("String")) {
                return DBFDataType.CHARACTER;
            }
            if (o.getClass().getSimpleName().equals("Timestamp")) {
                return DBFDataType.DATE;
            }
            if (o.getClass().getSimpleName().equals("BigDecimal")) {
                return DBFDataType.NUMERIC;
            }
            if (o.getClass().getSimpleName().equals("Double")) {
                return DBFDataType.FLOATING_POINT;
            }
            if (o.getClass().getSimpleName().equals("Integer")) {
                return DBFDataType.NUMERIC;
            }
        }
        return DBFDataType.CHARACTER;
    }
}
