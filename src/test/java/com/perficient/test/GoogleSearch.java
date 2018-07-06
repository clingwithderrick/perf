/**
 * A sample test script. 
 * @author Srinivasan Ramasamy
 * @version 1.0
 */

package com.perficient.test;

import org.testng.annotations.Test;
import com.perficient.core.TestDriver;

public class GoogleSearch extends TestDriver{
	
	@Test (dataProvider="data-provider", dataProviderClass=TestDriver.class, enabled=true)
	public void searchGoogle() throws Exception {
		open(data.get("URL"));
		verifyPresence("SearchBox");
		type("SearchBox", data.get("SearchString"));
		waitUntilElementPresent("SearchButton");
		click("SearchButton");
		verifyPresence("ResultsCount");
	}
	
}
