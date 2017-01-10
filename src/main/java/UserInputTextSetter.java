import com.sun.istack.internal.NotNull;
import io.reactivex.Observable;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by masanori on 2016/12/31.
 */
class UserInputTextSetter {
    private CellValueGetter cellValueGetter;
    UserInputTextSetter(){
        cellValueGetter = new CellValueGetter();
    }
    Observable<Integer> insertInputTexts(@NotNull String filePath, @NotNull ArrayList<InputValueClass> targetSheetList){
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
            for(InputValueClass targetSheet: targetSheetList){
                // とりあえずSheet名は固定.
                Sheet sheet = workbook.getSheet(targetSheet.getSheetName());
                if(sheet == null){
                    // Sheetが取得できなければskip.
                    continue;
                }
                if(targetSheet.getSheetEnabled()){
                    List<Name> allCellNameList = cellValueGetter.getTargetCellValueList(workbook, sheet.getSheetName());

                    Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
                    if(newRow == null){
                        observer.onError(new Throwable("行の追加に失敗しました。"));
                    }
                    for(InputValueClass.CellClass targetCell: targetSheet.getTargetCellList()){
                        int index = cellValueGetter.getTargetCellColumnNum(allCellNameList,targetCell.getCellName());
                        newRow.createCell(index).setCellValue(targetCell.getCellValue());
                    }
                }
                else{
                    // SheetのIndex取得.
                    int indexNum = workbook.getSheetIndex(sheet);
                    // シート非表示.
                    workbook.setSheetHidden(indexNum, true);
                }
            }
            // Workbookの保存は全てのSheetへの変更が完了後に行う.
            FileOutputStream outputStream = new FileOutputStream(filePath);

            workbook.write(outputStream);
            outputStream.close();

            workbook.close();
            fileStream.close();
            // 発行したIDを返す.
            observer.onNext(0);
            observer.onComplete();
        });
    }
}
