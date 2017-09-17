package gokul_security_test.gokul_security_test;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Hello world!
 *
 */
public class AppNavigation 
{
	WebDriver driver;
	public String BASE_URL="http://demo.testfire.net/bank/login.aspx";
	//public String BASE_URL="http://localhost:8889/bodgeit/";
	
	public AppNavigation(WebDriver driver) 
	{
		this.driver=driver;
		this.driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
    public void navigateToLogin()
    {
    	driver.get(BASE_URL);
    	driver.findElement(By.id("_ctl0__ctl0_Content_LinkHeader2")).click();
    	driver.findElement(By.cssSelector("h1")).isDisplayed();
    }
    
    public void navigateToPersonal()
    {
    	driver.get(BASE_URL);
    	driver.findElement(By.id("_ctl0__ctl0_Content_LinkHeader3")).click();
    	driver.findElement(By.cssSelector("h1")).isDisplayed();
    }
}
