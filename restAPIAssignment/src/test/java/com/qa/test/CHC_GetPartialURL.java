package com.qa.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.ingenico.connect.gateway.sdk.java.Factory;
import com.qa.base.TestBase;

import io.github.bonigarcia.wdm.WebDriverManager;



public class CHC_GetPartialURL extends TestBase {

	ExtentHtmlReporter reporter=new ExtentHtmlReporter("./extentReports/TestReport");
	ExtentReports extent=new ExtentReports();
	ExtentTest logger;
	@BeforeSuite
	public void setUp() throws ClientProtocolException, IOException{
		//System.setProperty("webdriver.chrome.driver","./BrowserDrivers/chromedriver.exe");
		WebDriverManager.chromedriver().setup();
		extent.attachReporter(reporter);
		logger=extent.createTest("IngenicoAPITest");
		logger.log(Status.INFO,"Test begins..");
		
		 testbase=new TestBase();
		currencyCode=prop.getProperty("currencyCode");
		localeLang=prop.getProperty("localeLang");
		countryCode=prop.getProperty("countryCode");
		merchantCustomerID=prop.getProperty("merchantCustomerID");
		apiKeyId=prop.getProperty("apiKeyId");
		secretKeyId=prop.getProperty("secretKeyId");
		String propertiesUrl="./ConfigParam/sdkConfig.properties";

		File f=new File(propertiesUrl);
		client = Factory.createClient(f.toURI(), apiKeyId, secretKeyId);
							 		
	}
	
	@Test (priority=1)
	public void CreateRequestBody(){
		
		try{
			SetSpecificInput();
		}
		catch (Exception e){
			e.printStackTrace();
			//assert.assertEquals(1, 0, "Error occurred while constructing request body.");
			logger.log(Status.FAIL, "Cannot create Request Body--" + e.getMessage());
			Assert.fail("Cannot set inputs.." + e.getMessage());
			return;
		}
		logger.log(Status.PASS, "Request Body is created-" + body.toString());
	}
	@Test (priority=2, dependsOnMethods={"CreateRequestBody"})

	public void SendReqest(){
		try{
			SendRequest();
			
		}
		catch (Exception e){
			e.printStackTrace();
			
			logger.log(Status.FAIL, "Response NOT received." + e.getMessage());
			Assert.fail("Cannot send request.." + e.getMessage());
			return;
		}
		logger.log(Status.PASS, "Request is sent successfully and response received.- " + response.toString());

	}
	
	@Test (priority=3, dependsOnMethods={"SendReqest"})

	public void LaunchURL() throws IOException{
		
		driver = new ChromeDriver();
		driver.get(urlToNavigate);
		logger.pass("Browser is lanched.", MediaEntityBuilder.createScreenCaptureFromPath(getScreenshotPath(driver)).build());


	}
	
	@Test (priority=4, dependsOnMethods={"LaunchURL"})

	public void EnterPaymentDetails() throws IOException{
		cardNumber=prop.getProperty("cardNumber");
		paymentType=prop.getProperty("paymentType");
		validThru=prop.getProperty("validThru");
		cvv=prop.getProperty("cvv");
		boolean bFound=false;

		//driver.findElement(By.xpath("//li//div[contains(.,'"+paymentType+"')]")).click();
		List <WebElement> li= driver.findElements(By.xpath("//li//div[contains(.,'"+paymentType+"')]"));
		for (int k=0;k<li.size();k++){
			String lText=li.get(k).getText().trim();	

			if (lText.equals(paymentType))	{
				li.get(k).click();
				bFound=true;
				break;
			}
			
		}
		
		if (bFound==false){
			Assert.assertEquals(bFound, true,"Payment method not found.");
			return;
		}

		
		driver.findElement(By.name("cardNumber")).sendKeys(cardNumber);
		driver.findElement(By.id("expiryDate")).sendKeys(validThru);
		driver.findElement(By.id("cvv")).sendKeys(cvv);
		//logger.log(Status.PASS, "Payment Details are added.");
		//logger.addScreenCaptureFromPath(getScreenshotPath(driver));
		logger.pass("Payment Details are added.", MediaEntityBuilder.createScreenCaptureFromPath(getScreenshotPath(driver)).build());
			driver.findElement(By.id("primaryButton")).click();
}
	
	@Test (priority=5, dependsOnMethods={"EnterPaymentDetails"})

	public void VerifyPayment(){
		logger.log(Status.PASS, "Payment is successfull.- //DUMMY STATEMENT- NO CARD DETAILS.");

	}
	
	@AfterSuite
	public void AfterTest(){
		extent.flush();
		driver.close();
		driver.quit();
	}
}
