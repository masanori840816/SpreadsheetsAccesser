import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by masanori on 2016/12/31.
 * Get values from target cell.
 */
class CellValueGetter {
    private int columnNum;
    List<Name> getTargetCellValueList(Workbook workbook, String sheetName){
        // 指定されたSheet内のCellの名前を全て取得する.
        List<? extends Name> targetNames = workbook.getAllNames();
        return targetNames.stream()
                .filter(filteredName -> filteredName.getSheetName().equals(sheetName))
                .map(object -> (Name)object)
                .collect(Collectors.toList());
    }
    int getTargetCellColumnNum(List<Name> cellNameList, String targetCellName){
        Optional<Name> foundCellName = cellNameList.stream().filter(name -> name.getNameName().equals(targetCellName)).findFirst();

        Optional<Integer> foundColumnNum = foundCellName.map(cellName -> {
            CellReference reference = new CellReference(cellName.getRefersToFormula());
            return (int)reference.getCol();
        });
        columnNum = -1;
        foundColumnNum.ifPresent(num -> columnNum = num);
        return columnNum;
    }
}
