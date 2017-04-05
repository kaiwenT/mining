package com.hust.mining.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hust.mining.constant.Constant.Index;

public class ExcelUtil {

    public static List<String[]> read(String filename) throws IOException {
        InputStream inputStream = new FileInputStream(new File(filename));
        return read(filename, inputStream, -1);
    }

    public static List<String[]> read(String filename, InputStream inputStream, int startRow) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream is null");
        }
        return read(filename, inputStream, startRow, -1, null);
    }

    public static List<String[]> read(String filename, InputStream inputStream, int startRow, int rowNum)
            throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream is null");
        }
        return read(filename, inputStream, startRow, rowNum, null);
    }

    public static List<String[]> read(String filename, InputStream inputStream, int start, int rows, Integer...indexes)
            throws FileNotFoundException, IOException {

        List<String[]> list = new ArrayList<String[]>();
        Workbook workbook;
        if (filename.endsWith("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            workbook = new XSSFWorkbook(inputStream);
        }
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        int colNum = sheet.getRow(0).getLastCellNum();
        if (null == indexes || indexes.length == 0) {
            indexes = new Integer[colNum];
            for (int i = 0; i < colNum; i++) {
                indexes[i] = i;
            }
        }
        if (rows >= 0) {
            rowNum = rows > rowNum ? rowNum : rows;
        }
        start = start > rowNum ? rowNum : start;
        List<String> exitUrls = new ArrayList<String>();
        for (int i = start, x = 1; x <= rowNum; i++, x++) {
            String[] rowStr = new String[indexes.length];
            for (int j = 0; j < indexes.length; j++) {
                try {
                    Cell cell = sheet.getRow(i).getCell(indexes[j]);
                    if (cell.getCellType() == 0) {
                        rowStr[j] = TimeUtil.convert(cell);
                    } else {
                        rowStr[j] = cell.toString();
                    }
                } catch (Exception e) {
                    rowStr[j] = "";
                }
                rowStr[j] = rowStr[j].replaceAll("\n", "");
                rowStr[j] = rowStr[j].replaceAll("\t", "");
                rowStr[j] = rowStr[j].replaceAll("\r", "");
                rowStr[j] = rowStr[j].replaceAll("\b", "");
                rowStr[j] = rowStr[j].replaceAll("\f", "");
            }
            if (CommonUtil.hasEmptyArray(rowStr)) {
                continue;
            }
            int exitIndex = exitUrls.indexOf(rowStr[Index.URL_INDEX]);
            if (exitIndex != -1) {
                if (rowStr[Index.TIME_INDEX].compareTo(list.get(exitIndex)[Index.TIME_INDEX]) < 0) {
                    list.set(exitIndex, rowStr);
                }
            } else {
                list.add(rowStr);
                exitUrls.add(rowStr[Index.URL_INDEX]);
            }
        }
        workbook.close();
        return list;
    }

    public static HSSFWorkbook exportToExcel(@SuppressWarnings("unchecked") List<String[]>...lists) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (int k = 0; k < lists.length; k++) {
            List<String[]> list = lists[k];
            Sheet sheet = workbook.createSheet("sheet" + (k + 1));
            for (int i = 0; i < list.size(); i++) {
                String[] rowList = list.get(i);
                Row row = sheet.createRow(i);
                for (int j = 0; j < rowList.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(rowList[j]);
                }
            }
        }
        return workbook;
    }
}
