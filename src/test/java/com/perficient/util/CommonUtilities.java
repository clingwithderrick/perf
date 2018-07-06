package com.perficient.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.perficient.core.TestDriver;
import com.relevantcodes.extentreports.LogStatus;


public class CommonUtilities {
	
	public String getScreenshot(WebDriver driver, String Status) throws Exception {
		 File srcfile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		 File destfile = new File(new SimpleDateFormat("'reports/screenshots/" + TestDriver.data.get("TC_Name") + "_" + Status + "_Screenshot_'yyyyMMdd_hhmmss'.png'").format(new Date()));
		 FileUtils.copyFile(srcfile,destfile);
		 return destfile.getAbsolutePath();
	}

	public By getLocator(String strElement) throws Exception {
		String locator = TestDriver.objectRepository.getProperty(strElement);
		
		//Find the split point, i.e., the first colon(:) to split the locator type and value
		int i;
		for(i = 0; i < locator.length(); i++) {
			char j = locator.charAt(i);
			if(j == ':')
				break;
		}
			
		//Assign the locator type and it's value 
		String locatorType = locator.substring(0, i);
		String locatorValue = locator.substring(i + 1);

		if (locatorType.toLowerCase().equals("id"))
			return By.id(locatorValue);
		else if (locatorType.toLowerCase().equals("name"))
			return By.name(locatorValue);
		else if ((locatorType.toLowerCase().equals("classname")) || (locatorType.toLowerCase().equals("class")))
			return By.className(locatorValue);
		else if ((locatorType.toLowerCase().equals("tagname")) || (locatorType.toLowerCase().equals("tag")))
			return By.tagName(locatorValue);
		else if ((locatorType.toLowerCase().equals("linktext")) || (locatorType.toLowerCase().equals("link")))
			return By.linkText(locatorValue);
		else if (locatorType.toLowerCase().equals("partiallinktext"))
			return By.partialLinkText(locatorValue);
		else if ((locatorType.toLowerCase().equals("cssselector")) || (locatorType.toLowerCase().equals("css")))
			return By.cssSelector(locatorValue);
		else if (locatorType.toLowerCase().equals("xpath"))
			return By.xpath(locatorValue);
		else
			throw new Exception("Unknown locator type '" + locatorType + "'");
	}
	
	public WebElement getElement(String strElement) throws Exception{
		By by = getLocator(strElement);
		if (TestDriver.driver.findElements(by).size() <= 0)
			Assert.fail(String.format("%s element is not found on the page", strElement));
		return TestDriver.driver.findElement(by);
	}
	
	public Select getDropdown(String strElement) throws Exception{
		By by = getLocator(strElement);
		if (TestDriver.driver.findElements(by).size() <= 0)
			Assert.fail(String.format("%s element is not found on the page", strElement));
		return new Select(TestDriver.driver.findElement(by));
	}
	
	public void report(String Status, String Description, boolean isScreenshotNeeded) throws Exception{
		if (isScreenshotNeeded) {
			getScreenshot(TestDriver.driver, Status.toUpperCase());
		}
		switch (Status.toUpperCase()) {
			case "PASS": 
				TestDriver.test.log(LogStatus.PASS, Description); 
				break;
			case "FAIL": 
				TestDriver.test.log(LogStatus.FAIL, Description); 
				Assert.fail(Description);
				break;
			case "WARNING": 
				TestDriver.test.log(LogStatus.WARNING, Description); 
				break;
			case "INFO": 
				TestDriver.test.log(LogStatus.INFO, Description); 
				break;
			case "ERROR": 
				TestDriver.test.log(LogStatus.ERROR, Description); 
				Assert.fail(Description);
				break;
			default: 
				TestDriver.test.log(LogStatus.UNKNOWN, Description); 
				break;
		}
	}
	
	public void open(String strURL) throws Exception{
		TestDriver.driver.get(strURL);
		report("INFO", String.format("Navigated to page - %s", strURL), false);
	}	
	
	public void click(String strObjectName) throws Exception{
		 getElement(strObjectName).click();
		 report("INFO", String.format("%s clicked", strObjectName), false);
	}
	
	public void type(String strObjectName, String strText) throws Exception{
		getElement(strObjectName).sendKeys(strText);
		report("INFO", String.format("\"%s\" entered on %s", strText, strObjectName), false);
	}
	
	public void verifyPresence(String strObjectName) throws Exception{
		if (getElement(strObjectName) != null) 
			report("PASS", String.format("%s is present", strObjectName), true);
		else
			report("FAIL", String.format("%s is not present", strObjectName), true);
	}
	
	public void waitFor(long waitSeconds) throws InterruptedException {
		Thread.sleep(waitSeconds * 1000);
	}

	public WebElement waitUntilElementPresent(String element) throws Exception {
		return new WebDriverWait(TestDriver.driver, 30)
				.until(ExpectedConditions.presenceOfElementLocated(getLocator(element)));
	}
	
	public boolean waitUntilElementInvisible(String element) throws Exception {
		return new WebDriverWait(TestDriver.driver, 30)
				.until(ExpectedConditions.invisibilityOfElementLocated(getLocator(element)));
	}
	
	//Navigate To Menu
	public void goToMenu(String mainMenu, String... menuItem) throws Exception {
		boolean actionPerformed = false;
		try {
			Actions action = new Actions(TestDriver.driver);
			WebElement Mainmenu = getElement(mainMenu);
			if (menuItem.length > 0) {
				action.moveToElement(Mainmenu).build().perform();
				for (int i = 0; i < menuItem.length; i++) {
					By css = getLocator(menuItem[i]);
					if (i == menuItem.length - 1)
						action.moveToElement(new WebDriverWait(TestDriver.driver, 2)
								.until(ExpectedConditions.visibilityOfElementLocated(css))).click().build().perform();
					else
						action.moveToElement(new WebDriverWait(TestDriver.driver, 2)
								.until(ExpectedConditions.visibilityOfElementLocated(css))).build().perform();
				}
				actionPerformed = true;
			} else {
				action.moveToElement(Mainmenu).build().perform();
				actionPerformed = true;
			}
		} catch (Exception ex) {
			actionPerformed = false;
		}
		if (actionPerformed)
			report("PASS", String.format("Navigated to page - %s", mainMenu), false);
		else
			report("FAIL", String.format("Navigation failed "), true);
	}
	
	public void scrollToTop(){
		((JavascriptExecutor) TestDriver.driver).executeScript("window.scrollTo(0, 0);");
	}
	
	public void scrollToBottom(){
		((JavascriptExecutor) TestDriver.driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}
	
	public void scrollInToView(String strObjectName) throws Exception{
		((JavascriptExecutor) TestDriver.driver).executeScript("arguments[0].scrollIntoView(true);", getElement(strObjectName));
	}
	
	public void selecDropdown_ByVisibleText(String strObjectName, String strVisibleText) throws Exception {
		getDropdown(strObjectName).selectByVisibleText(strVisibleText);
		report("INFO", String.format("\"%s\" selected on the %s dropdown", strVisibleText, strObjectName), false);
		
	}

	public void selecDropdown_ByIndex(String strObjectName, int index) throws Exception {
		getDropdown(strObjectName).selectByIndex(index);
		report("INFO", String.format("Item selected on the %s dropdown", strObjectName), false);
	}

	public void switchToNewWindow() {
		ArrayList<String> tabs = new ArrayList<String>(TestDriver.driver.getWindowHandles());
		TestDriver.driver.switchTo().window(tabs.get(1));
	}

	public void switchToOldWindow() {
		ArrayList<String> tabs = new ArrayList<String>(TestDriver.driver.getWindowHandles());
		TestDriver.driver.switchTo().window(tabs.get(1));
		TestDriver.driver.close();
		TestDriver.driver.switchTo().window(tabs.get(0));
	}

	public void switchToiframe(String frameName) throws Exception {
		TestDriver.driver.switchTo().frame(frameName);
		waitFor(3);
	}
	
	public void verifyIfTextIsNotEmpty(String strObjectName) throws Exception{
		if (getElement(strObjectName).getText().trim() == null || getElement(strObjectName).getText().trim().length() <= 0)
			report("FAIL", String.format("%s is empty", strObjectName), true);
		else
			report("PASS", String.format("%s field is not empty as expected", strObjectName), true);
	}
	
	public void verifyElementText(String strObjectName, String strExpectedText) throws Exception{
		String strActualText = getElement(strObjectName).getText();
		
		if(strActualText.equalsIgnoreCase(strExpectedText))
			report("PASS", String.format("%s has the expected text \"%s\"", strObjectName, strExpectedText), true);
		else
			report("FAIL", String.format("%s does not contain the expected text \"%s\"", strObjectName, strExpectedText), true);
	}
	
	public String getRandomName(){
		String randomValue = Integer.toString((int)(Math.random()*1000000000));
		return "Test_" + randomValue.substring(0, 5);
	}
}
