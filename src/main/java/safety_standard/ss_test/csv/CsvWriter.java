package safety_standard.ss_test.csv;

import com.opencsv.CSVWriter;
import safety_standard.ss_test.dto.Category;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvWriter {

    public static void writeCsv(List<Category> categoryList, String filePath, boolean withAddition) {

        try (FileOutputStream fos = new FileOutputStream(filePath);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw, ';', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

            // Добавление этих байт в начало файла указывает Excel, что файл закодирован в UTF-8
            // Теперь можно открывать файлик через Excel
            fos.write(0xEF);
            fos.write(0xBB);
            fos.write(0xBF);

            // Заголовок
            String[] header = {"Имя категории", "Количество документов"};
            String[] headerWithAddition = {"Имя категории", "Количество документов", "Добавлено", "Удалено"};

            // Записываем данные
            if(withAddition) {
                writer.writeNext(headerWithAddition);

                for (Category str : categoryList) {
                    String[] data = {
                            str.getNameCategory(),
                            String.valueOf(str.getDocCount()),
                            str.getAdded()==null ? "": String.valueOf(str.getAdded()),
                            str.getDeleted()==null? "": String.valueOf(str.getDeleted())
                    };
                    writer.writeNext(data);
                }
            }
            else {
                writer.writeNext(header);

                for (Category str : categoryList) {
                    String[] data = {
                            str.getNameCategory(),
                            String.valueOf(str.getDocCount())
                    };
                    writer.writeNext(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
