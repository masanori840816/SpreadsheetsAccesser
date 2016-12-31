import com.sun.istack.internal.NotNull;
import io.reactivex.Observable;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by masanori on 2016/12/24.
 * Get lagest ID number from spreadsheet. and add new ID to the spreadsheet.
 */
public class IdManager {
    private final static String CellNameId = "ID";
    private final static String CellNameTitle = "TITLE";
    private final static String CellNameLastUpdateDate = "LASTUPDATEDATE";

    private CellValueGetter cellValueGetter;

    public IdManager(){
        cellValueGetter = new CellValueGetter();
    }

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
            List<Name> allCellNameList = cellValueGetter.getTargetCellValueList(workbook, sheet.getSheetName());

            int idColumnNum = cellValueGetter.getTargetCellColumnNum(allCellNameList, CellNameId);
            int titleColumnNum = cellValueGetter.getTargetCellColumnNum(allCellNameList, CellNameTitle);
            int lastUpdateDateColumnNum = cellValueGetter.getTargetCellColumnNum(allCellNameList, CellNameLastUpdateDate);

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
            else{
                // IDが未登録の場合は1を付与する.
                newId = 1;
            }

            Row newRow = sheet.createRow(lastRowNum + 1);

            if(newRow != null){
                newRow.createCell(idColumnNum).setCellValue(newId);
                newRow.createCell(titleColumnNum).setCellValue(title);


                FileOutputStream outputStream = new FileOutputStream(filePath);

                workbook.write(outputStream);
                outputStream.close();
            }
            System.out.println("last "+ sheet.getRow(lastRowNum).getCell(idColumnNum));

            workbook.close();
            fileStream.close();
            // 発行したIDを返す.
            observer.onNext(newId);
            observer.onComplete();
        });
    }
}
