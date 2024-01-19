package com.sample.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class Base {
	/* Minimum requirement for test configuration */
	public static Properties pr;
	private InputStream is;
	public static int passedtestcases = 0;
	public static int failedtestcases = 0;
	public static int totaltestcases = 0;
	public static int failedSno = 0;
	public static int passedSno = 0;
	public static String Subject;
	public static String failed;
	public static String module;
	protected static String test_data_folder_path = null;
	protected static String screenshot_folder_path = null;
	public static String currentTest; // current running test
	protected WebDriver driver;
	private ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<WebDriver>();
	public static ThreadLocal<Integer> steps = new ThreadLocal<Integer>();

	/* Page's declaration */

	public AbstractPage abstractPage;
	public ConfigFileReader configFileReader;
	public Common common;

	/**
	 * Fetches suite-configuration from XML suite file.
	 * 
	 * @param testContext
	 */
	@BeforeSuite(alwaysRun = true)
	public void fetchSuiteConfiguration() {
		try {
			pr = new Properties();
			is = getClass().getClassLoader().getResourceAsStream("config.properties");
			pr.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp(Method method) throws Exception {
		currentTest = method.getName(); // get Name of current test.
		String SCREENSHOT_FOLDER_NAME = "screenshots";
		String TESTDATA_FOLDER_NAME = "test_data";
		test_data_folder_path = new File(TESTDATA_FOLDER_NAME).getAbsolutePath();
		screenshot_folder_path = new File(SCREENSHOT_FOLDER_NAME).getAbsolutePath();
		String targetBrowser = pr.getProperty("browser");
		String driverPathWindow = pr.getProperty("driverPathWindow");
		String driverPathMac = pr.getProperty("driverPathMac");
		String driverPathFirefox = pr.getProperty("driverPathFirefox");
		String driverPathEdge = pr.getProperty("driverPathEdge");
		if (targetBrowser.equalsIgnoreCase("firefox")) {
			FirefoxProfile profile = new FirefoxProfile();
			System.setProperty("webdriver.gecko.driver", driverPathFirefox);
			FirefoxOptions options = new FirefoxOptions();
			options.setBinary("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
			options.addArguments("--headless");
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			threadLocalDriver.set(new FirefoxDriver(options));
			driver = threadLocalDriver.get();
		} else if (targetBrowser.equalsIgnoreCase("edge")) {
			System.setProperty("webdriver.edge.driver", driverPathEdge);
			EdgeOptions options = new EdgeOptions();
			options.addArguments("--headless");
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			options.setAcceptInsecureCerts(true);
			options.merge(capabilities);
			options.addArguments("--remote-allow-origins=*");
			threadLocalDriver.set(new EdgeDriver(options));
			driver = threadLocalDriver.get();
		} else if (targetBrowser.equalsIgnoreCase("chrome")) {
			// System.setProperty("webdriver.chrome.driver",driverPathWindow);
			System.setProperty("webdriver.chrome.driver", "/Users/apple/Documents/HHP/Projects/drivers/chromedriver");
			ChromeOptions options = new ChromeOptions();
			// options.addArguments("--headless");
			options.addArguments("--remote-allow-origins=*");
			options.setAcceptInsecureCerts(true);
			// WebDriverManager.chromedriver().setup();
			threadLocalDriver.set(new ChromeDriver(options));
			driver = threadLocalDriver.get();
		} else {
			throw new Exception("Browser is not correct");
		}
		driver.manage().window().maximize();
		steps.set(1);
		common = new Common(driver);

	}

	@AfterMethod(alwaysRun = true)
	public void tearDown(ITestResult testResult) throws Exception {
		Reporter.setCurrentTestResult(testResult);
		String testName = testResult.getName();
		Reporter.setCurrentTestResult(testResult);
		File img = new File("target" + File.separator + "failsafe-reports" + File.separator + testName + ".png");
		if (testResult.isSuccess()) {
			common.log("PASS : " + testResult.getName());
		} else if (!testResult.isSuccess()) {
			failed = testResult.getMethod().getDescription();
			module = "<a href=" + driver.getCurrentUrl() + ">" + driver.getClass().getSimpleName() + "</a>";
			failedSno++;
			common.log("FAIL : " + testResult.getName());
			Common.takeSnapShot(driver, testName);
			failedtestcases++;

		} else {

			System.out.println("TEST SKIPPED - " + testName + "\n");
		}

		if (driver != null) {
			driver.quit();
			threadLocalDriver.remove();
		}
	}

	@AfterSuite(alwaysRun = true)
	public void after() {
	}

}