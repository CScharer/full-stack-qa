package com.cjs.qa.bts.pages;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;

public class TakeScreenshot {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(TakeScreenshot.class));

  public void screenshot(String browser, WebDriver webDriver) {
    final WebDriver augmentedDriver = new Augmenter().augment(webDriver);
    final File screenshot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
    try {
      final String sHostName = InetAddress.getLocalHost().getHostName();
      LOG.debug("HostName: [{}]", sHostName);
      final String sUserName = System.getProperty("user.name");
      LOG.debug("UserName: [{}]", sUserName);
      FileUtils.copyFile(screenshot, new File(Constants.PATH_DESKTOP + browser + "test.png"));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
