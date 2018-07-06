# seleniumframework
The Selenium Test Framework Proposal

# How to setup
---
The first step towards integrating or building the project would be to import the project work into the eclipse workspace. 

The user setup file is located at the root directory of your project. This file allows customisation of the various directories and wait times. Any changes to this file will reflect in the locations of the reports and required test data (Input.xls). It is advised to not alter the names of the properties, rather, customise the directories alone.

The Test Data comprises of the excel file – Input.xls which contains the values of the various “Common parameters” which are common to the entire test suite and the test level parameters in the succeeding rows which are common to a single test method. 

The User is advised to key in all the required values with the corresponding headers ensuring that the value under the Heading  **TC_Name** is the name of the **@Test Method** as this is the main identifier while getting data from the excel file.

The user is to input all the necessary test data into this excel file maintaining the existing scheme:

Each row representing a single @Test method.

The Test case which is the function which performs the automated tests should be prefixed with the @Test Annotation. It must be under the test package – com.perficient.test

The following format is to be adhered:

The Test method MUST have the parameter String BrowserName as an argument

- The entire test method body must be enclosed within a try-catch block with the recover() function being called in case an exception is caught.
- Every Test method should call the report function whenever a checkpoint is reached or a log is needed.  
- Within the report() function, the user has an option to take a screenshot of the current state of the test. It can be enabled by inputting the Boolean value “true” as the last argument in the report() function.
- Utilization of the parameters linked hash map and the associated get() method to obtain the necessary values from the Input File
- The parameters map is present during the entire run time of the test method and houses all the important test parameters along with the suite level common parameters.

The Page factory is a functional component of the Test suite acting as an object repository.

It contains all the object and element instantiations housing the wrapper methods which can be called in the Test case. 

The running of the programme is accomplished via the XML file Suite.xml. 

The xml file should contain the suite-test tags which either encloses package tags or class tags.

This determines the Test Execution and reporting. 

Each @Test Method will be considered as a single test case.

At the end of execution, the user can view the reports and logs that are generated as an excel output file and a HTML extent report file in the reports directory by default or in the user defined directory. The reports directory contains the excel reports, html reports and the screenshots. 

# Components of the framework
---

### TestDriver

This is the class where all the TestNG annotated methods are present.

Here you modularize your program and break it down into a set of functionalities and based on the frequency of occurrence or calling of these functionalities, we categorize them under the corresponding before and after method annotations.

There are 8 Before and and After Method annotations.

- Before Suite - After Suite
- Before Test – After Test
- Before Class – After Class
- Before Method – After Method

The Test driver houses all these annotations and their corresponding methods.

The Before Suite method invokes and sets up the various reporting functionalities and loads the user properties file. This is performed once before the entire Testing Suite.

The Before method gets the input data from the excel file and starts the HTML extent report.

The After method ends the excel reporting.

The After suite acts as a flush closing the HTML reporting.

The Data Provider supplies the string of browsers to each before method and each test method.

### User Properties file

This houses all the user customizable fields such as the location of the test data, the location of the reports, the maximum wait time, etc. The user is allowed to modify the properties file as per the requirement. Though it is suggested to follow the established scheme.

### IOUtil

This is the main utilities class for the excel reporting functionalities. It has methods to setup write and read data from excel spreadsheets.

The getInputData accepts the String which is the name of the test method as a parameter. Returns the LinkedHashMap of data read from the Input excel file (the corresponding row whose TC_Name is the same as the test method name).

The setupExcelOutput method creates an excel file at the user defined location which is where all the events and checkpoints during the testing is logged.

The writeExcelOutput method is logs the various parameters under the corresponding headings in the output excel file that is previously set up.

### BrowserUtilities

This utilities class is where the web driver element is setup and the event logging is enabled. The supported browsers are Firefox, Internet Explorer, Chrome.

### CommonUtilities

This class houses all the common utility methods such as the getScreenshot(), sendKeys(), getText(), isElementPresent(), pageLoadTimeout() and most importantly report()and recover().

The report function is called whenever a checkpoint is reached or the result (i.e) PASS or FAIL is to be logged. This writes into both the excel output file as well as the HTML extent report

### ExtentManager

This class contains the Instance()function which returns an object of the type ExtentReport. It is used to initially setup the HTML ExtentReport.

### Suite.xml

This is the xml file that has the various suite level and test level tags. The user is advised to give appropriate names for the suite tag and the test tags.

This is the file that acts as panel operating the entire test suite.

The user is to indent all the test cases within the ***<test> </test>*** tags.

The user can either perform the test at the class level using the ***<class> <class name=” “> </class>*** or at a package level using ***<package> <package name=””> </package>***






# perf
