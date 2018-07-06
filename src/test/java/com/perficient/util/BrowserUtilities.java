package com.perficient.util;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import com.perficient.core.TestDriver;

public class BrowserUtilities {
	
	private static WebDriver driver;
	private static File path = new File(TestDriver.props.getProperty("driverfilepath"));
	
	//derive the  required Browser
	public static WebDriver getBrowser(String browserName){
		if(browserName.equalsIgnoreCase("FF") || browserName.equalsIgnoreCase("Firefox")){
			driver = getFirefoxBrowser();
		} else if(browserName.equalsIgnoreCase("IE") || browserName.equalsIgnoreCase("InternetExplorer")){
			driver = getInternetExplorerDriver();
		} else if(browserName.equalsIgnoreCase("Chrome")){
			driver = getChromeDriver();
		}
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Integer.parseInt(TestDriver.props.getProperty("timeout")), TimeUnit.SECONDS);
		return driver;
	}
	
	//load FF browser and enable the logs
	public static WebDriver getFirefoxBrowser(){
		new DesiredCapabilities();
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		
		LoggingPreferences logs = new LoggingPreferences();
		logs.enable(LogType.BROWSER, Level.ALL);
		logs.enable(LogType.CLIENT, Level.ALL);
		logs.enable(LogType.DRIVER, Level.INFO);
		logs.enable(LogType.PERFORMANCE, Level.ALL);
		logs.enable(LogType.PROFILER, Level.ALL);
		logs.enable(LogType.SERVER, Level.ALL);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);
		
		System.setProperty("webdriver.gecko.driver", path.getAbsolutePath() + "\\geckodriver.exe");
		driver = new FirefoxDriver(capabilities);
		return driver;
	}
	
	//load IE browser and enable the logs
	public static WebDriver getInternetExplorerDriver(){
		new DesiredCapabilities();
		DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		
		LoggingPreferences logs = new LoggingPreferences();
		logs.enable(LogType.BROWSER, Level.ALL);
		logs.enable(LogType.CLIENT, Level.ALL);
		logs.enable(LogType. DRIVER, Level.INFO);
		logs.enable(LogType.PERFORMANCE, Level.ALL);
		logs.enable(LogType.PROFILER, Level.ALL);
		logs.enable(LogType.SERVER, Level.ALL);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);
		
		capabilities.setJavascriptEnabled(true);
		System.setProperty("webdriver.ie.driver", path.getAbsolutePath() + "\\IEDriverServer.exe");
		driver = new InternetExplorerDriver(capabilities);
		return driver;
	}
	
	//load Chrome browser and enable the logs
	public static WebDriver getChromeDriver(){
		new DesiredCapabilities();
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--test-type");
		options.addArguments("--no-sandbox");
		capabilities.setCapability(ChromeOptions.CAPABILITY,options);
		
		LoggingPreferences logs = new LoggingPreferences();
		logs.enable(LogType.BROWSER, Level.ALL);
		logs.enable(LogType.DRIVER, Level.INFO);
		logs.enable(LogType.PERFORMANCE, Level.ALL);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);
		
		System.setProperty("webdriver.chrome.driver", path.getAbsolutePath() + "\\chromedriver.exe");
		driver = new ChromeDriver(capabilities);
		return driver;
	}

}
