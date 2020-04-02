package sample.print;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.barcode.BarCode;
import com.github.anastaciocintra.escpos.barcode.QRCode;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.EscPosImage;
import com.github.anastaciocintra.escpos.image.RasterBitImageWrapper;
import sample.model.Item;

import javax.imageio.ImageIO;
import javax.print.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PosPrinter {

    private static final PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

    public void printImage(Item item) throws IOException, PrintException {
        DocPrintJob dpj = printService.createPrintJob();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        /// your legacy commands ini
        // initialize printer
//        outputStream.write(27); // ESC
//        outputStream.write('@');

//        outputStream.write("\n".getBytes());


//        // feed 5 lines
//        outputStream.write(27); // ESC
//        outputStream.write('d');
//        outputStream.write(5);


        /// fitting lib calls on same outputStream
        EscPos escpos = new EscPos(outputStream);

        RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
        BufferedImage githubBufferedImage = ImageIO.read(new File("studio21.jpeg"));
        EscPosImage escposImage = new EscPosImage(githubBufferedImage, new BitonalThreshold());
        Style imgStyle = new Style();
        imgStyle.setJustification(EscPosConst.Justification.Center);
        imageWrapper.setJustification(EscPosConst.Justification.Center);
        escpos.write(imageWrapper, escposImage);
        // lib end
        /// legacy continues from this point

        Style contactStyle = new Style();
        contactStyle.setJustification(EscPosConst.Justification.Center);
        contactStyle.setFontSize(Style.FontSize._1, Style.FontSize._1);

        escpos.setStyle(contactStyle);
        escpos.write(contactStyle, "Addressa: F. Pershefce\n");
        escpos.write(contactStyle, "Contact: +389 70 000 000\n\n");
//
        Style lineStyle = new Style();
        lineStyle.setFontSize(Style.FontSize._6, Style.FontSize._1);
        lineStyle.setBold(true);
        escpos.setStyle(lineStyle);
        escpos.write("--------\n");
        outputStream.write("\n".getBytes());

        Style headerStyle = new Style();
        headerStyle.setJustification(EscPosConst.Justification.Center);
        headerStyle.setBold(true);
        escpos.setStyle(headerStyle);
        escpos.write(headerStyle, "Pershkrimi i servisit\n\n");

        Style contentStyle = new Style();
        contentStyle.setJustification(EscPosConst.Justification.Left_Default);
        escpos.setStyle(contentStyle);
        escpos.write(contentStyle, item.getServiceDesc() + "\n\n");

        Style priceStyle = new Style();
        priceStyle.setBold(true);
        priceStyle.setJustification(EscPosConst.Justification.Center);
        priceStyle.setFontSize(Style.FontSize._3, Style.FontSize._1);
        escpos.setStyle(priceStyle);
        escpos.write(priceStyle, "Cmimi: " + item.getPrice() + " den\n");
        if (item.isPaymentStatus()) {
            escpos.setStyle(headerStyle);
            escpos.write(headerStyle, "E paguar\n\n");
        } else {
            escpos.write(headerStyle, "E papaguar\n\n");
        }


        escpos.setStyle(lineStyle);
        escpos.write(lineStyle, "--------\n\n");

        escpos.setStyle(contentStyle);
        escpos.write(contentStyle, "Lojji i produktit: " + item.getProductType() + "\n");
        escpos.write(contentStyle, "Modeli: " + item.getProductDesc() + "\n\n");


        escpos.setStyle(lineStyle);
        escpos.write(lineStyle, "--------\n");

        escpos.setStyle(contentStyle);
        escpos.write(contentStyle, "Data e pranimit: " +
                item.getReceivedDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + "\n");
        String receivedDate = item.isServiceStatus() ? item.getCompletedDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : "";
        escpos.write(contentStyle, "Data e dorezimit: " + receivedDate + "\n");

        escpos.setStyle(lineStyle);
        escpos.write(lineStyle, "--------\n");

        escpos.setStyle(contentStyle);
        escpos.write(contentStyle, "Personi per kontakt: " + item.getContactPerson() + "\n");
        escpos.write(contentStyle, "Tel: " + item.getPhoneNumber() + "\n\n\n");


        QRCode qrCode = new QRCode();
        qrCode.setJustification(EscPosConst.Justification.Center);
        qrCode.setModel(QRCode.QRModel._2);
        qrCode.setSize(8);
        escpos.write(qrCode, item.getPhoneNumber());
        escpos.write("\n\n");

        BarCode barCode = new BarCode();
        barCode.setJustification(EscPosConst.Justification.Center);
        barCode.setHRIPosition(BarCode.BarCodeHRIPosition.BelowBarCode);
        escpos.write(barCode, item.getSerialNumber());

        escpos.write("\n\n");
        escpos.setStyle(headerStyle);
        escpos.write(headerStyle, "Ju Falemenderit");


        // feed 5 lines
        outputStream.write(27); // ESC
        outputStream.write('d');
        outputStream.write(5);

        // cut paper
        outputStream.write(29); // GS
        outputStream.write('V');
        outputStream.write(48);

        // do not forguet to close outputstream
        outputStream.close();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());


        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc doc = new SimpleDoc(inputStream, flavor, null);
        dpj.print(doc, null);
    }

    public String getDefaultPrinter() {
        return printService.getName();
    }
}
