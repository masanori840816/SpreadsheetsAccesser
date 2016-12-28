import com.sun.istack.internal.NotNull;
import io.reactivex.Observable;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by masanori on 2016/12/24.
 * Get lagest ID number from spreadsheet. and add new ID to the spreadsheet.
 */
public class IdManager {
    public Observable<Integer> addNewId(@NotNull String title, @NotNull String filePath){
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
            Name name = workbook.getName("ID");
            CellReference reference = new CellReference(name.getRefersToFormula());

            int lastRowNum = sheet.getLastRowNum();
            int newId = 1;
            if(lastRowNum > 0){
                Row lastRow = sheet.getRow(lastRowNum);
                if(lastRow != null){
                    Cell lastCell = lastRow.getCell(reference.getCol());
                    float lastId = NumParser.TryParseFloat(lastCell.toString());
                    newId = (int)lastId + 1;
                }
            }
            else{
                // IDが未登録の場合は1を付与する.
                newId = 1;
            }

            Row newRow = sheet.createRow(lastRowNum + 1);

            if(newRow != null){
                newRow.createCell(reference.getCol()).setCellValue(newId);

                FileOutputStream outputStream = new FileOutputStream(filePath);

                workbook.write(outputStream);
                outputStream.close();
            }
            System.out.println("last "+ sheet.getRow(lastRowNum).getCell(reference.getCol()));



            workbook.close();
            fileStream.close();
            observer.onNext(0);
            observer.onComplete();
        });
    }
    private String getCellValue(Cell targetCell){
        if(targetCell == null){
            return "";
        }
        return targetCell.getStringCellValue();
    }
}
