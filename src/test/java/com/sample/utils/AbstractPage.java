package com.sample.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public abstract class AbstractPage extends Base {

	public AbstractPage(WebDriver driver) {

		this.driver = driver;

	}
	public String ALL_CATEGORIES = "(//div[@class='side_categories']//ul//li//ul//li//a)";
	public String CATEGORY_HEADER = "//h1";
	public String ALL_BOOKS = "(//h3//a)";
	public String BOOK_INFO = "(//tbody//tr//th)";
	public String BOOK_INFO_VALUE = "(//tbody//tr//td)";
}