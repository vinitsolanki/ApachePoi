import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vinit.solanki on 27-Jan-17.
 */
public class ExportExcel {

    private static Logger logger = Logger.getLogger(ExportExcel.class.getName());

    //sheet config
    private static XSSFWorkbook workbook;
    private static XSSFSheet sheet;
    private XSSFCellStyle defaultStyle;
    private List<CellRangeAddress> mergeCells = new ArrayList<CellRangeAddress>();


    public static int ROW_NUM = 0;
    public static int COL_NUM = 0;

    //cell/row style config
    public static XSSFCellStyle defaultFontStyle;
    //text style config
    public static XSSFCellStyle rightAlignStyle;


    public ExportExcel() {
        ROW_NUM = 0;
        COL_NUM = 0;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Sample sheet");
//        default9FontStyle = getFontStyle(9, Color.DARK_GRAY);
//        default10FontStyle = getFontStyle(10, Color.DARK_GRAY);
        defaultFontStyle = getFontStyle(11, Color.DARK_GRAY);
//        default12FontStyle = getFontStyle(12, Color.DARK_GRAY);
        rightAlignStyle = getAlignmentStyle(CellStyle.ALIGN_RIGHT);
    }

    public static XSSFCellStyle getDefaultDateFormat() {
        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        dateCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        return dateCellStyle;
    }

    public static XSSFCellStyle getBorderStyle() {
        XSSFCellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        borderStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        borderStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        borderStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        return borderStyle;
    }

    public static XSSFCellStyle getAlignmentStyle(short alignIndex) {
        XSSFCellStyle rightAligned = workbook.createCellStyle();
        rightAligned.setAlignment(alignIndex);
        return rightAligned;
    }

    public static XSSFCellStyle getFontStyle(int fontSize, Color fontColor) {
        return getFontStyle(fontSize, fontColor, null, false);
    }

    public static XSSFCellStyle getFontStyle(int fontSize, Color fontColor,  boolean isBold) {
        return getFontStyle(fontSize, fontColor, null, isBold);
    }

    public static XSSFCellStyle getFontStyle(int fontSize, Color fontColor,  Color bgColor){
        return getFontStyle(fontSize, fontColor, bgColor, false);
    }

    public static XSSFCellStyle getFontStyle(int fontSize, Color fontColor,  Color bgColor, boolean isBold) {
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) fontSize);
        font.setColor(new XSSFColor(fontColor));
        font.setBold(isBold);

        XSSFCellStyle fontStyle = workbook.createCellStyle();
        fontStyle.setFont(font);
        if(null != bgColor){
            fontStyle.setFillForegroundColor(new XSSFColor(bgColor));
            fontStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }

        return fontStyle;
    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public XSSFSheet getSheet() {
        return sheet;
    }


    public static void setDefaultColumnWidth(int toColumn, int fromColumn, int defaultWidth) {

        for (; toColumn <= fromColumn; toColumn++) {
            sheet.setColumnWidth(toColumn, 6000);
            sheet.setDefaultColumnStyle(toColumn, defaultFontStyle);
        }

    }


    public Row createRow() {
        COL_NUM = 0;
        Row row = sheet.createRow(ROW_NUM++);
        row.setRowStyle(getDefaultStyle()==null ? defaultFontStyle : getDefaultStyle() );
        return row;
    }

    public static Row createRow(XSSFCellStyle defaultRowStyle) {
        COL_NUM = 0;
        Row row = sheet.createRow(ROW_NUM++);

        if(null != defaultRowStyle)
            row.setRowStyle(defaultRowStyle);

        return row;
    }

    //Added image
    public static void addImage(int column, int row, String imagePath, double scale, int pictureType) throws IOException {
        InputStream inputStream = new FileInputStream(imagePath);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        int pictureIdx = workbook.addPicture(bytes, pictureType);
        inputStream.close();

        CreationHelper helper = workbook.getCreationHelper();
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();
        //set image to cell
        anchor.setCol1(column);
        anchor.setRow1(row);

        Picture pict = drawing.createPicture(anchor, pictureIdx);
        pict.resize(scale);
    }


    public Cell createCell(Object value, XSSFCellStyle cellStyle, Row row ) {

        Cell cell = row.createCell(COL_NUM++);
        cell.setCellValue("");

        if (null != value) {
            if (value instanceof String) {
                String strValue = (String) value;
                //if(StringUtils.hasText(strValue)){//
                if(strValue != null && !strValue.trim().equals("")){
                    //check, value is formula, formula always starts with '='
                    if(strValue.startsWith("=")){
                        cell.setCellType(Cell.CELL_TYPE_FORMULA);
                        //cell.setCellFormula(strValue.substring(1)); // removes first character "="
                    } else {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cell.setCellValue(strValue);
                    }
                }
            } else if (value instanceof Number) {
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                if (value instanceof Integer) {
                    cell.setCellValue((Integer) value);
                } else if (value instanceof Float) {
                    cell.setCellValue((Float) value);
                } else if (value instanceof Double) {
                    cell.setCellValue((Double) value);
                }else if (value instanceof Long) {
                    cell.setCellValue((Long) value);
                }
            } else if (value instanceof Boolean) {
                cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
                cell.setCellValue((String) value);
            } else if (value instanceof Date) {
                cell.setCellValue((Date) value);
                cell.setCellStyle(getDefaultDateFormat());
            }

            //check for cell have defaultCellStyle
            if (null != cellStyle) {
                cell.setCellStyle(cellStyle);
            }
        }
        return cell;

    }

    public Cell createCell(Object value,  Row row) {
        return createCell(value, null, row);
    }

    public Cell createCell(Object value, boolean isBold, Row row) {

        Cell cell = createCell(value, null, row);

        XSSFCellStyle cellBoldStyle = (XSSFCellStyle) cell.getCellStyle();
        if(null != cellBoldStyle) {
            XSSFFont cellBoldFont = cellBoldStyle.getFont();
            cellBoldFont.setBold(isBold);
            cellBoldStyle.setFont(cellBoldFont);
            cell.setCellStyle(cellBoldStyle);
        }
        return cell;
    }

    public Cell createCell(Object value, XSSFCellStyle cellStyle, boolean isBold, Row row) {

        if(null != cellStyle) {
            XSSFFont cellBoldFont = cellStyle.getFont();
            cellBoldFont.setBold(isBold);
            cellStyle.setFont(cellBoldFont);
        }

        Cell cell = createCell(value, cellStyle, row);

        return cell;

    }

    public Cell createCell(Object value, int columnNumber, XSSFCellStyle cellStyle, Row row) {
        COL_NUM = columnNumber;
        return createCell(value, cellStyle, row );
    }

    private void mergeCellAddresses(List<CellRangeAddress> mergeCells) {
        for (CellRangeAddress mergedCell : mergeCells) {
            sheet.addMergedRegion(mergedCell);
        }
    }

    public boolean writeExcelToFile(File createdExcelFile) {

        try {

            //merging cells
            mergeCellAddresses(mergeCells);

            FileOutputStream out = new FileOutputStream(createdExcelFile);
            workbook.write(out);
            out.close();
            logger.info("Excel written successfully..");
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(createdExcelFile.getAbsolutePath() + " File not found " );
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(createdExcelFile.getAbsolutePath() + " File not found " );

        }
        return false;
    }

    public String getCellAddress(Cell cell){
        return CellReference.convertNumToColString(cell.getColumnIndex()) + (cell.getRowIndex()+1);
    }

    public XSSFCellStyle getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(XSSFCellStyle defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public void setMergeCells(List<CellRangeAddress> mergeCells) {
        this.mergeCells = mergeCells;
    }
}
