package com.skillmap.bot;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.entity.ContentType;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skillmap.bot.utils.HttpRequestTask;
import com.skillmap.bot.utils.HttpRequestTask.RequestType;
import com.skillmap.bot.utils.HttpUtils;
import com.skillmap.bot.utils.JSONUtils;
import com.skillmap.bot.utils.TaskResult;

public class WhatsappBot {

	private static final Logger logger = LoggerFactory.getLogger(WhatsappBot.class);
	public static  String message="";

	public static void main(String[] args) throws Exception {
		Configuration.initializeConfiguration(new File(args[0], "config.xml"));
		String phone_csv = args[1];
		WebDriver driver = WebDriverProvider.getWebDriver();
		driver.get("https://web.whatsapp.com/");
		Thread.sleep(10000);
		logger.info("Please scan Qr code to use whatsWeb");
		// sendMessageToAllContact();
		sendMessageUsingPhone(phone_csv); 
	}

	public static void sendMessageToAllContact() throws InterruptedException {

		WebDriver driver = WebDriverProvider.getWebDriver();
		WebDriverWait wait = new WebDriverWait(driver, 10000);
		wait.until(ExpectedConditions.or(
				ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".jN-F5.copyable-text.selectable-text"))));
		WebElement searchBox = driver.findElement(By.cssSelector(".jN-F5.copyable-text.selectable-text"));
		searchBox.click();
		Thread.sleep(10000);
		searchBox.sendKeys(Configuration.getSearchTerm());
		Thread.sleep(1000);
		List<WebElement> allEle = driver.findElements(By.cssSelector("._2wP_Y"));
		int totalEle = allEle.size();
		logger.info("Total contact founds : {} ", totalEle);
		for (WebElement ele : allEle) {
			if (totalEle == 0)
				logger.info("No results found please try with different search term ");

			int i = 0;
			i++;
			if (i == totalEle)
				return;
			String chatName = ele.findElement(By.cssSelector("span>span")).getAttribute("title");
			if (chatName == null)
				continue;
			driver.findElement(By.cssSelector("span[title='" + chatName + "']"));
			ele.click();
			Thread.sleep(1000);
			logger.info("Sending message to : {} ", chatName);
			WebElement composeBox = driver.findElement(By.cssSelector("._2S1VP.copyable-text.selectable-text"));
			composeBox.clear();
			composeBox.sendKeys(Configuration.getMessageToSend());
			Thread.sleep(2000);

			WebElement sendButon = driver.findElement(By.cssSelector("._35EW6"));
			Thread.sleep(10000);
			sendButon.click();
			logger.info("Message sent successfully to  : {}", chatName);
		}

	}

	public static void sendMessageUsingPhone(String phone_csv) throws InterruptedException, IOException {
		message=getResultFromUrl();
		WebDriver driver = WebDriverProvider.getWebDriver();
		WebDriverWait wait = new WebDriverWait(driver, 10000);
		wait.until(ExpectedConditions.or(
				ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".jN-F5.copyable-text.selectable-text"))));
		WebElement searchBox = driver.findElement(By.cssSelector(".jN-F5.copyable-text.selectable-text"));
		List<String> phoneNoColl = Arrays.asList(phone_csv.split("\\s*,\\s*")).stream().collect(Collectors.toList());
		for (String phone : phoneNoColl) {
			Thread.sleep(10000);
			searchBox.click();
			searchBox.clear();
			searchBox.sendKeys(phone);
			Thread.sleep(6000);
			try {
				WebElement allEle = driver
						.findElement(By.cssSelector("#pane-side > div > div > div > div:nth-child(1)"));
				Thread.sleep(3000);
				allEle.click();
			} catch (org.openqa.selenium.NoSuchElementException e) {
				logger.info("Exception occurd : {} ", e.getMessage());
				continue;
			}
                   
			Thread.sleep(4000);
			logger.info("Sending message to : {} ", phone);
			WebElement composeBox = driver.findElement(By.cssSelector("._2S1VP.copyable-text.selectable-text"));
			composeBox.clear();
			Thread.sleep(1000);
			String msgNew = org.apache.commons.lang3.StringUtils.replacePattern(message,
					"\\r\\n|\\r|\\n", Keys.chord(Keys.SHIFT, Keys.ENTER));
			composeBox.sendKeys(msgNew);

			Thread.sleep(5000);
			logger.info("WhatsApp message is : {}",message);
             
			WebElement sendButon = driver.findElement(By.cssSelector("._35EW6"));
			Thread.sleep(5000);
			sendButon.click();
			logger.info("Message sent successfully to  : {}", phone);
		}

	}
	/*this method is used to get response from url using jobId and hiringCode
	 *and param get from command line -DjobId=123456
	 *and hCode get from command line -DhCode="Oyoggn"
	 */
	public  static String getResultFromUrl() throws IOException{
		String jobId=System.getProperty("jobId");
		String hiringCode=System.getProperty("hCode");
		Map<String,Object> param=new HashMap<>();
		param.put("jobId",jobId );
		param.put("hiringCode", hiringCode);
		HttpRequestTask task=new HttpRequestTask(RequestType.post, Configuration.getBotServerUrl(), 
				ContentType.APPLICATION_FORM_URLENCODED, param);
		logger.info("Sendin request to server with headers :{}, : {}",jobId,hiringCode);
	    TaskResult result=HttpUtils.getResultFromUrl(task, true);
	    Map<String, Object> msg=JSONUtils.getJsonMapFromString(result.getData());
	    String whatsAppMsg=(String) msg.get("whatsAppTemplate");
		return whatsAppMsg;
	}
}
