import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by masanori on 2016/12/24.
 * Main controller class.
 */
public class MainCtrl implements Initializable {
    @FXML private AnchorPane mainPane;
    @FXML private TextField idSheetPathField;
    @FXML private ComboBox<String> docTypeCombobox;
    @FXML private TextField outputSheetPathField;
    @FXML private TextField docTitleField;
    @FXML private Tab sheet1Tab;
    @FXML private Tab sheet2Tab;
    @FXML private TextField sheet1InputTextArea;
    @FXML private TextField sheet2InputTextArea;

    private FileChooser fileChooser;

    private final static String FileTypeA = "TypeA";
    private final static String FileTypeB = "TypeB";

    private IdManager idManager;
    private int newId;

    @FXML
    public void onSearchIdSheetButtonClicked(){
        // ダイアログを表示して、指定されたファイルのパスをTextFieldにセット.
        File selectedFile = fileChooser.showOpenDialog(mainPane.getScene().getWindow());
        idSheetPathField.setText(selectedFile.getPath());
    }
    @FXML
    public void onSearchOutputSheetButtonClicked(){
        // ダイアログを表示して、指定されたファイルのパスをTextFieldにセット.
        File selectedFile = fileChooser.showOpenDialog(mainPane.getScene().getWindow());
        outputSheetPathField.setText(selectedFile.getPath());
    }
    @FXML
    public void onCreateDocButtonClicked(){
        // 必須項目が空ならそのまま.
        if(idSheetPathField.getText().isEmpty()
                || docTitleField.getText().isEmpty()
                || outputSheetPathField.getText().isEmpty()
                || docTypeCombobox.getSelectionModel().getSelectedIndex() < 0){
            return;
        }

        idManager.addNewId(docTitleField.getText(), idSheetPathField.getText())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Integer integer) {
                        newId = integer;
                    }
                    @Override
                    public void onError(Throwable e) {
                        Alert alert = new Alert(Alert.AlertType.NONE, e.getMessage(), ButtonType.CLOSE);
                        alert.show();
                    }
                    @Override
                    public void onComplete() {
                        // TODO: Book2.xlsxに値を挿入.
                    }
                });
    }
    @FXML
    public void onDocTypeComboboxSelected(){
        switch(docTypeCombobox.getSelectionModel().getSelectedItem()){
            case FileTypeA:
                sheet1Tab.setDisable(false);
                sheet2Tab.setDisable(true);
                break;
            case FileTypeB:
                sheet1Tab.setDisable(true);
                sheet2Tab.setDisable(false);
                break;
            default:
                sheet1Tab.setDisable(true);
                sheet2Tab.setDisable(true);
                break;
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ComboBoxにセットするListの生成.
        ObservableList<String> fileTypeList = FXCollections.observableArrayList();
        fileTypeList.add(FileTypeA);
        fileTypeList.add(FileTypeB);
        docTypeCombobox.setItems(fileTypeList);

        fileChooser = new FileChooser();
        // デフォルトでカレントディレクトリが開くようにする.
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("ファイルを選択");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Spreadsheet", "*.xlsx", "*.ods"));

        idManager = new IdManager();
    }
}
