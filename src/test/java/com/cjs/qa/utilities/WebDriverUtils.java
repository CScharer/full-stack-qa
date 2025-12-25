package com.cjs.qa.utilities;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class WebDriverUtils {

  // final String PATH_SCREENSHOTS = "C:" + Constants.DELIMETER_PATH + "Temp"
  // + Constants.DELIMETER_PATH + "Screenshots" + Constants.DELIMETER_PATH;
  private static final String pathScreenshots = "./target/reports/Screenshots/";
  private WebDriver driver;

  public WebDriverUtils(WebDriver driver) {
    this.driver = driver;
  }

  public String getPathScreenshots() {
    return pathScreenshots;
  }

  public WebDriver getDriver() {
    return driver;
  }

  public void takeScreenshotDesktop(String path, String name, String extension) {
    if (path.isEmpty()) {
      path = pathScreenshots;
    }
    final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final GraphicsDevice[] screens = ge.getScreenDevices();
    final Rectangle allScreenBounds = new Rectangle();
    for (final GraphicsDevice screen : screens) {
      final Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
      allScreenBounds.width += screenBounds.width;
      allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
      allScreenBounds.x = Math.min(allScreenBounds.x, screenBounds.x);
      allScreenBounds.y = Math.min(allScreenBounds.y, screenBounds.y);
    }
    Robot robot;
    try {
      robot = new Robot();
      final BufferedImage bufferedImage = robot.createScreenCapture(allScreenBounds);
      final File file = new File(path + name + "." + extension);
      if (!file.exists()) {
        file.createNewFile();
      }
      try (FileOutputStream fos = new FileOutputStream(file)) {
        ImageIO.write(bufferedImage, extension, fos);
      }
    } catch (AWTException | IOException oException) {
      oException.printStackTrace();
    }
  }

  public void takeScreenshot(String path, String name, String extension) {
    if (path.isEmpty()) {
      path = pathScreenshots;
    }
    if (extension.isEmpty()) {
      extension = "png";
    }
    final Encoder encoder = new Encoder("");
    final String encodedValue = encoder.getEncodedValue("");
    final String decodedValue = encoder.getDecodedValue(encodedValue, "");
    name += "_" + encodedValue + "_" + decodedValue;
    if (driver != null) {
      final File screenShotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
      try {
        FileUtils.copyFile(screenShotFile, new File(path + name + "." + extension));
      } catch (final IOException oIOException) {
        oIOException.printStackTrace();
      }
    } else {
      takeScreenshotDesktop(path, name, extension);
    }
  }
}
