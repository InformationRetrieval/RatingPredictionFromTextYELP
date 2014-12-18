package com.yelpdata.ir.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

public class BuildDataset {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileReader fr = new FileReader("features.txt");
		BufferedReader br = new BufferedReader(fr);
		String currentLine = br.readLine();
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFWorkbook workbook2 = new HSSFWorkbook();
		
		HSSFSheet sheet = workbook.createSheet("sample");
		HSSFSheet sheet2 = workbook2.createSheet("sample");
		
		List<String> Features = new ArrayList<String>();
		int rownum = 0;
		int column = 0;
		
		HSSFRow row = sheet.createRow(rownum++);
		
		while ((currentLine = br.readLine()) != null)
		{
			Features.add(currentLine.toLowerCase());
			
			Cell bidCell = row.createCell(column++);
			bidCell.setCellValue(currentLine.toLowerCase());
		}
		Cell bidCell = row.createCell(column++);
		bidCell.setCellValue("stars");
		
		
		String path = "tp.xls";
		
		FileInputStream file = new FileInputStream(new File(path));
		
		HSSFWorkbook workbook1 = new HSSFWorkbook(file);
		HSSFSheet sheet1 = workbook1.getSheetAt(0);
		HSSFRow row1 = null;
		HSSFRow row2 = null;
		int counter = 1;
		
		//File fout = new File("processInput.txt");
		//FileWriter fileWriter = new FileWriter(fout,false);	
		BufferedWriter bw = null;
		while(true)
		{	
			
			row1 = sheet1.getRow(counter);
			row2 = sheet2.createRow(rownum);
			row = sheet.createRow(rownum++);
			
			if(row1 != null)
			{
				int stars = (int)row1.getCell(0).getNumericCellValue();
				String text = row1.getCell(1).getStringCellValue();
			
				//String removed = text;
				counter++;
				//if (counter == 100)
				// 	break;
				System.out.println(counter);
				int column1 = 0;
				for(int i = 0; i < Features.size(); i++)
				{
					String feature = Features.get(i);
					
					Cell cell = row.createCell(column1++);
					
					if(text.toLowerCase().contains(feature.toLowerCase()))
						cell.setCellValue(1);
					else
						cell.setCellValue(0);
				}
				Cell cell = row.createCell(column1++);
				cell.setCellValue(stars);
				
				Cell cell2 = row2.createCell(1);
				cell2.setCellValue(text);
				
				Cell cell3 = row2.createCell(2);
				cell3.setCellValue(stars);
				
			}
			else
				break;
		}
		
		FileOutputStream out = new FileOutputStream(new File("final.xls"));
		workbook.write(out);
		
		FileOutputStream out1 = new FileOutputStream(new File("finalData.xls"));
		workbook2.write(out1);
		
		out.close();
	}

}
