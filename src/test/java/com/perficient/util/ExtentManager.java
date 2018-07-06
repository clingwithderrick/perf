package com.perficient.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.perficient.core.TestDriver;
import com.relevantcodes.extentreports.ExtentReports;

public class ExtentManager {

	private static File path = new File(TestDriver.props.getProperty("extentreportpath"));
	
	//Set up of the Extent Report    
	public static ExtentReports Instance()
	{
		ExtentReports extent;
		String Path = path.getAbsolutePath() + "\\" + new SimpleDateFormat("'ExtentReport_'yyyy_MM_dd_HHmmss'.html'").format(new Date());
		extent = new ExtentReports(Path, false);
		File config = new File(TestDriver.props.getProperty("extentreportconfigpath"));
		extent.loadConfig(config);
		return extent;
	}
}