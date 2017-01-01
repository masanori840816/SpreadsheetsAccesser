import com.sun.istack.internal.NotNull;

import java.util.ArrayList;

/**
 * Created by masanori on 2016/12/31.
 * set input value to insert Sheet2.xlsx.
 */

class InputValueClass {
    InputValueClass(String setSheetName){
        sheetName = setSheetName;
    }
    class CellClass{
        private String cellName;
        public String getCellName(){
            return cellName;
        }
        private String cellValue;
        public String getCellValue(){
            return cellValue;
        }
        public void setCellValue(String setValue){
            cellValue = setValue;
        }
        CellClass(@NotNull String setCellName){
            cellName = setCellName;
        }
    }
    private String sheetName = "";
    String getSheetName(){
        return sheetName;
    }
    private boolean sheetEnabled = false;
    boolean getSheetEnabled(){
        return sheetEnabled;
    }
    void setSheetEnabled(boolean setValue){
        sheetEnabled = setValue;
    }
    private ArrayList<CellClass> targetCellList;
    ArrayList<CellClass> getTargetCellList(){
        return targetCellList;
    }
    void setTargetCellList(ArrayList<CellClass> setValue){
        targetCellList = setValue;
    }
}
