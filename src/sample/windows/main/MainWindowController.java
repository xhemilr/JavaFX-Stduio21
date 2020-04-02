package sample.windows.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import sample.data.ItemData;
import sample.model.Item;
import sample.windows.additem.ItemDialogController;
import sample.windows.edititem.EditDialogController;
import sample.windows.settings.SettingsController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Predicate;

public class MainWindowController {
    private static final ItemData itemData = new ItemData();

    public BorderPane mainBorderPane;
    public TableView<Item> tblItems;
    public MenuItem refreshItems;
    public MenuItem mnExit;
    public MenuItem mnNewItem;
    public MenuItem mnEditItem;
    public ToggleButton tglOpenItems;
    public ToggleButton tglOpenPayments;
    public MenuItem mnDeleteItem;
    public DatePicker dtPickerFrom;
    public DatePicker dtPickerTo;
    public CheckBox chkbFilterByDate;
    public MenuItem mnServerSettings;
    private final Label placeHolder = new Label("No items found");
    public Label lblProductType;
    public Label lblSerialNumber;
    public Label lblProductDesc;
    public Label lblDateReceived;
    public Label lblDateReleased;
    public Label lblServiceStatus;
    public Label lblPaymentStatus;
    public Label lblPrice;
    public TextArea txtComments;
    public TextField txtSearchBySN;
    public Label lblPhoneNumber;
    public Label lblContact;
    public TextField txtSearchByName;
    public Button btnRefreshData;


    @FXML
    private Button btnNewItem;

    private ObservableList<Item> items;

    private Predicate<Item> filter;

    public void initialize(){
        initializeTable();
        loadData();
        setListeners();
    }

    private void setListeners(){
        tblItems.getSelectionModel().selectedItemProperty().addListener((selectedItem, oldItem, newItem) -> {
            if (selectedItem.getValue() != null){
                lblProductType.setText(selectedItem.getValue().getProductType() == null ? "" : selectedItem.getValue().getProductType());
                lblSerialNumber.setText(selectedItem.getValue().getSerialNumber() == null ? "" :selectedItem.getValue().getSerialNumber());
                lblProductDesc.setText(selectedItem.getValue().getProductDesc() == null ? "" : selectedItem.getValue().getProductDesc());
                lblPrice.setText(selectedItem.getValue().getPrice() == null ? "0.0 mkd" : selectedItem.getValue().getPrice() + " mkd");
                lblServiceStatus.setText(selectedItem.getValue().isServiceStatus() ? "Closed" : "Open");
                lblPaymentStatus.setText(selectedItem.getValue().isPaymentStatus() ? "Closed" : "Open");
                txtComments.setText(selectedItem.getValue().getServiceDesc() == null ? "" : selectedItem.getValue().getServiceDesc());
                lblDateReceived.setText(selectedItem.getValue().getReceivedDate() == null ? "" : selectedItem.getValue().getReceivedDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
                lblDateReleased.setText(selectedItem.getValue().getCompletedDate() == null ? "" : selectedItem.getValue().getCompletedDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
                lblContact.setText(selectedItem.getValue().getContactPerson() == null ? "" : selectedItem.getValue().getContactPerson());
                lblPhoneNumber.setText(selectedItem.getValue().getPhoneNumber() == null ? "" : selectedItem.getValue().getPhoneNumber());
            }
        });

        txtSearchBySN.textProperty().addListener((searchText, s, t1) -> {
            filter = item -> item.getSerialNumber().contains(searchText.getValue().trim());
            filterData();
        });

        txtSearchBySN.focusedProperty().addListener((observableValue, aBoolean, t1) -> txtSearchByName.clear());

        txtSearchByName.textProperty().addListener((searchText, s, t1)->{
            filter = item -> item.getContactPerson().contains(searchText.getValue().trim());
            filterData();
        });

        txtSearchByName.focusedProperty().addListener((observableValue, aBoolean, t1) -> txtSearchBySN.clear());

        btnNewItem.setOnAction(event-> showAddDialog());

        refreshItems.setOnAction(event-> loadData());

        mnExit.setOnAction(event -> System.exit(0));

        mnNewItem.setOnAction(event -> showAddDialog());

        mnEditItem.setOnAction(event -> showEditDialog());

        mnDeleteItem.setOnAction(event -> deleteItem());

        tblItems.setRowFactory(tv->{
            TableRow<Item> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2){
                    showEditDialog();
                }
            });
            return row;
        });

        tglOpenItems.setOnAction(event -> {
            if (tglOpenItems.isSelected()){
                filter = (item -> !item.isServiceStatus());
            }else{
                filter = (item -> true);
            }
            filterData();
        });

        tglOpenPayments.setOnAction(event -> {
            if (tglOpenPayments.isSelected()){
                filter = (item -> !item.isPaymentStatus());
            }else{
                filter = (item -> true);
            }
            filterData();
        });

        chkbFilterByDate.setOnAction(event -> filterByDate());

        dtPickerTo.setOnAction(event -> {
            if (dtPickerTo.getValue().isBefore(dtPickerFrom.getValue())){
                dtPickerTo.setValue(dtPickerFrom.getValue());
            }
            filterByDate();
        });

        dtPickerFrom.setOnAction(event -> {
            if (dtPickerFrom.getValue().isAfter(dtPickerTo.getValue())){
                dtPickerFrom.setValue(dtPickerTo.getValue());
            }
            filterByDate();
        });

        btnRefreshData.setOnAction(event -> loadData());

        mnServerSettings.setOnAction(event -> showServerSettings());
    }

    private void initializeTable(){
        tblItems.setPlaceholder(placeHolder);
        tblItems.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn<Item, String> serialNumberColumn = new TableColumn<>("Serial Number");
        TableColumn<Item, String> contactColumn = new TableColumn<>("Contact");
        TableColumn<Item, String> dateReceivedColumn = new TableColumn<>("Received Date");
        TableColumn<Item, String> productDescColumn = new TableColumn<>("Product Description");
        TableColumn<Item, Boolean> dateReleasedColumn = new TableColumn<>("Completed Date");
        TableColumn<Item, Boolean> serviceStatusColumn = new TableColumn<>("Service Status");
        TableColumn<Item, Boolean> paymentStatusColumn = new TableColumn<>("Payment Status");
        serialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        productDescColumn.setCellValueFactory(new PropertyValueFactory<>("productDesc"));
        dateReceivedColumn.setCellValueFactory(new PropertyValueFactory<>("receivedDate"));
        serviceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("serviceStatus"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        dateReleasedColumn.setCellValueFactory(new PropertyValueFactory<>("completedDate"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        paymentStatusColumn.setStyle("-fx-alignment: CENTER");
        serviceStatusColumn.setStyle("-fx-alignment: CENTER");
        dateReceivedColumn.setStyle("-fx-alignment: CENTER");
        dateReleasedColumn.setStyle("-fx-alignment: CENTER");
        serialNumberColumn.setMinWidth(120);
        productDescColumn.setMinWidth(120);
        dateReceivedColumn.setMinWidth(100);
        serviceStatusColumn.setMinWidth(120);
        paymentStatusColumn.setMinWidth(120);
        dateReleasedColumn.setMinWidth(100);
        contactColumn.setMinWidth(150);


        serviceStatusColumn.setCellFactory(col-> new TableCell<Item,Boolean>(){
            @Override
            protected void updateItem(Boolean s, boolean b) {
                super.updateItem(s, b);
                setText(b ? null : s ? "Closed" : "Open");
            }
        });
        paymentStatusColumn.setCellFactory(col-> new TableCell<Item, Boolean>(){
            @Override
            protected void updateItem(Boolean s, boolean b) {
                super.updateItem(s, b);
                setText(b ? null : s ? "Closed" : "Open");
            }
        });
        items = FXCollections.observableArrayList();
        tblItems.setItems(items);
        tblItems.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblItems.getColumns().addAll(serialNumberColumn, contactColumn, productDescColumn,
                dateReceivedColumn, dateReleasedColumn, paymentStatusColumn, serviceStatusColumn);

        dtPickerFrom.setValue(LocalDate.now());
        dtPickerTo.setValue(LocalDate.now());
    }

    private void loadData(){
        new Thread(()-> itemData.getJsonData(items)).start();
    }

    private void deleteItem(){
        Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
        if (selectedItem==null){
            Alert itemNotSelectedAlert = new Alert(Alert.AlertType.INFORMATION);
            itemNotSelectedAlert.setTitle("No Todo Item selected");
            itemNotSelectedAlert.setContentText("Please select Todo Item to edit");
            itemNotSelectedAlert.showAndWait();
            return;
        }
        Alert confimationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confimationAlert.setTitle("Confirmation Dialog");
        confimationAlert.setHeaderText("Delete Item");
        confimationAlert.setContentText("Are you sure?");

        Optional<ButtonType> result = confimationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            itemData.deleteItem(selectedItem.getServiceId());
            resetLabels();
            Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
            informationAlert.setTitle("Information Dialog");
            informationAlert.setHeaderText(null);
            informationAlert.setContentText("Item Deleted");
            informationAlert.showAndWait();
        }
        loadData();
    }

    private void filterData(){
        FilteredList<Item> filteredList = new FilteredList<>(items, filter);
        tblItems.setItems(filteredList);
        if (filteredList.isEmpty()){
            resetLabels();
        }
    }

    private void filterByDate(){
        if (chkbFilterByDate.isSelected()){
            filter = item -> (item.getReceivedDate().isAfter(dtPickerFrom.getValue().minusDays(1)) && (item.getReceivedDate().isBefore(dtPickerTo.getValue().plusDays(1))));
        }else{
            filter = item -> true;
        }
        filterData();
    }

    private void showAddDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/sample/windows/additem/additemdialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Could not load the dialog");
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.setTitle("Add new Item");
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            ItemDialogController controller = fxmlLoader.getController();
            controller.processResults();
            loadData();
        }
    }

    private void showEditDialog(){
        Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
        if (selectedItem==null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Item selected");
            alert.setContentText("Please select Item to edit");
            alert.showAndWait();
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/sample/windows/edititem/edititemdialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Could not load the dialog");
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        EditDialogController dialogController = fxmlLoader.getController();
        dialogController.loadItemDetails(selectedItem);
        dialog.setTitle("Edit Item");
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            EditDialogController controller = fxmlLoader.getController();
            controller.processResult();
            loadData();
        }
    }

    private void resetLabels(){
        lblProductType.setText("");
        lblSerialNumber.setText("");
        lblProductDesc.setText("");
        lblDateReceived.setText("");
        lblDateReleased.setText("");
        lblServiceStatus.setText("");
        lblPaymentStatus.setText("");
        lblPrice.setText("");
        txtComments.setText("");
        lblPhoneNumber.setText("");
        lblContact.setText("");
    }

    private void showServerSettings(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/sample/windows/settings/settingsdialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Could not load the dialog");
        }
        SettingsController settingsController  = fxmlLoader.getController();
        settingsController.loadSettings();
        dialog.showAndWait();

    }
}
