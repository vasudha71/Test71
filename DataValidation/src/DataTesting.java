
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
//import com.sun.xml.internal.bind.v2.util.FatalAdapter;

public class DataTesting {

	WebDriver driver = null;
	
	AccountDetails sampleAccount =null;
	AccountDetails totalActual =null;
	AccountDetails committedRevenue =null;
	AccountDetails backLogRevenue =null;
	AccountDetails recurrentRevenue =null;
	AccountDetails conservative =null;
	AccountDetails comitted =  null;
	
	List<AccountDetails> conservativeSubList = null;
	List<AccountDetails> committedSubList  = null;
	public static void main(String[] args) {
		
		
		DataTesting testobj = new DataTesting();
		try {
			testobj.executeProcess();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error in main ::"+e.getMessage());
		}
		
		

	}
	
	public void executeProcess() throws Exception{
		
		System.out.println("In executeProcess");
		// Load Webdriver and clicking the account
		clickingTheAccount();
		
		//loadAccountDetails
		processAccountDetailsAndvalidate();
		System.out.println("Exiting executeProcess");
	}
	
	public void clickingTheAccount() throws Exception{
		System.out.println("Clicking the Account:: ");
		
		System.setProperty("webdriver.chrome.driver","D:\\chromedriver.exe");
		//driver =new FirefoxDriver();
		driver=new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		//driver.get("https://login.salesforce.com/");
		driver.get("http://3.208.226.214:3000/ce-dashboard/financial-view");
		/*driver.findElement(By.xpath(".//input[@id='username']")).sendKeys(
				"vasudha.katragunta@revnav.com");
		driver.findElement(By.xpath(".//input[@id='password']")).sendKeys(
				"Vasudha@123");
		driver.findElement(By.xpath(".//input[@id='Login']")).click();*/
		driver.findElement(By.xpath(".//*[text()='Sourcing Assumptions']")).click();
		Thread.sleep(6000);
		System.out.println("User Logged On");
		
		try{
			
		Thread.sleep(10000);
		WebElement element = driver.findElement(By
				.xpath(".//span[contains(text(),'FCST Forecast Workbench')]"));
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].click();", element);
		Thread.sleep(10000);
		WebElement ele1 = driver.findElement(By
				.xpath(".//*[@name='SampleAccount']"));
		ele1.click();
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		// jse.executeScript("scroll(0, 1000);");
		jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		Thread.sleep(2000);
		System.out.println("Done Scrolling");
		driver.findElement(
				By.xpath("(.//*[@class='slds-button slds-button_icon expandbutton slds-button_icon-xx-small slds-button_icon-border'])[3]"))
				.click();
		
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		System.out.println("Completed the clicking account ");
		
	}
	
	public void processAccountDetailsAndvalidate() throws Exception{
		System.out.println("In processAccountDetailsAndvalidate ");
		int rowStartIndex = 3;
		int colStartIndex = 2;
		try{
			 sampleAccount =  loadAccountInfo(driver, rowStartIndex, colStartIndex);
			 totalActual =  loadAccountInfo(driver, ++rowStartIndex, colStartIndex);
			 committedRevenue =  loadAccountInfo(driver, ++rowStartIndex, colStartIndex);
			 backLogRevenue =  loadAccountInfo(driver, ++rowStartIndex, colStartIndex);
			 recurrentRevenue =  loadAccountInfo(driver, ++rowStartIndex, colStartIndex);
			 conservative =  loadAccountInfo(driver, ++rowStartIndex, colStartIndex);
			 conservativeSubList  = new ArrayList<AccountDetails>();
			rowStartIndex++;
			int committedStartIndex = rowStartIndex;
			System.out.println("Committed StartIndex :: " +committedStartIndex);
			
			
			while(!getValueAtPath(driver, rowStartIndex, 1)){
				AccountDetails accountDetail = loadAccountInfo(driver, rowStartIndex, colStartIndex);
				conservativeSubList.add(accountDetail);
				rowStartIndex++;
			}
			
			
			System.out.println("Account Details :: "+sampleAccount);
			System.out.println("totalActual :: "+totalActual);
			System.out.println("committedRevenue:: "+committedRevenue);
			System.out.println("backLogRevenue :: "+backLogRevenue);
			System.out.println("recurrentRevenue :: "+recurrentRevenue);
			System.out.println("conservative:: "+conservative);
			System.out.println("conservativeSubList :: "+conservativeSubList);
			
			// Click button for commit - Xpath to be changed
			driver.findElement(
					By.xpath("//*[@id=\"layout1\"]/table/tr["+rowStartIndex+"]/th[1]/p/button"))
					.click();
			
			
			comitted =  loadAccountInfo(driver, committedStartIndex, colStartIndex);
			committedStartIndex++;
			committedSubList  = new ArrayList<AccountDetails>();
			while(!getValueAtPath(driver, committedStartIndex, 1)){
				AccountDetails accountDetail = loadAccountInfo(driver, committedStartIndex, colStartIndex);
				committedSubList.add(accountDetail);
				committedStartIndex++;
			}
		
			System.out.println("comitted:: "+comitted);
			System.out.println("committedSubList:: "+committedSubList);
			
			validateAllAccountScenarios();
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		System.out.println("Exiting processAccountDetailsAndvalidate ");
	}
	
	public void validateAllAccountScenarios() throws IllegalAccessException{
		System.out.println("In validateAllAccountScenarios ");
		try {
			if (validateAccountAgainstList(conservative, conservativeSubList,
						"Conversative")
						&& validateAccountAgainstList(comitted, committedSubList,
								"Committed")) {
				System.out.println("Both Conservative and Committed validations are successful");
				List<AccountDetails> committedRevenueList = new ArrayList<AccountDetails>();
				committedRevenueList.add(backLogRevenue);
				committedRevenueList.add(recurrentRevenue);
				committedRevenueList.add(conservative);
				committedRevenueList.add(comitted);
				
				
				if(validateAccountAgainstList(committedRevenue, committedRevenueList,
						"CommittedRevenue")){
					System.out.println("Committed Revenue Validation Is SuccessFul");
					if(validateAccount(sampleAccount, totalActual, committedRevenue)){
						System.out.println("Every Validation is Successful");
					}
					
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		System.out.println("Exiting validateAllAccountScenarious ");
	}
	
	public AccountDetails loadAccountInfo(WebDriver webDriver, int row , int col ){
		
		 AccountDetails accountDetail = new AccountDetails();
		 String xpath = ".//*[@id='layout1']/table/tr[row]/th[col]";
		 accountDetail.setM1(getUpdateXpath(xpath, webDriver, row, col));
		 accountDetail.setM2(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setM3(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setQuarter1(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setM4(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setM5(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setM6(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setQuarter2(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setM7(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setM8(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setM9(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setQuarter3(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setM10(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setM11(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setM12(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setQuarter4(getUpdateXpath(xpath, webDriver, row, ++col));
		 accountDetail.setTotal(getUpdateXpath(xpath, webDriver, row, ++col));
		 
		 return accountDetail;
	}

	public  int getUpdateXpath(String xpath, WebDriver driver, int row, int col){
		
		xpath = xpath.replaceAll("row", String.valueOf(row));
		xpath = xpath.replaceAll("col", String.valueOf(col));
		
		try{
			String value = driver.findElement(By.xpath(xpath)).getText();
			value = value.replaceAll("\\$", "");
			value = value.replaceAll(",", "");
			
			return Integer.parseInt(value);
		}catch(Exception e){
			System.out.println("Error in getUpdate Xpath :: "+xpath);
			e.printStackTrace();
			throw e;
		}
		
		
	}
	
public  boolean  getValueAtPath( WebDriver driver, int row, int col){
		
	 	String xpath = ".//*[@id='layout1']/table/tr[row]/th[col]";
		xpath = xpath.replaceAll("row", String.valueOf(row));
		xpath = xpath.replaceAll("col", String.valueOf(col));
		
		try{
			WebElement element =driver.findElement(By.xpath(xpath));
			
			String value  = element.getText();
			
			
			if(value.contains("Committed")){
				return true;
			}else{
				return false;
			}
		}catch(NoSuchElementException ne){
			System.out.println("No such element :: ");
			return true;
		}
		
	}
	

public  boolean validateAccountAgainstList(AccountDetails mainAccount, List<AccountDetails> accountDetailSubList, String accountDetailName) throws IllegalArgumentException, IllegalAccessException{
	System.out.println("In validateAccountAgainstList"); 
	
	Field[] fields = AccountDetails.class.getDeclaredFields();
	
	boolean flag = true;
	 
	 for(Field field : fields) {  
		 System.out.println("Field Name :: "+field.getName());
		 
		 int mainAccountValue = field.getInt(mainAccount);
		 int compareToValue = 0;
		 for(AccountDetails accountDetail : accountDetailSubList){
			 compareToValue = compareToValue+field.getInt(accountDetail);
		 }
		 
		 if(mainAccountValue!=compareToValue){
			 System.out.println("Opportunites Value Doesnt Match with Total Account For "+accountDetailName+" at "+field.getName());
			 flag = false;
			 break;
		 }
	 }
	 System.out.println("Exiting validateAccountAgainstList"); 
	 return flag;
}	

public  boolean validateAccount(AccountDetails mainAccount, AccountDetails actualAccount, AccountDetails conversativeRevenueAcc) throws IllegalArgumentException, IllegalAccessException{
	System.out.println("In validateAccount"); 
	
	Field[] fields = AccountDetails.class.getDeclaredFields();
	 boolean flag = true;
	 for(int i = 0; i < fields.length; i++) {  
       
		 Field field = fields[i];
		 String fieldName = field.getName();
		 System.out.println("Field Name :: "+fieldName);
		 int mainAccountValue = field.getInt(mainAccount);
		 int actualAccountValue = field.getInt(actualAccount);
		 int conservativeRevenueValue = field.getInt(conversativeRevenueAcc);
		 
		 if(actualAccountValue!=0 && actualAccountValue==mainAccountValue){
			 System.out.println("We have Actual value and It match with MainAccount :: " +fieldName);
		 }else  if(conservativeRevenueValue!=0 && conservativeRevenueValue==mainAccountValue){
			 System.out.println("We have Conversative value and It match with MainAccount :: "+fieldName);
		 }else{
			 System.out.println("Neither Actual Value nor Conversative revenue matches with Main Account for fieldName :: "+fieldName);
			 flag= false;
			 break;
		 }
	}  
	 System.out.println("Exiting validateAccount"); 

	 return flag;
}
}

