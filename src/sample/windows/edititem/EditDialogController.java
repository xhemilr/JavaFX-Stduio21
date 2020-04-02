package sample.windows.edititem;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.data.ItemData;
import sample.model.Item;
import sample.print.CustomPrintService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditDialogController {

    public TextField txtContact;
    public TextField txtPhoneNumber;
    public TextField txtProductType;
    public TextField txtSerialNumber;
    public TextField txtProductDescription;
    public TextArea txtComments;
    public TextField txtPrice;
    public TextField txtReceivedDate;
    public CheckBox service;
    public CheckBox payment;
    public Button btnPrint;

    private Item oldItem = null;

    private static CustomPrintService customPrintService;

    public void initialize(){
        customPrintService = new CustomPrintService();
        txtPrice.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,12}([\\.]\\d{0,4})?")) {
                txtPrice.setText(oldValue);
            }
        });

        if (oldItem == null){
            btnPrint.setVisible(false);
        }

        btnPrint.setOnAction(event -> customPrintService.print(oldItem));
    }

    public void loadItemDetails(Item newItem){
        if (newItem != null){
            oldItem = newItem;
            txtContact.setText(newItem.getContactPerson());
            txtPhoneNumber.setText(newItem.getPhoneNumber());
            txtProductType.setText(newItem.getProductType());
            txtSerialNumber.setText(newItem.getSerialNumber());
            txtProductDescription.setText(newItem.getProductDesc());
            txtComments.setText(newItem.getServiceDesc());
            txtPrice.setText(newItem.getPrice().toString());
            txtReceivedDate.setText(oldItem.getReceivedDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
            if (oldItem.isPaymentStatus()){
                payment.selectedProperty().setValue(true);
            }
            if (oldItem.isServiceStatus()){
                service.selectedProperty().setValue(true);
            }
        }
    }

    public void processResult(){
        oldItem.setProductType(txtProductType.getText().trim());
        oldItem.setProductDesc(txtProductDescription.getText().trim());
        oldItem.setPrice(Double.parseDouble(txtPrice.getText().trim()));
        oldItem.setServiceStatus(service.isSelected());
        oldItem.setPaymentStatus(payment.isSelected());
        oldItem.setServiceDesc(txtComments.getText().trim());
        oldItem.setPhoneNumber(txtPhoneNumber.getText().trim());
        oldItem.setContactPerson(txtContact.getText().trim());
        oldItem.setCompletedDate(oldItem.isServiceStatus() ? LocalDate.now().plusDays(1) : null);
        ItemData.saveItem(oldItem);
    }
}
