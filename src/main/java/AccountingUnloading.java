import database.DataBase;
import services.ExportToDBF;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;


public class AccountingUnloading {
    public static void main(String[] arg) throws IOException, ParseException {
        if (arg.length != 3) {
            System.out.println("Parameters not passed (DateBenin DateEnd Operation)");
            return;
        }
        /*Начало блока работы с конфигурационным файлом*/
        String path = System.getProperty("user.dir"); // Получаем текущий каталог
        File file = new File(path + "\\config.properties");
        Properties property = new Properties();

        if (!file.exists()) { // Если файл отсутвует создаем и заполняем настройками по умолчанию
            ClassLoader classLoader = AccountingUnloading.class.getClassLoader();
            URL resource = classLoader.getResource("config.properties");
            assert resource != null;
            property.load(resource.openStream());
            property.store(new FileOutputStream(path + "\\config.properties"), null);
        }
        property.load(new FileInputStream(path + "\\config.properties")); // Считываем конфигурационный файл и загружаем его
        /*Конец блока работы с конфигурационным файлом */

        DataBase dataBase = new DataBase(property.getProperty("db.connectionString"));
        String beginDate = LocalDate.now().minusDays(Long.parseLong(arg[0])).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endDate = LocalDate.now().minusDays(Long.parseLong(arg[1])).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nameOperation = arg[2];
        System.out.println("Unload data for a period " + beginDate + " - " + endDate);
        if (dataBase.isConnection()) {
            ExportToDBF exportToDBF = new ExportToDBF();
            if (Objects.equals(nameOperation, "")) {
                for (String name : dataBase.getDocumentName()) {
                    exportToDBF.create(dataBase.getData(beginDate, endDate, 0, name), property.getProperty("outPath"), name);
                }
                exportToDBF.create(dataBase.getCashOrder(beginDate, endDate), property.getProperty("outPath"), "CASH_ORDER");
            } else {
                if (nameOperation.equals("CASH_ORDER")) {
                    exportToDBF.create(dataBase.getCashOrder(beginDate, endDate), property.getProperty("outPath"), "CASH_ORDER");
                } else {
                    exportToDBF.create(dataBase.getData(beginDate, endDate, 0, nameOperation), property.getProperty("outPath"), nameOperation);
                }
            }
            System.out.println("Done");
        }
    }
}
