/**
 * Utility class having methods for excel book manipulations like reading input data and writing to output sheet.
 * @author Srinivasan Ramasamy
 * @version 1.0
 */

package com.perficient.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.perficient.core.TestDriver;

//This is the Input Output Utility File with methods to setup, read, write methods for the EXCEL Input and Output files.
public class IOUtil {

	//Takes the Test Method name as input identifier. Returns a linked hashmap that has the data of entire row from Input.xls
	public static LinkedHashMap<String, String> getInputData(String TCName, int intCurrentIteration) {
		//Setup local Linked Hash Map parameters to read from Input.xls
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		try {
			FileInputStream fs = new FileInputStream(TestDriver.props.getProperty("inputexcelpath"));
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheet("TestData");
			
			int intRow = findRowNumber(sheet, "Common_Parameters");
			
			if(intRow!=0){
				for (int i = 1; i <= sheet.getRow(intRow).getLastCellNum()-1; i++) {
					sheet.getRow(intRow).getCell(i,Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					sheet.getRow(intRow+1).getCell(i,Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					parameters.put(sheet.getRow(intRow).getCell(i,Row.CREATE_NULL_AS_BLANK).getStringCellValue(), sheet.getRow(intRow+1).getCell(i,Row.CREATE_NULL_AS_BLANK).getStringCellValue());
				}	
			}
			
			intRow = findRowNumber(sheet, TCName);
			parameters.put("TC_Name", TCName);
			
			// Default input value for description if no input data row available.
			if(intRow==0){
				parameters.put("TC_Description","");
				return parameters;
			}
			else {
				sheet.getRow(intRow).getCell(1,Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				parameters.put("TC_Description", sheet.getRow(intRow).getCell(1,Row.CREATE_NULL_AS_BLANK).getStringCellValue());
			}
			
			//find the data row for the current iteration
			int currentIterationRow;
			for (int i = intRow; ; i++) {
				sheet.getRow(i).getCell(2,Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				if(sheet.getRow(i).getCell(2,Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("Iteration"+Integer.toString(intCurrentIteration))) {
					currentIterationRow = i;
					break;
				}
			}

			//get the current iteration input data			
			for (int i = 2; i <= sheet.getRow(intRow-1).getLastCellNum()-1; i++) {
				sheet.getRow(intRow-1).getCell(i,Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				sheet.getRow(currentIterationRow).getCell(i,Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				parameters.put(sheet.getRow(intRow-1).getCell(i,Row.CREATE_NULL_AS_BLANK).getStringCellValue(), sheet.getRow(currentIterationRow).getCell(i,Row.CREATE_NULL_AS_BLANK).getStringCellValue());
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			e.printStackTrace();
		}
		return parameters;
	}

	//Method to find the row number of the given string within a given sheet
	private static int findRowNumber(HSSFSheet sheet, String cellData) {
		for (Row row : sheet) {
			Cell cell = row.getCell(0,Row.CREATE_NULL_AS_BLANK);
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				if (cell.getRichStringCellValue().getString().trim().equals(cellData)) {
					return row.getRowNum();
				}
			}
		}
		return 0;
	}

	public static LinkedHashMap<String, String> getInputDataFromJSON(String TCName, int intCurrentIteration) {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();

		try {
			Object obj = new JSONParser().parse(new FileReader(TestDriver.props.getProperty("inputjsonpath")));
			JSONObject jsonObject = (JSONObject) obj;
			
			Map tcparameters = null;
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    if (key.equalsIgnoreCase(TCName)){
			    	tcparameters = ((Map)jsonObject.get(key));
			    	parameters.put("TC_Name", TCName);
			    	break;
			    }
			}
			if (!tcparameters.isEmpty()){
				Map currentIterationParams = (Map)tcparameters.get("Iteration"+Integer.toString(intCurrentIteration));
				for(Iterator iterator = currentIterationParams.keySet().iterator(); iterator.hasNext();) {
				    String key = (String) iterator.next();
				    if(currentIterationParams.get(key) instanceof String)
				    	parameters.put(key, ((String)currentIterationParams.get(key)));
				    else if(currentIterationParams.get(key) instanceof Integer || currentIterationParams.get(key) instanceof Long || currentIterationParams.get(key) instanceof Boolean)
				    	parameters.put(key, ((String)tcparameters.get(key).toString()));
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    return parameters;
	}
	
	public static int getNumOfChildNodesFromJSON(String TCName) {
		int size = 0;
		try {
			Object obj = new JSONParser().parse(new FileReader(TestDriver.props.getProperty("inputjsonpath")));
			JSONObject jsonObject = (JSONObject) obj;
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    if (key.equalsIgnoreCase(TCName)){
			    	size = (((Map)jsonObject.get(key)).size());
			    	break;
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return size;
	}
	
	public static int getNumOfChildNodesFromExcel(String TCName) {
		int size = 1, intRow;
		try {
			FileInputStream fs = new FileInputStream(TestDriver.props.getProperty("inputexcelpath"));
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheet("TestData");
			
			intRow = findRowNumber(sheet, TCName);
			if(intRow != 0) {
				for (int i = intRow + 1; ; i++) {
					sheet.getRow(i).getCell(2,Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					if(sheet.getRow(i).getCell(2,Row.CREATE_NULL_AS_BLANK).getStringCellValue().startsWith("Iteration"))
						size = size + 1;
					else
						break;
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return size;
	}
}
