package sample.windows.additem;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.data.ItemData;
import sample.model.Item;

import java.time.LocalDate;

public class ItemDialogController {

    public TextField txtContact;
    public TextField txtPhoneNumber;
    public TextField txtProductType;
    public TextField txtSerialNumber;
    public TextField txtProductDescription;
    public TextArea txtComments;
    public TextField txtPrice;

    public void initialize(){
        txtPrice.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,12}([\\.]\\d{0,4})?")) {
                txtPrice.setText(oldValue);
            }
        });
    }

    public void processResults() {
        Item item = new Item();
        item.setContactPerson(txtContact.getText());
        item.setPhoneNumber(txtPhoneNumber.getText());
        item.setPrice(txtPrice.getText().isEmpty() ? 0 : Double.parseDouble(txtPrice.getText()));
        item.setProductDesc(txtProductDescription.getText());
        item.setPaymentStatus(false);
        item.setSerialNumber(txtSerialNumber.getText());
        item.setServiceDesc(txtComments.getText());
        item.setProductType(txtProductType.getText());
        item.setReceivedDate(LocalDate.now().plusDays(1));
        item.setCompletedDate(null);
        item.setServiceStatus(false);
        item.setUserId(1);
        ItemData.saveItem(item);
    }
}
