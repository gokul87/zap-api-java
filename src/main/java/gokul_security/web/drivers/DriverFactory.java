package gokul_security.web.drivers;

import java.io.File;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.openqa.selenium.phantomjs.*;

public class DriverFactory {

	private final static String CHROME = "chrome";
	private final static String FIREFOX = "firefox";
	static Logger log = Logger.getLogger(Driver.class.getName());
	
	public static WebDriver createProxyDriver(String type, Proxy proxy,String path)
	{
		if(type.equalsIgnoreCase(CHROME)) return createChromeDriver(createProxyCapabilities(proxy), path);
		throw new RuntimeException("Unknown webdriver browser "+ type);			
	}
	
	public static WebDriver createChromeDriver(DesiredCapabilities capabilities, String path)
	{
		System.setProperty("webdriver.chrome.driver", path);
		if(capabilities != null) {
		   capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		   return new ChromeDriver(capabilities);
		} else return new ChromeDriver();
	}

	
	public static PhantomJSDriver phantomJsDriver(String driverPath) {
	   
	    DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
	    capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, driverPath);
	    capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCapabilities());
	    return new PhantomJSDriver(capabilities);
	}

	private static List<String> cliArgsCapabilities() {
	    List<String> cliArgsCap = new ArrayList<String>();
	    cliArgsCap.add("--proxy=localhost:8090");
	    return cliArgsCap;
	}
	
	public static DesiredCapabilities createProxyCapabilities(Proxy proxy)
	{
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability("proxy", proxy);
		return capabilities;
	}
}
