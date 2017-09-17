package gokul_security_test.gokul_security_test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import gokul_security.web.drivers.DriverFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.zaproxy.clientapi.core.Alert;
import org.zaproxy.clientapi.core.Alert.Confidence;
import org.zaproxy.clientapi.core.Alert.Risk;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppScanTest 
{
	static Logger log = Logger.getLogger(AppScanTest.class.getName());
	final static String ZAP_PROXYHOST = "localhost";
	final static int ZAP_PROXYPORT = 8090;
	final static String ZAP_APIKEY = null;
	private WebDriver driver;
	private ClientApi api;
	public String scanid;
	private final static String MEDIUM = "MEDIUM";
    private final static String HIGH = "HIGH";
	public int progress;
	public ApiResponse resp;
	public int count=0;
	
	
	final static String CHROME_DRIVER_PATH = "drivers/chromedriver.exe";
	final static String PHANTOM_DRIVER_PATH = "/usr/bin/phantomjs";
    private AppNavigation app;
    
    @Before
    public void setUp() throws IOException
    {	
    	api = new ClientApi(ZAP_PROXYHOST, ZAP_PROXYPORT);
    	//driver = DriverFactory.createProxyDriver("chrome", createZapProxyConfigurationForWebDriver(), CHROME_DRIVER_PATH);
    	//driver = DriverFactory.createProxyDriver("firefox",createZapProxyConfigurationForWebDriver(),"");
    	driver = DriverFactory.phantomJsDriver(PHANTOM_DRIVER_PATH);
    	app = new AppNavigation(driver);
    }
    
    @After
    public void tearDown()
    {
    	driver.quit();
    }
    
    @Test
    public void testSite() throws InterruptedException, ClientApiException, FileNotFoundException
    {
    	
    	app.navigateToLogin();
    	spiderWithZap();
    	scanWithZap();
    	fetchAlerts("results_1.html");
    }
    
    @Test
    public void testPersonalLink() throws InterruptedException, ClientApiException, FileNotFoundException
    {
    	app.navigateToPersonal();
    	spiderWithZap();
    	scanWithZap();
    	fetchAlerts("results_2.html");
      }
    
    public void fetchAlerts(String output) throws ClientApiException, FileNotFoundException
    {
    	List<Alert> alert = api.getAlerts("", 0, 0);
    	
    	for(Iterator iterator = alert.iterator(); iterator.hasNext(); ) {
    		Alert alert2 = (Alert)iterator.next();
    		
    		if (alert2.getRisk().equals(Alert.Risk.High) || alert2.getRisk().equals(Alert.Risk.Medium)){
    			
    			if (alert2.getConfidence() != Alert.Confidence.Low) {
    				count++;
    				System.out.println("the no of alert found: "+count);
    				System.out.println("-----Alert is-----" + alert2.getAlert());
            	    System.out.println("URL is::  " + alert2.getUrl());
            	    System.out.println("Risk is::  " + alert2.getRisk());
            	    //System.out.println("No of non-low alerts are: "+ alert.);
    			} 
    		}
    	}

		System.out.println("HTML Report");
		PrintWriter outputFile = new PrintWriter(output);
	    outputFile.write(new String(api.core.htmlreport(ZAP_APIKEY)));
	    System.out.println("Scanning stops");  
	    
    	assertThat(count, equalTo(0));
    }
    
	public static Proxy createZapProxyConfigurationForWebDriver()
    {
		Proxy proxy = new Proxy();
		
    	proxy.setHttpProxy(ZAP_PROXYHOST + ":" +  ZAP_PROXYPORT);
    	proxy.setSslProxy(ZAP_PROXYHOST + ":" + ZAP_PROXYPORT);
    	return proxy;
    }
    
    private void spiderWithZap() throws InterruptedException, ClientApiException
    {
    	try {
    		System.out.println("Spider : " + app.BASE_URL);
    		resp = api.spider.scan(ZAP_APIKEY,app.BASE_URL,null,null,null,null);
    		
    		scanid = ((ApiResponseElement) resp).getValue();
    		System.out.println("this is the scanned id "+ scanid);
    		
    		 // Poll the status until it completes
            while (true) {
                Thread.sleep(1000);
                progress = Integer.parseInt(((ApiResponseElement) api.spider.status(scanid)).getValue());
                System.out.println("Spider progress : " + progress + "%");
                if (progress >= 100) {
                    break;
                }
            }
            System.out.println("Spider complete");
    		
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }
    
    private void scanWithZap() throws InterruptedException, ClientApiException
    {
    	try{
    		System.out.println("Active scan : " + app.BASE_URL);
            resp = api.ascan.scan(ZAP_APIKEY, app.BASE_URL, "True", "False", null, null, null);

            // The scan now returns a scan id to support concurrent scanning
            scanid = ((ApiResponseElement) resp).getValue();

            // Poll the status until it completes
            while (true) {
                Thread.sleep(5000);
                progress = Integer.parseInt(((ApiResponseElement) api.ascan.status(scanid)).getValue());
                System.out.println("Active Scan progress : " + progress + "%");
                if (progress >= 100) {
                    break;
                }
            }
            System.out.println("Active Scan complete");
            
            
          //  System.out.println("Alerts:");
          //  System.out.println(new String(api.core.xmlreport(ZAP_APIKEY)));
    	} catch (InterruptedException e) {
    		e.getMessage();
    	}
    }
}
