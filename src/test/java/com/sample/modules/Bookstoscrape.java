package com.sample.modules;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.sample.utils.AbstractPage;
import com.sample.utils.Base;

public class Bookstoscrape extends Base {

	AbstractPage ap = new AbstractPage(driver) {
	};

	@Test
	public void openBooksToScrape() throws InterruptedException {
		String url = "http://books.toscrape.com/";
		common.log("Step :: Open URL :- " + url);
		driver.get(url);
		int allrows = 0;
		int total_Categories = driver.findElements(By.xpath(ap.ALL_CATEGORIES)).size();

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Sheet1");
		Row row = null;
		
		for (int i = 1; i <= total_Categories; i++) {
			String xPath = ap.ALL_CATEGORIES + "[" + i + "]";
			WebElement category = common.waitUntilElementToBeClickable(By.xpath(xPath));
			String categoryName = category.getText().trim();
			common.log("categoryName : " + categoryName);
			common.log("Step :: Click on " + categoryName + " category.");
			category.click();
			String header = common.getText(ap.CATEGORY_HEADER);
			common.log("Step :: Verify " + categoryName + " category page opened.");
			Assert.assertTrue(categoryName.equalsIgnoreCase(header));
			int totalBooks = driver.findElements(By.xpath(ap.ALL_BOOKS)).size();
			if (allrows == 0) {
				row = sheet.createRow(0);
				row.createCell(0).setCellValue("Category");
				row.createCell(1).setCellValue("Boook Name");
				row.createCell(2).setCellValue("UPC");
				row.createCell(3).setCellValue("Type");
				row.createCell(4).setCellValue("Price (excl. Tax)");
				row.createCell(5).setCellValue("Price (incl. Tax)");
				row.createCell(6).setCellValue("Tax");
				row.createCell(7).setCellValue("Availability");
				row.createCell(8).setCellValue("Reviews");
			}

			for (int k = 1; k <= totalBooks; k++) {
				allrows++;
				String book_xpath = ap.ALL_BOOKS + "[" + k + "]";
				WebElement book = common.waitUntilElementToBeClickable(By.xpath(book_xpath));
				String bookName = book.getText().trim();
				common.log("Step :: Click on " + bookName + " book.");
				book.click();
				int total_Info = driver.findElements(By.xpath(ap.BOOK_INFO)).size();
				row = sheet.createRow(allrows);
				row.createCell(0).setCellValue(categoryName);
				row.createCell(1).setCellValue(bookName);
				for (int j = 1; j <= total_Info; j++) {

					String xpathBookInfo = ap.BOOK_INFO + "[" + j + "]";
					String bookInfo = common.getText(xpathBookInfo);
					String xpathBookInfoValuue = ap.BOOK_INFO_VALUE + "[" + j + "]";
					String bookinfoValue = common.getText(xpathBookInfoValuue);
					common.log(j + ". " + bookInfo + " : " + bookinfoValue);
					row.createCell(j+1).setCellValue(bookinfoValue);
				}
				driver.navigate().back();
			}

		}
		try (FileOutputStream fileOut = new FileOutputStream("ScrapingData.xlsx")) {
			workbook.write(fileOut);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}