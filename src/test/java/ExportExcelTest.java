import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by indianic on 13-Feb-17.
 */
public class ExportExcelTest {

    private static Logger lw = Logger.getLogger(ExportExcelTest.class.getName());
    String fileNamePrefix = new Date().getTime() + "-sample.xlsx";

    @Test
    public void exportExcelFile() {
        boolean isExpored = false;
        try {
            //Create excel sheet
            ExportExcel excel = new ExportExcel();

            //set default min width to columns
            excel.setDefaultColumnWidth(0, 8, 6000);

            //merge cells
            List<CellRangeAddress> mergeCells = new ArrayList<CellRangeAddress>();
            mergeCells.add(new CellRangeAddress(0, 0, 1, 6));//First Row, Last Row, First Column, Last Column (0-based)
            mergeCells.add(new CellRangeAddress(7, 7, 0, 6));//First Row, Last Row, First Column, Last Column (0-based)

            Row row = excel.createRow();

            try {
                //Added image
                excel.addImage(0, 0, "C:\\ProjectsPerforce\\CM\\web\\images\\logo.png", 1, Workbook.PICTURE_TYPE_PNG);  //TODO : need get logo from properties, // scale = 0.5, Reset the image to the 0.5 scale size
            } catch (IOException e) {
                lw.error("Export report logo not found while exporting excel report");
            }

            excel.createCell("CARLTON & UNITED BREWERIES PRODUCTION MANAGEMENT UNIT", 1, excel.getFontStyle(18, Color.DARK_GRAY), row);

            excel.createRow(); //created blank row

            row = excel.createRow();
            excel.createCell("Date :", true, row);
            excel.createCell(new Date(), row);
            excel.createCell("Agency :", true, row);
            excel.createCell("agency value", row);
            excel.createCell("PMU contact : ", true, row);
            excel.createCell("PMUContacts", row);

            row = excel.createRow();
            excel.createCell("Noosh Number (SAP Supplier Part No) :", true, row);
            excel.createCell(500, row);
            excel.createCell("Agency contact: ", true, row);
            excel.createCell("", row);
            excel.createCell("CUB Initiator :", true, row);
            excel.createCell("", row);

            row = excel.createRow();
            excel.createCell("Project Title (SAP Description) : ", true, row);
            excel.createCell("Project Name", row);
            excel.createCell("Req Del Date :", true, row);
            excel.createCell(new Date(), row);
            excel.createCell("CUB Division :", true, row);
            excel.createCell("CUB Division", row);


            row = excel.createRow();
            excel.COL_NUM = 4; //skip some columns
            excel.createCell("Retailer :", true, row);
            excel.createCell("retailer", row);

            row = excel.createRow(excel.getFontStyle(10, Color.RED));
            row.setHeight((short) 1000);

            excel.createCell("When placing your order in SAP: \n" +
                    "1. Enter the TOTAL $ ex GST as the quantity with a unit price of $1.00 as a single li order for the total quote \n" +
                    "2. Enter the 5 digit Noosh number as the Supplier Part No. \n" +
                    "3. Use the Project title as the SAP Description inc quote version number", row);

            row = excel.createRow(excel.getFontStyle(12, Color.DARK_GRAY, true));

            excel.createCell("Item", excel.getBorderStyle(), true, row);
            excel.createCell("Item Version", excel.getBorderStyle(), true, row);
            excel.createCell("Noosh No", excel.getBorderStyle(), true, row);
            excel.createCell("Specification", excel.getBorderStyle(), true, row);
            excel.createCell("Quantity", excel.getBorderStyle(), true, row);
            excel.createCell("Unit Price", excel.getBorderStyle(), true, row);
            excel.createCell("Item Total", excel.getBorderStyle(), true, row);

            for (int i = 0; i <= 10; i++) {

                row = excel.createRow(excel.getFontStyle(9, Color.DARK_GRAY));

                XSSFCellStyle cellStyle = excel.getBorderStyle();
                cellStyle.setFont(excel.getFontStyle(10, Color.DARK_GRAY).getFont());

                excel.createCell("Spec Name " + i, cellStyle, row);
                excel.createCell("Item Version " + i, excel.getBorderStyle(), row);
                excel.createCell("SKU " + i , excel.getBorderStyle(), row);
                excel.createCell("Desc " + i, row);

                cellStyle = excel.getFontStyle(10, Color.DARK_GRAY);
                cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                Cell chosenQuantityCell = excel.createCell(i * 100, cellStyle, row);

                String unitPriceFormat = "=" + CellReference.convertNumToColString(chosenQuantityCell.getColumnIndex() + 2) + (chosenQuantityCell.getRowIndex() + 1) + "/" + excel.getCellAddress(chosenQuantityCell);
                excel.createCell(unitPriceFormat, excel.getBorderStyle(), row);

                excel.createCell(i * 12, cellStyle, row);

            }

            //create blank rows with borders.
            row = excel.createRow();
            for (int i = 0; i < 7; i++) {
                excel.createCell("", excel.getBorderStyle(), row);
            }
            row = excel.createRow();
            for (int i = 0; i < 7; i++) {
                excel.createCell("", excel.getBorderStyle(), row);
            }

            mergeCells.add(new CellRangeAddress(excel.ROW_NUM, excel.ROW_NUM + 1, 0, 2));

            row = excel.createRow();
            excel.createCell("Additional instructions", excel.getFontStyle(9, Color.DARK_GRAY), true, row);

            excel.COL_NUM = 5; //skip some columns

            XSSFCellStyle cellStyle = excel.getFontStyle(9, Color.DARK_GRAY, true);
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            excel.createCell("TOTAL ex GST", cellStyle, row);

            cellStyle = excel.getFontStyle(12, Color.DARK_GRAY, true);
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            excel.createCell("", cellStyle, row);

            row = excel.createRow();
            excel.COL_NUM = 6; //skip some columns

            cellStyle = excel.getFontStyle(9, Color.DARK_GRAY);
            cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            excel.createCell("Estimated Production Turnaround XXX working days", cellStyle, true, row);
            mergeCells.add(new CellRangeAddress(excel.ROW_NUM, excel.ROW_NUM, 0, 6));

            row = excel.createRow();
            excel.createCell("Like items print together unless otherwise requested. Costs subject to 10% GST. Valid for 30 days from the date of this quote. Please itemise each line of your CUB purchase order as per the items listed above.", excel.getFontStyle(9, Color.DARK_GRAY), row);
            mergeCells.add(new CellRangeAddress(excel.ROW_NUM, excel.ROW_NUM, 0, 6));

            row = excel.createRow();
            excel.createCell("Forms are quoted standard unless stated as unique/new in brief. Prices are subject to sighting artwork and mock ups and are only based on sketches supplied with briefs. Small quantities are printed digitally without gloss varnish.", excel.getFontStyle(9, Color.DARK_GRAY), row);
            mergeCells.add(new CellRangeAddress(excel.ROW_NUM, excel.ROW_NUM, 0, 6));

            row = excel.createRow();
            excel.createCell("Production turnaround based on Ergo’s receipt of BOTH approved purchase order AND approved print ready artwork being uploaded to the DAM (whichever is last). Delivery dates to be reconfirmed by Ergo. ", excel.getFontStyle(9, Color.DARK_GRAY), row);
            mergeCells.add(new CellRangeAddress(excel.ROW_NUM, excel.ROW_NUM, 0, 6));

            row = excel.createRow();
            excel.createCell("Delays in supply of PO, artwork or signed off proof may result in delays in delivery of stock", excel.getFontStyle(9, Color.RED), row);

            mergeCells.add(new CellRangeAddress(excel.ROW_NUM, excel.ROW_NUM, 0, 6));

            //add merged cells to excel sheet.
            excel.setMergeCells(mergeCells);

            String fileNamePrefix = new Date().getTime() + "-sample.xlsx";
            //File createdExcelFile = new File(ServerConfig.getInstance().get("uploadpath") + fileNamePrefix);
            File createdExcelFile = new File("C:\\Temp\\" + fileNamePrefix);

            //write workbook to excel file.
            isExpored = excel.writeExcelToFile(createdExcelFile);

            if (isExpored) {
                System.out.println("file Export successfully to : " + createdExcelFile.getAbsolutePath());
                lw.error("file Export successfully to : " + createdExcelFile.getAbsolutePath());
            } else {
                System.out.println("Failed to write worksheet to file");
            }

        } catch (Exception e) {
            lw.error("Failed to Export Excel", e);
        }

        Assert.assertTrue("Failed to export excel", isExpored);
    }


    @Test
    public void simpleExportTest(){
        ExportExcel excel = new ExportExcel();


        Row row = excel.createRow();
        excel.createCell("Date :", true, row);
        excel.createCell(new Date(), row);
        excel.createCell("Agency :", true, row);
        excel.createCell("agency value", row);
        excel.createCell("PMU contact : ", true, row);
        excel.createCell("PMUContacts", row);

        File createdExcelFile = new File("C:\\Temp\\" + fileNamePrefix);

        //write workbook to excel file.
        boolean isExpored = excel.writeExcelToFile(createdExcelFile);

        if (isExpored) {
            System.out.println("file Export successfully to : " + createdExcelFile.getAbsolutePath());
            lw.error("file Export successfully to : " + createdExcelFile.getAbsolutePath());
        } else {
            System.out.println("Failed to write worksheet to file");

        }


    }
}
