/**
 * A sample test script. 
 * @author Srinivasan Ramasamy
 * @version 1.0
 */

package com.perficient.test;

import org.testng.annotations.Test;
import com.perficient.core.TestDriver;

public class BingSearch extends TestDriver{
	
	@Test (dataProvider="data-provider", dataProviderClass=TestDriver.class, enabled=true)
	public void searchBing() throws Exception {
		open(data.get("URL"));
		verifyPresence("BingSearchBox");
		type("BingSearchBox", data.get("SearchString"));
		waitUntilElementPresent("BingSearchButton");
		click("BingSearchButton");
		verifyPresence("BingResults");
	}
	
}
