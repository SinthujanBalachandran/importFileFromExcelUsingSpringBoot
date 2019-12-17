package com.example.demo.services;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.User;
import com.example.demo.repos.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepo userRepo;

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return (List<User>) userRepo.findAll();
	}

	@Override
	public boolean saveDataFromUploadFile(MultipartFile file) {
		boolean isFlag = false;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (extension.equalsIgnoreCase("json")) {
			isFlag = readDataFromJson(file);
		} else if (extension.equalsIgnoreCase("CSV")) {
			isFlag = readDataFromCsv(file);
		} else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx") ) {
			isFlag = readDataFromExcel(file);
		}
		return isFlag;
	}

	private boolean readDataFromExcel(MultipartFile file) {
		Workbook workbook = getWorkBook(file);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		rows.next();
		while(rows.hasNext()) {
			Row row = rows.next();
			User user = new User();
			if(row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
				user.setFirstName(row.getCell(0).getStringCellValue());
			}if(row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
				user.setLastName(row.getCell(1).getStringCellValue());
			}if(row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
				user.setEmail(row.getCell(2).getStringCellValue());
			}if(row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
				String phoneNumber=NumberToTextConverter.toText(row.getCell(3).getNumericCellValue());
				user.setPhoneNumber(phoneNumber);
			}else if(row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING) {
				
				user.setPhoneNumber(row.getCell(3).getStringCellValue());
			}
			user.setFileType(FilenameUtils.getExtension(file.getOriginalFilename()));
			userRepo.save(user);
		}
		
		return true;
	}

	private Workbook getWorkBook(MultipartFile file) {
		Workbook workbook = null;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		try {
			if(extension.equalsIgnoreCase("xls")) {
				workbook = new XSSFWorkbook(file.getInputStream());
			}else if(extension.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(file.getInputStream());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}

	private boolean readDataFromCsv(MultipartFile file) {
		try {
			InputStreamReader reader = new InputStreamReader(file.getInputStream());
			CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
			List<String[]> rows = csvReader.readAll();
			for (String[] row : rows) {
				userRepo.save(new User(row[0], row[1], row[2], row[3],
						FilenameUtils.getExtension(file.getOriginalFilename())));
			}
			return true;
		} catch (Exception e) {

		}
		return false;
	}

	private boolean readDataFromJson(MultipartFile file) {
		try {
			InputStream inputStream = file.getInputStream();
			ObjectMapper mapper = new ObjectMapper();
			List<User> users = Arrays.asList(mapper.readValue(inputStream, User[].class));
			if (users != null && users.size() > 0) {
				for (User user : users) {
					user.setFileType(FilenameUtils.getExtension(file.getOriginalFilename()));
					userRepo.save(user);
				}
			}
			return true;
		} catch (Exception e) {

		}
		return false;
	}
}
