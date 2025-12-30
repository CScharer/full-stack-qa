# -*- coding: utf-8 -*-
"""Robot Framework library for automatic WebDriver management using webdriver-manager."""
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.service import Service
import os


class WebDriverManager:
    """Library for managing WebDriver setup automatically."""
    
    ROBOT_LIBRARY_SCOPE = 'GLOBAL'
    
    def setup_chromedriver(self):
        """Set up ChromeDriver automatically using webdriver-manager.
        
        This sets the CHROMEDRIVER_PATH environment variable that SeleniumLibrary can use.
        Returns the path to the ChromeDriver executable.
        """
        try:
            driver_path = ChromeDriverManager().install()
            os.environ['CHROMEDRIVER_PATH'] = driver_path
            # Also set PATH so SeleniumLibrary can find it
            current_path = os.environ.get('PATH', '')
            driver_dir = os.path.dirname(driver_path)
            if driver_dir not in current_path:
                os.environ['PATH'] = driver_dir + ":" + current_path
            print("✅ ChromeDriver automatically installed at: " + driver_path)
            return driver_path
        except Exception as e:
            print("❌ Failed to set up ChromeDriver: " + str(e))
            raise
    
    def get_chromedriver_path(self):
        """Get the ChromeDriver path (sets it up if not already done)."""
        if 'CHROMEDRIVER_PATH' in os.environ:
            return os.environ['CHROMEDRIVER_PATH']
        return self.setup_chromedriver()
