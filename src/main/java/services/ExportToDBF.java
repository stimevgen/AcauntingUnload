package services;

import DTO.AnswerDTO;
import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class ExportToDBF {
    public void create(AnswerDTO data, String path, String fillName) throws FileNotFoundException {
        if (data == null) {
            return;
        }
        if (data.getRow().size() == 0) {
            return;
        }
        File dir = new File(path + "\\" + fillName);
        if (dir.mkdir()) {
            System.out.println("Create dir " + dir.getPath());
        }
        System.out.print("Create file " + dir.getPath() + "\\" + fillName + ".dbf" + "");
        Charset w1251 = Charset.forName("cp866");
        DBFWriter writer = new DBFWriter(new FileOutputStream(dir.getPath() + "\\" + fillName + ".dbf"), w1251);
        DBFField[] fields = new DBFField[data.getColumn().size()];
        List<Map<Class<?>, String>> rowsName = data.getColumn();
        int key = 0;
        for (Map<Class<?>, String> rowName : rowsName) {
            fields[key] = new DBFField();
            fields[key].setName((String) rowName.values().toArray()[0]);
            fields[key].setType(getType((Class<?>) rowName.keySet().toArray()[0]));
            fields[key].setLength(fields[key].getType().getMaxSize());
            if (fields[key].getType() == DBFDataType.FLOATING_POINT || fields[key].getType() == DBFDataType.NUMERIC) {
                fields[key].setDecimalCount(2);
            }
            key++;
        }
        writer.setFields(fields);
        for (List<?> row : data.getRow()) {
            writer.addRecord(row.toArray());
        }
        writer.close();
        System.out.println(" - Successful!!!");
    }

    private DBFDataType getType(Class<?> o) {
        if (o != null) {
            if (o.getSimpleName().equals("String")) {
                return DBFDataType.CHARACTER;
            }
            if (o.getSimpleName().equals("Timestamp")) {
                return DBFDataType.DATE;
            }
            if (o.getSimpleName().equals("BigDecimal")) {
                return DBFDataType.NUMERIC;
            }
            if (o.getSimpleName().equals("Double")) {
                return DBFDataType.FLOATING_POINT;
            }
            if (o.getSimpleName().equals("Integer")) {
                return DBFDataType.NUMERIC;
            }
        }
        return DBFDataType.CHARACTER;
    }
}
