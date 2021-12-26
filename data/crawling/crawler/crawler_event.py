import selenium
from selenium import webdriver
from selenium.webdriver import ActionChains

from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By

from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait
from webdriver_manager.chrome import ChromeDriverManager # setting up the chromedriver automatically using webdriver_manager

import json
import sys
from tqdm import tqdm
import pandas as pd
import os
import re

import time

import mysql.connector
from mysql.connector.constants import ClientFlag

import requests
from bs4 import BeautifulSoup

from preprocess import preprocess1, preprocess2
from utils import startDriver


def findInfo(driver, path, name):
    try:
        result = driver.find_element_by_xpath(path).text
        return result
    except:
        print(name)
        result="해당없음"
        return result

def clickButton(driver, path, name):
    try:
        button = driver.find_element_by_xpath(path )
        button.click()
        driver.implicitly_wait(10)
    except:
        print(name)

def category_to_int(x):
    
    if x=="임신·출산": return 15
    elif x=="영유아" : return 0
    elif x=="아동·청소년" : return 1
    elif x=="청년" : return 2
    elif x=="중장년" : return 3
    elif x=="노년" : return 4
    elif x=='장애인' : return 5
    elif x=='한부모' : return 6
    elif x=='한부모·조손' : return 6
    elif x=='다문화(새터민)' : return 7
    elif x=='다문화·탈북민' : return 7
    elif x=='저소득층' : return 8
    elif x=='저소득' : return 8
    elif x=="교육" : return 9
    elif x=="고용" : return 10
    elif x=='일자리' : return 10
    elif x=="주거" : return 11
    elif x=="건강" : return 12
    elif x=="서민금융" : return 13
    elif x=="문화" : return 14
    elif x=='문화·여가' : return 14
    else: return False

welfare_df=pd.DataFrame(columns=['title', 'summary', 'who', 'what', 'category','start_date', 'end_date' ])
url = 'https://www.bokjiro.go.kr/ssis-teu/twataa/wlfareInfo/moveTWAT52005M.do'
driver = startDriver(url)
all_page = 0

for _ in range(3):
    for page in range(1,6):
        print("-----------page:", 5*(all_page//15)+page, "gone--------------");
        all_page += page
        for idx in [7,8,10,11,13,14]:
            
            #민간 태그로 옮기기
            category_path = '/html/body/div[1]/div[1]/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div[1]/div/div[3]/div/div/div'
            clickButton(driver, category_path, "민간태그")

            # 해당 페이지로 가기
            if 30>=all_page>15: 
                next_button_path = '/html/body/div[1]/div[1]/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div[4]/div/div/div/div[2]/div/div/div/div[3]'
                clickButton(driver, next_button_path, "다음 페이지 버튼")
                print("success")
            if all_page==33: 
                print("----done------")
                break
            if all_page>30:
                clickButton(driver, next_button_path, "다음 페이지 버튼")
                print("success")
                clickButton(driver, next_button_path, "다음 페이지 버튼")
                print("success")
            time.sleep(3)
            page_path = '/html/body/div[1]/div[1]/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div[4]/div/div/div/div[2]/div/div/div/div[2]/div['+str(page)+']/div/div'
            clickButton(driver, page_path, "해당 페이지로 가기")
            # button = driver.find_element_by_xpath(page_path )
            # button.click()
            # driver.implicitly_wait(10)

            # 제목 
            title_path = '/html/body/div[1]/div[1]/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div['+str(idx)+']/div/div/div[3]/div/div/div/div[2]/div/div/div'
            title = findInfo(driver, title_path, "title")

            # 지원기간
            date_path='/html/body/div[1]/div[1]/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div['+str(idx)+']/div/div/div[4]/div/div/div/div[10]/div/div'
            date = findInfo(driver, date_path, "date")
            [start_date, end_date] = date.split('~')
            start_date = re.sub('\n', '', start_date)
            from datetime import date
            if (date.fromisoformat(start_date)>date.fromisoformat(end_date)) : 
                driver.get(url)
                continue

            # 유형(카테고리)
            classification=''
            for num in range(1,4):
                try:
                    classification_path = '/html/body/div[1]/div[1]/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div['+str(idx)+']/div/div/div[2]/div/div/div/div[4]/div/div/div['+str(num)+']/div/div/div'
                    classification_text = driver.find_element_by_xpath(classification_path).text
                    classification_previous = category_to_int(classification_text)
                    if classification_previous:
                        classification = classification+','+str(classification_previous)
                except:
                    pass

            # 자세히 보기 클릭
            try:
                detail_button_path = '/html/body/div[1]/div[1]/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div['+str(idx)+']/div/div/div[5]/div/a/div'
                detail_button= driver.find_element_by_xpath(detail_button_path)
                driver.execute_script("arguments[0].click();", detail_button)
                #detail_button.click()
                driver.implicitly_wait(10)
            except:
                print("----------오류 --자세히보기----------")

            # summary
            summary_path = '/html/body/div[1]/div[1]/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div[3]/div/div/div'
            summary = findInfo(driver, summary_path, "summary")
            summary = preprocess1(summary)

            # who
            who_path = '/html/body/div[1]/div[1]/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[3]/div/div/div'
            who = findInfo(driver, who_path, "who")
            who = preprocess1(who)
            
            # what
            what_path = '/html/body/div[1]/div[1]/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div[4]/div/div/div/div[3]/div/div/div'
            what = findInfo(driver, what_path, "what")
            what = preprocess1(what)

            # calls
            # time.sleep(3)
            # calls_path = '/html/body/div[1]/div[1]/div/div[3]/div/div/div/div[3]/div/div/div/div[2]/div/div/div/div[2]/div/div/div/div[3]/div/div/div/div[5]/div/div/div[3]/div/div/div/div/div/div/div[4]/div/div/div'
            # result = driver.find_element_by_xpath(calls_path).text
            # calls = findInfo(driver, calls_path, "calls")
            # print(calls)

            if title!='해당없음' and title:
                new_welfare_list=[(title, summary,who, what, classification ,start_date, end_date )]
                dfNew= pd.DataFrame(new_welfare_list, columns=['title', 'summary', 'who', 'what',  'category','start_date', 'end_date' ])
                welfare_df=welfare_df.append(dfNew, ignore_index=True)

            driver.back()
            time.sleep(3)
            driver.implicitly_wait(10)
            print("-----------idx:", idx, "done--------------");

welfare_df_copy = welfare_df


welfare_df = welfare_df.drop_duplicates(keep='first', ignore_index=True)
welfare_df = welfare_df.drop_duplicates(['title'], keep='first', ignore_index=True)
welfare_df.to_csv("민간.csv", index=False, encoding='utf-8-sig')

