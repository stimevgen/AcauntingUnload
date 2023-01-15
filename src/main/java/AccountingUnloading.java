import database.DataBase;
import services.ExportToDBF;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;


public class AccountingUnloading {
    public static void main(String[] arg) throws IOException {
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
        String beginDate = LocalDate.now().minusDays(1).minusDays(Integer.parseInt(property.getProperty("minusDay"))).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        System.out.println("Unload data for a period " + LocalDate.now().minusDays(1).minusDays(Integer.parseInt(property.getProperty("minusDay"))) + " - " + LocalDate.now().minusDays(1));
        if (dataBase.isConnection()) {
            ExportToDBF exportToDBF = new ExportToDBF();
            for (String name : dataBase.getDocumentName()) {
                exportToDBF.create(dataBase.getData(beginDate, endDate, 0, name), property.getProperty("outPath") + "\\" + name);
            }
            exportToDBF.create(dataBase.getCashOrder(beginDate, endDate), property.getProperty("outPath") + "\\CASH_ORDER");
            System.out.println("Done");
        }
    }
}
