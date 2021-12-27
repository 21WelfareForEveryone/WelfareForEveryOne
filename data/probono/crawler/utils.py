import selenium
from selenium import webdriver
from selenium.webdriver import ActionChains

from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By

from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait
from webdriver_manager.chrome import ChromeDriverManager # setting up the chromedriver automatically using webdriver_manager

import os

def startDriver(url):
    options = webdriver.ChromeOptions()
    options.add_argument('window-size=1920,1080')
    options.add_experimental_option('excludeSwitches', ['enable-logging'])
    #options.add_argument('--use-gl=desktop')

    driver_path=os.getcwd()+'/chromedriver'
    driver = webdriver.Chrome(executable_path=driver_path, options=options) #ChromeDriverManager().install()
    ## driver = webdriver.Chrome(ChromeDriverManager().install())

    driver.get(url)
    driver.implicitly_wait(3)

    return driver
