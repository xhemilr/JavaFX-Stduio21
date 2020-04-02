package sample.print;

import sample.model.Item;

import javax.swing.*;

public class CustomPrintService {

    public void print(Item item){
        PosPrinter posPrinter = new PosPrinter();
        String defaultPrinter = posPrinter.getDefaultPrinter();
        if (defaultPrinter != null){
            try {
                posPrinter.printImage(item);
            }catch (Exception e){
                JOptionPane.showMessageDialog(null, "Could not print\nPlease check printer configuration" +
                        "Error Code: " + e.getMessage() + " \nPrinter Name: " + defaultPrinter);
            }
        }
    }
}
