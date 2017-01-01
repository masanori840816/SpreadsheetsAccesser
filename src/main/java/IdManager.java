import com.sun.istack.internal.NotNull;
import io.reactivex.Observable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by masanori on 2016/12/24.
 * Get lagest ID number from spreadsheet. and add new ID to the spreadsheet.
 */
class IdManager {

    private CellValueGetter cellValueGetter;

    IdManager(){
        cellValueGetter = new CellValueGetter();
    }
    Observable<Integer> generateNewId(@NotNull String title, @NotNull String filePath){
        return Observable.create(observer -> {
            File targetFile = new File(filePath);
            if(! targetFile.exists()
                    || ! targetFile.isFile()){
                // ファイルが見つからない、またはファイルでないパスが入力されていればError.
                observer.onError(new Throwable("不正なパスが入力されています。"));
            }
            FileInputStream fileStream = new FileInputStream(filePath);
            Workbook workbook = WorkbookFactory.create(fileStream);
            if(workbook == null){
                observer.onError(new Throwable("Workbookの取得に失敗しました。"));
            }
            // とりあえずSheet名は固定.
            Sheet sheet = workbook.getSheet("Sheet1");
            if(sheet == null){
                observer.onError(new Throwable("Sheetの取得に失敗しました。"));
            }
            List<Name> allCellNameList = cellValueGetter.getTargetCellValueList(workbook, sheet.getSheetName());

            int idColumnNum = cellValueGetter.getTargetCellColumnNum(allCellNameList, "ID");
            int titleColumnNum = cellValueGetter.getTargetCellColumnNum(allCellNameList, "TITLE");
            int lastUpdateDateColumnNum = cellValueGetter.getTargetCellColumnNum(allCellNameList, "LASTUPDATEDATE");

            int lastRowNum = sheet.getLastRowNum();
            int newId = 1;
            if(lastRowNum > 0){
                Row lastRow = sheet.getRow(lastRowNum);
                if(lastRow != null){
                    Cell lastCell = lastRow.getCell(idColumnNum);

                    float lastId = NumParser.TryParseFloat(lastCell.toString());
                    newId = (int)lastId + 1;
                }
            }
            else {
                // IDが未登録の場合は1を付与する.
                newId = 1;
            }
            Row newRow = sheet.createRow(lastRowNum + 1);

            if(newRow == null){
                observer.onError(new Throwable("行の追加に失敗しました。"));
            }
            else{
                newRow.createCell(idColumnNum).setCellValue(newId);
                newRow.createCell(titleColumnNum).setCellValue(title);

                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                newRow.createCell(lastUpdateDateColumnNum).setCellValue(currentDate.format(formatter));

                FileOutputStream outputStream = new FileOutputStream(filePath);

                workbook.write(outputStream);
                outputStream.close();
            }
            workbook.close();
            fileStream.close();
            // 発行したIDを返す.
            observer.onNext(newId);
            observer.onComplete();
        });
    }
}
