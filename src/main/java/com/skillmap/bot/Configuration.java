package com.skillmap.bot;

import java.io.File;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import com.skillmap.bot.utils.XmlUtils;

@XmlRootElement(name = "whatsapp-config")
public class Configuration {
	
	@XmlElement
	private String botServerUrl;
	
	@XmlElement
	private String userAgent;
	
	@XmlElement
	private String webBrowser;
	
	@XmlElement
	private String messageToSend;
	
	@XmlElement
	private String searchTerm;
	
	private static Configuration config;
	private static File configFolder;
	
	private Configuration() {
	}
	
	public static void initializeConfiguration(File configFile) {
		config = XmlUtils.convertXmlToJavaObjectSilently(Configuration.class, configFile);
		configFolder = configFile.getParentFile();
	}
	
	public static String getUserAgent() {
		return config.userAgent;
	}

	public static boolean isChromeBrowser() {
		return StringUtils.equalsIgnoreCase(config.webBrowser, "chrome");
	}

	public static File getConfigurationFolder() {
		return configFolder;
	}
	
	public static String getMessageToSend() {
		return config.messageToSend;
	}
	
	public static String  getSearchTerm() {
		return config.searchTerm;
	}

	public static String getBotServerUrl() {
		return config.botServerUrl;
	}

}
