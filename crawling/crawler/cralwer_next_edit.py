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


page_num=19
#page=1
#block=1


df=pd.DataFrame(columns=['title', 'summary', 'who', 'criteria', 'what', 'how', 'calls', 'sites','category'])
df_check=pd.DataFrame(columns=['page', 'block'])

#def crawler_url(page_num):

for try_num in range(5):
    
    options = webdriver.ChromeOptions()
    options.add_argument('window-size=1920,1080')
    options.add_experimental_option('excludeSwitches', ['enable-logging'])
    #options.add_argument('--use-gl=desktop')
    
    driver_path=os.getcwd()+'\chromedriver.exe'
    driver = webdriver.Chrome(driver_path, options=options) #ChromeDriverManager().install()
    page_num=int(page_num)
    
    url="http://www.bokjiro.go.kr/welInfo/retrieveWelInfoBoxList.do"
    driver.get(url)
    driver.implicitly_wait(3)

    
    # 카테고리 가져오기
    #div=1
    for div in range(1,17):
        
        driver.get(url)
        
        if 0<div<7:
            path='//*[@id="catLeftColor"]/li['+str(div)+"]/a"
            division=driver.find_element_by_xpath(path)
            division.click()
            driver.implicitly_wait(3)
            
            category_name=driver.find_element_by_xpath(path+'/span').text
            
        elif 7<=div<11:
            div_2=div-6
            path='//*[@id="catCenterColor"]/li['+str(div_2)+"]/a"
            division=driver.find_element_by_xpath(path)
            division.click()
            driver.implicitly_wait(3)
            
            category_name=driver.find_element_by_xpath(path+'/span').text
        else:
            div_3=div-10
            path='//*[@id="catRightColor"]/li['+str(div_3)+"]/a"
            division=driver.find_element_by_xpath(path)
            division.click()
            driver.implicitly_wait(3)
            
            category_name=driver.find_element_by_xpath(path+'/span').text
            
        # page=2    
        # 페이지 넘기기
        for page in tqdm(range(1,page_num+1),mininterval=1):
            print('category name :', category_name,'page :', page )
            
            if 3<=div<7:
                try:
                    # 끝 페이지 처리하기
                    if page==1:
                        pass
                    elif page==11:
                        path='//*[@id="contents"]/div[4]/div/a[12]'
                        category = driver.find_element_by_xpath(path)
                        category.click()
                        driver.implicitly_wait(3)
                    elif page>11: # 12page의 div/a[3]
                        path='//*[@id="contents"]/div[4]/div/a['+str(page%11+2)+']'
                        category = driver.find_element_by_xpath(path)
                        category.click()
                        driver.implicitly_wait(3)                        
                    else: #2 페이지 div/a[3]
                        path='//*[@id="contents"]/div[4]/div/a['+str(page%11+1)+']'
                        category = driver.find_element_by_xpath(path)
                        category.click()
                        driver.implicitly_wait(3)
                except:
                    print('html page does not exist :',category_name, "page :", page)
                    break
            else:
                try:
                    if page==1:
                        pass
                    else:
                        path='//*[@id="contents"]/div[4]/div/a['+str(page-1)+']'
                        category = driver.find_element_by_xpath(path)
                        category.click()
                        driver.implicitly_wait(3)
                except:
                    print('html page does not exist :',category_name, "page :", page)
                    break             
                
            try:
                # page 당 10개씩 있는 정책 가져오기
                for block in tqdm(range(1,11),mininterval=1):
                    #print("page :", page, "block :", block, "시작합니다" )
                    # category를 찾아서 들어가기
                    path='//*[@id="contents"]/div[4]/ul/li['+str(block)+']/div/a[2]'
                    category = driver.find_element_by_xpath(path)
                    category.click()
                    driver.implicitly_wait(3)
                    
                    # category url 가져오기
                    #url_list=driver.current_url
                    soup = BeautifulSoup(driver.page_source, "html.parser")
                    
                    
                    # # category_name 가져오기
                    # if 0<div<7:
                    #     category_name='//*[@id="catLeftColor"]/li['+str(div)+']/a/span'
                    # elif 7<=div<11:
                    #     div_2=div-6
                    #     category_name='//*[@id="catCenterColor"]/li['+str(div_2)+']/a/span'
                    # else:
                    #     div_3=div-10
                    #     category_name='//*[@id="catRightColor"]/li['+str(div_3)+']/a/span'
                    
                    
                    # title 가져오기
                    title=soup.select_one('#contents > div.bokjiDetailWrap > h4 > span')
                    title=title.get_text()
                    if title=='학교정보': #다른 링크로 가는 사이트
                        continue
                    
                    # summary
                    summary=soup.select_one('#contents > div.bokjiDetailWrap > div')
                    summary=summary.get_text()
                    summary=preprocess1(summary)
                    
                    # who
                    if soup.select('#backup > div:nth-child(1) > div > ul > li.first > p'):   
                        path='#backup > div:nth-child(1) > div > ul > li.first > p'
                        who=soup.select_one(path)
                        who=who.get_text()
                    elif soup.select('#backup > div:nth-child(1) > div > ul > li.first > ul > li'):
                        path='#backup > div:nth-child(1) > div > ul > li.first > ul > li'
                        who=soup.select_one(path)
                        who=who.get_text()
                    elif soup.select('#backup > div:nth-child(1) > div > ul > li > ul > li'):
                        path='#backup > div:nth-child(1) > div > ul > li > ul > li'
                        who=soup.select_one(path)
                        who=who.get_text()
                    else:
                        who=''
                    if who:
                        who=preprocess1(who)
                    
                    # criteria
                    if soup.select('#backup > div:nth-child(1) > div > ul > li:nth-child(2) > p'):   
                        path='#backup > div:nth-child(1) > div > ul > li:nth-child(2) > p'
                        criteria=soup.select_one(path)
                        criteria=criteria.get_text()
                    elif soup.select('#backup > div:nth-child(1) > div > ul > li:nth-child(2) > ul'):
                        path='#backup > div:nth-child(1) > div > ul > li:nth-child(2) > ul'
                        criteria=soup.select_one(path)
                        criteria=criteria.get_text()
                    else:
                        criteria=''
                    if criteria:
                        criteria=preprocess1(criteria)
                    
                    # what
                    try:
                        what=soup.select_one('#backup > div:nth-child(2) > div > ul > li > ul > li')
                        what=what.get_text()
                    except:
                        what=''
                    if what:
                        what=preprocess1(what)
                    driver.implicitly_wait(3)
                    
                    # how
                    if soup.select('#backup > div:nth-child(3) > div > ul.bokjiContsIn > li.first > ul > li'):   
                        path='#backup > div:nth-child(3) > div > ul.bokjiContsIn > li.first > ul > li'
                        how=soup.select_one(path)
                        how=how.get_text()
                    elif soup.select('#backup > div:nth-child(3) > div > ul > li > ul > li'):
                        path='#backup > div:nth-child(3) > div > ul > li > ul > li'
                        how=soup.select_one(path)
                        how=how.get_text()
                    else:
                        how=''
                    if how:
                        how=preprocess1(how)
                    
                    # call
                    try:
                        call=soup.select_one('#contents > div:nth-child(7) > div.bokjiServiceView > div > div > ul > li.phone > ul > li')
                        call=call.get_text()
                    except:
                        call=''
                    if call:
                        call=preprocess2(call)
                    
                    # site
                    try:
                        site=soup.select_one('#contents > div:nth-child(7) > div.bokjiServiceView > div > div > ul > li.internet > ul > li')
                        site=site.get_text()
                    except:
                        site=''
                    if site:
                        site=preprocess2(site)
                    
                    # dataframe에 가져온 category url 저장                
                    new_welfare_list=[(title, summary, who, criteria, what, how, call, site, category_name)]
                    dfNew= pd.DataFrame(new_welfare_list, columns=['title', 'summary', 'who', 'criteria', 'what', 'how', 'calls', 'sites','category'])
                    df=df.append(dfNew, ignore_index=True)
                    #print("page :", page, "block :", block, "저장합니다" )
                    df_checkNew= pd.DataFrame([(page, block)], columns=['page', 'block'])
                    df_check=df_check.append(df_checkNew, ignore_index=True)

                    driver.back()
                    driver.implicitly_wait(3)

            except:
                print('html page does not exist :',category_name, "block :", block)
                continue
    
    driver.quit()

    #return df




# 중복된 값 확인하기

len(df)
len(df.drop_duplicates(['title'],keep='first')) #356개

len(df_check)
len(df_check.drop_duplicates(['page', 'block'], keep='first'))

# 중복된 값들 유일하게 만들기
df=df.drop_duplicates(['title'],keep='first')

# 크롤링 끝날 때까지 보존하기
import copy
df_copy=copy.deepcopy(df) #임신출산, 영유아

# category를 db에 맞게 int로 바꿔주기
def category_to_int(x):
    if x=="임신·출산": return 0
    elif x=="영유아" : return 1
    elif x=="아동·청소년" : return 2
    elif x=="청년" : return 3
    elif x=="중장년" : return 4
    elif x=="노년" : return 5
    elif x=='장애인' : return 6
    elif x=='한부모' : return 7
    elif x=='다문화(새터민)' : return 8
    elif x=='저소득층' : return 9
    elif x=="고용" : return 10
    elif x=="주거" : return 11
    elif x=="건강" : return 12
    elif x=="교육" : return 13

#df['category']=df['category'].apply(lambda x: 0 if x=="임신·출산" else 1)
df['category']=df['category'].apply(lambda x: category_to_int(x))
df.info()
df['category']=df.category.astype(int)
df['category']=df.category.astype(str)
#df.to_csv("임신출산영유아.csv", index=False, encoding='utf-8-sig')
#df_check.to_csv("임신출산영유아_확인.csv", index=False, encoding='utf-8-sig' )

# db에 dataframe 저장
os.chdir("./crawler")
import config


# now we establish our connection
cnxn = mysql.connector.connect(**config.DATABASE_CONFIG)
cursor = cnxn.cursor(prepared=True)

## welfare commit
#query = ("""INSERT INTO welfare (title, summary) VALUES (%s, %s)""") #table name 바꾸는 거 기억하기
#input_data=[tuple(x) for x in result_final.to_records(index=False)]
#cursor.executemany(query, input_data) #error 나는 이유가 tuple이 아니라 numpy.record이기 때문
#
#cnxn.commit()  # and commit changes
#cnxn.close() 

result_final=copy.deepcopy(df) 
# welfare_detail commit
query = ("""INSERT INTO welfare (title, summary, who,criteria, what, how, calls, sites, category) VALUES (%s, %s, %s, %s, %s, %s,  %s,  %s, %s)""") #table name 바꾸는 거 기억하기
input_data=[tuple(x) for x in result_final.to_records(index=False)]
cursor.executemany(query, input_data) #error 나는 이유가 tuple이 아니라 numpy.record이기 때문

cnxn.commit()  # and commit changes
cnxn.close() 

result_final.to_csv("크롤링최종1.csv", index=False)



