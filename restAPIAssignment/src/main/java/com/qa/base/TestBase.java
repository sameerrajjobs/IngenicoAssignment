package com.qa.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.io.Files;
import com.ingenico.connect.gateway.sdk.java.Client;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.Address;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.AmountOfMoney;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutRequest;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutResponse;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.definitions.HostedCheckoutSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Customer;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Order;

public class TestBase {

	public static WebDriver driver ;
	public static CreateHostedCheckoutResponse response;
	public static URI uri;	
	public static Client client;
	public static CreateHostedCheckoutRequest body;
	public static String urlToNavigate;
	public static String currencyCode,localeLang, countryCode, merchantCustomerID,apiKeyId,secretKeyId,cardNumber,paymentType,validThru,cvv;
	public Properties prop;
	public static TestBase testbase;
	public TestBase() { //constructor to load properties file
		try{
		prop= new Properties();
		FileInputStream ip= new FileInputStream("./ConfigParam/config.properties");		
		prop.load(ip);
		
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
			System.out.println("file not found exception...");
		}
		catch(IOException e){
			e.printStackTrace();
			System.out.println("IO exception...");

		}
	}
	
	public void SetSpecificInput(){
		HostedCheckoutSpecificInput hostedCheckoutSpecificInput = new HostedCheckoutSpecificInput();
		hostedCheckoutSpecificInput.setLocale(localeLang);
		hostedCheckoutSpecificInput.setVariant("testVariant");

		AmountOfMoney amountOfMoney = new AmountOfMoney();
		amountOfMoney.setAmount(2345L);
		amountOfMoney.setCurrencyCode(currencyCode);

		Address billingAddress = new Address();
		billingAddress.setCountryCode(countryCode);

		Customer customer = new Customer();
		customer.setBillingAddress(billingAddress);
		customer.setMerchantCustomerId(merchantCustomerID);

		Order order = new Order();
		order.setAmountOfMoney(amountOfMoney);
		order.setCustomer(customer);

		body = new CreateHostedCheckoutRequest();
		body.setHostedCheckoutSpecificInput(hostedCheckoutSpecificInput);
		body.setOrder(order);
		
	}
	

	public void SendRequest (){
		 response = client.merchant(merchantCustomerID).hostedcheckouts().create(body);
		urlToNavigate="https://payment." + response.getPartialRedirectUrl();
		//System.out.println("URL is -" + urlToNavigate);
	}
	
	public static String getScreenshotPath(WebDriver driver){
		String path=System.getProperty("user.dir") + "/screenshots/" + System.currentTimeMillis() +".png";
		TakesScreenshot ts= (TakesScreenshot) driver;
		File src=ts.getScreenshotAs(OutputType.FILE);
		File destination=new File(path);
		try{
			Files.copy(src,destination);
		
		}
		catch(IOException e){
			
		}
		
		return path;
		
	}
}
