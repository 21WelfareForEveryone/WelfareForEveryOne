import pandas as pd
import torch
from torch.utils.data import DataLoader, Dataset
from transformers import AutoTokenizer, AutoModel
import numpy as np
import pymysql

import user_based_collaborative_filtering as ubcf

from dotenv import load_dotenv
import os

# -*- coding: utf-8 -*-
from flask import Flask, request, jsonify

load_dotenv()  # env 파일 Load
#################################################################


# CHATBOT
class TrainDataset(Dataset):

    def __init__(self, dataset):
        self.tokenizer = AutoTokenizer.from_pretrained("beomi/KcELECTRA-base")

        self.sentences = [str([i[0]]) for i in dataset]
        self.labels = [np.int32(i[1]) for i in dataset]

    def __len__(self):
        return len(self.labels)

    def __getitem__(self, i):
        text = self.sentences[i]
        y = self.labels[i]

        inputs = self.tokenizer(
            text,
            return_tensors='pt',
            truncation=True,
            max_length=64,
            pad_to_max_length=True,
            add_special_tokens=True
        )

        input_ids = inputs['input_ids'][0]
        attention_mask = inputs['attention_mask'][0]

        return input_ids, attention_mask, y


def load_model():
    chatbot_model = AutoModel.from_pretrained("beomi/KcELECTRA-base", num_labels=363)

    chatbot_model.load_state_dict(torch.load('./model.pt', map_location=device))

    return chatbot_model


def predict(sentence):
    data = [sentence, '0']
    dataset_another = [data]
    logits = 0
    another_test = TrainDataset(dataset_another)
    test_dataloader = torch.utils.data.DataLoader(another_test)

    for input_ids_batch, attention_masks_batch, y_batch in test_dataloader:
        y_batch = y_batch.long().to(device)
        out = model(input_ids_batch.to(device), attention_mask=attention_masks_batch.to(device))[0]
        out = out[:, -1, :]

        for i in out:
            logits = i
            logits = logits.detach().cpu().numpy()
            logits = np.argmax(logits)
    return logits
#########################################################################################################


# User_Based_Collaborative_Filtering
def get_feature_dataframe(user):
    # MySQL 연결
    conn = pymysql.connect(
        host=os.environ.get("host"),
        port=int(os.environ.get("port")),  # mysql 포트
        user=os.environ.get("user"),  # 접속 계정
        password=os.environ.get("password"),  # 루트계정 본인 비번
        db=os.environ.get("db"),  # 접속하려는 데이터베이스 명
        charset='utf8'
    )

    cursor = conn.cursor()

    feature_table = []
    col_name = ["user_id", "user_gender", "user_age", "user_life_cycle", "user_is_multicultural", "user_is_one_parent", "user_income", "user_is_disabled"]

    cursor.execute('SELECT * FROM user WHERE user_id = "%s"' % user)

    user_feature = cursor.fetchone()[0:8]
    feature_table.append(list(user_feature))

    cursor.execute('SELECT * FROM user WHERE user_id != "%s"' % user)

    another_user_feature = cursor.fetchall()

    for i in another_user_feature:
        feature_table.append(list(i[0:8]))
    similarity_dataframe = pd.DataFrame(feature_table, columns=col_name)
    similarity_dataframe.set_index('user_id', inplace=True)

    conn.close()

    return similarity_dataframe


def get_collaborative_filtering_welfare(user, size):
    # MySQL 연결
    conn = pymysql.connect(
        host=os.environ.get("host"),
        port=int(os.environ.get("port")),  # mysql 포트
        user=os.environ.get("user"),  # 접속 계정
        password=os.environ.get("password"),  # 루트계정 본인 비번
        db=os.environ.get("db"),  # 접속하려는 데이터베이스 명
        charset='utf8'
    )

    cursor = conn.cursor()

    df = ubcf.get_cosine_similarity_user(get_feature_dataframe(user))
    col = df.columns.tolist()[1:]
    li = df.values.tolist()[0][1:]
    dic = {name: value for name, value in zip(col, li)}
    dic = sorted(dic.items(), reverse=True, key=lambda x: x[1])  # 유사한 사용자 순으로 정렬
    welfare_list = []

    # 사용자와 유사도가 높은 다른 사용자들의 관심 복지 정보를 가져온다.
    for i in range(len(dic)):
        another_user = dic[i][0]
        cursor.execute('SELECT * FROM user_like WHERE user_id = "%s"' % another_user)
        welfare = cursor.fetchmany(size=size)
        for j in welfare:
            welfare_list.append(j[2])
            if len(welfare_list) == size:
                break
        if len(welfare_list) == size:
            break

    conn.close()

    return welfare_list
#######################################################################################


# 관심 카테고리 복지 중 좋아요 개수 상위
def get_interest_most_like_welfare(user, size):

    # MySQL 연결
    conn = pymysql.connect(
        host=os.environ.get("host"),
        port=int(os.environ.get("port")),  # mysql 포트
        user=os.environ.get("user"),  # 접속 계정
        password=os.environ.get("password"),  # 루트계정 본인 비번
        db=os.environ.get("db"),  # 접속하려는 데이터베이스 명
        charset='utf8'
    )

    cursor = conn.cursor()

    welfare_list = []

    cursor.execute('SELECT b.welfare_id FROM user_interest a '
                   'INNER JOIN welfare_category b ON a.category_id = b.category_id '
                   'INNER JOIN welfare c ON b.welfare_id = c.welfare_id '
                   'WHERE a.user_id = "%s"ORDER BY c.like_count DESC' % user)
    welfare = cursor.fetchmany(size=size)

    for i in welfare:
        welfare_list.append(i[0])

    conn.close()

    return welfare_list


# 전체 복지 중 좋아요 개수 상위
def get_most_like_welfare(size):

    # MySQL 연결
    conn = pymysql.connect(
        host=os.environ.get("host"),
        port=int(os.environ.get("port")),  # mysql 포트
        user=os.environ.get("user"),  # 접속 계정
        password=os.environ.get("password"),  # 루트계정 본인 비번
        db=os.environ.get("db"),  # 접속하려는 데이터베이스 명
        charset='utf8'
    )

    cursor = conn.cursor()

    welfare_list = []

    cursor.execute('SELECT * FROM welfare ORDER BY like_count DESC')
    welfare = cursor.fetchmany(size)

    for i in welfare:
        welfare_list.append(i[0])

    conn.close()

    return welfare_list


# 메인 화면 추천 6개
def main_recommend(user):
    welfare1 = get_collaborative_filtering_welfare(user, 2)
    welfare2 = get_interest_most_like_welfare(user, 2)
    welfare3 = get_most_like_welfare(2)

    welfare_list = welfare1 + welfare2 + welfare3

    return welfare_list


# 개인 복지 추천 3개
def personal_recommend(user):
    welfare1 = get_collaborative_filtering_welfare(user, 2)
    welfare2 = get_interest_most_like_welfare(user, 1)

    welfare_list = welfare1 + welfare2

    return welfare_list


# CPU 사용
device = torch.device('cpu')

# LOAD CHATBOT MODEL
model = load_model()

# Initialize
app = Flask(__name__)


"""
통신 url : /chatbot
Node Server의 Request 
    {
        "message" : "사용자의 메시지"
        "id" : "사용자 id"
    }

Flask Server의 Response
    {
        "type" : "welfare or message",
        "message" : "label number",
        "welfare" : ["welfareid1","welfareid2","welfareid3"]    
    }
"""


# Define a route for url
@app.route('/chatbot', methods=['GET', 'POST'])
def action():
    global model

    reqJson = request.get_json()
    user_message = reqJson["message"]
    user_id = reqJson["id"]

    model = model.to(device)

    model.eval()

    chatbot_label = predict(user_message)

    message = chatbot_label


    resJson = {"type": "",
        "message": "",
        "welfare": ""
    }

    resJson['message'] = str(message)

    if chatbot_label == 359:
        resJson['type'] = "welfare"
        welfare = personal_recommend(user_id)
        resJson['welfare'] = welfare
    else:
        resJson['type'] = "message"

    return jsonify(resJson)


"""
통신 url : /main
Node Server의 Request 
    {
        "user_id" : "사용자 id"
    }

Flask Server의 Response
    {
        "welfare" : ["welfareid1","welfareid2","welfareid3","welfareid4","welfareid5","welfareid6"]    
    }
"""


# 홈 화면 추천 - 6개 (2개 사용자 유사도 기반=> API / db접속 pymysql로, 2개 좋아요 상위(node server에서 하는게 굿))
@app.route('/main', methods=['GET', 'POST'])
def action2():
    reqJson = request.get_json()
    user_id = reqJson["id"]
    welfare = main_recommend(user_id)

    resJson = {
        "welfare": ""
    }

    resJson['welfare'] = welfare
    return jsonify(resJson)


# Run the app
if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True, port=8000)
