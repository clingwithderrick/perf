/**
 * TestNG class which gets called for every test case. 
 * This is the driver class which runs for each test case. 
 * It reads the corresponding testdata from the input sheet and runs the actual test method 
 * and writes back the test results to the output sheet.
 * 
 */

package com.perficient.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import com.perficient.util.BrowserUtilities;
import com.perficient.util.CommonUtilities;
import com.perficient.util.ExtentManager;
import com.perficient.util.IOUtil;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

public class TestDriver extends CommonUtilities{

	public static LinkedHashMap<String, String> data;
	public static WebDriver driver;
	public static ExtentReports extent;
	public static ExtentTest test;
	public static Properties props;
	public static Properties objectRepository;
	public int intCurrentIteration = -1;
	
	//BeforeSuite sets up the configuration file and sets up the excel & html output files
	@BeforeSuite
	public void suiteSetup() throws IOException {
		File file = new File("user_config.properties");
		FileReader reader = new FileReader(file);
		props = new Properties();
		props.load(reader);		
		
		file = new File(props.getProperty("objectrepository"));
		reader = new FileReader(file);
		objectRepository = new Properties();
		objectRepository.load(reader);	
		
		extent = ExtentManager.Instance();
	}

	
	@BeforeMethod()
	public void InitializeTestIteration(Method result, ITestContext context) {
		for (int i=0; i<context.getAllTestMethods().length; i++){
			if (result.getName().equals(context.getAllTestMethods()[i].getMethodName()))
				intCurrentIteration = context.getAllTestMethods()[i].getCurrentInvocationCount()+1;
		}
		if (props.getProperty("whichinputfiletouse").equalsIgnoreCase("excel"))
			data = IOUtil.getInputData(result.getName(), intCurrentIteration);
		else if (props.getProperty("whichinputfiletouse").equalsIgnoreCase("json"))
			data = IOUtil.getInputDataFromJSON(result.getName(), intCurrentIteration);
		driver = BrowserUtilities.getBrowser(context.getCurrentXmlTest().getParameter("browsers"));
		test = extent.startTest(data.get("TC_Name") + " [Iteration"+Integer.toString(intCurrentIteration) + "]", data.get("TC_Description") + " [Broswer : " + context.getCurrentXmlTest().getParameter("browsers") + "]");
	}

	@DataProvider(name = "data-provider")
	public static Object[][] dataProvider(Method result) {
		int iterationCount;
		if (props.getProperty("whichinputfiletouse").equalsIgnoreCase("json"))
			iterationCount = IOUtil.getNumOfChildNodesFromJSON(result.getName());
		else
			iterationCount = IOUtil.getNumOfChildNodesFromExcel(result.getName());
		Object[][] obj = new Object[iterationCount][0];	
		return obj;
	}			

	//All the tear down operations occur. Since Reporting is done at the end of every test method
	@AfterMethod
	public void tearDown(ITestResult testResult){	
		if (driver != null)
	        driver.quit();
	    extent.endTest(test);
		data.clear();
	}
	
	//The extent reporting is done for each suite. The extent/HTML report is flushed and closed
	@AfterSuite
	public void flush() {
		extent.flush();
		extent.close();
	}

}
