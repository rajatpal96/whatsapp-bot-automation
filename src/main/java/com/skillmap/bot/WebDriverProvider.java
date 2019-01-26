package com.skillmap.bot;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class WebDriverProvider {
	
	private static WebDriver driver = null;
	
	public static WebDriver getWebDriver() {
		if (driver != null) {
			return driver;
		}
		String osName = System.getProperty("os.name");
		String osArch = System.getProperty("os.arch");
		String fileName = null;
		if(StringUtils.containsIgnoreCase(osName, "mac"))
			fileName = "/macos/{fileName}";
		else if(StringUtils.containsIgnoreCase(osName, "nix") || StringUtils.containsIgnoreCase(osName, "nux") || StringUtils.containsIgnoreCase(osName, "aix")) {
			if(osArch.contains("64"))
				fileName = "/linux64/{fileName}";
			else
				fileName = "/linux32/{fileName}";
		} else if (StringUtils.containsIgnoreCase(osName, "win")) {
			if(osArch.contains("64"))
				fileName = "/win64/{fileName}.exe";
			else
				fileName = "/win32/{fileName}.exe";
		}
		if (Configuration.isChromeBrowser()) {
			fileName = StringUtils.replace(fileName, "{fileName}", "chromedriver");
			System.setProperty("webdriver.chrome.driver",Configuration.getConfigurationFolder().getAbsolutePath() + fileName);
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--incognito");
			options.addArguments("--no-sandbox");
			driver = new ChromeDriver(options);
		} else {
			fileName = StringUtils.replace(fileName, "{fileName}", "geckodriver");
			System.setProperty("webdriver.gecko.driver",Configuration.getConfigurationFolder().getAbsolutePath() + fileName);
			FirefoxProfile profile = new FirefoxProfile();
//			profile.setPreference("general.useragent.override", Configuration.getUserAgent());
			profile.setPreference("browser.privatebrowsing.autostart", true);
			profile.setPreference("dom.max_chrome_script_run_time", 0);
			profile.setPreference("dom.max_script_run_time", 0);
			driver = new FirefoxDriver(profile);
		}
		return driver;
	}

}
