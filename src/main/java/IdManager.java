import com.sun.istack.internal.NotNull;
import io.reactivex.Observable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;

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
            observer.onNext(0);
            observer.onComplete();
        });
    }
}
