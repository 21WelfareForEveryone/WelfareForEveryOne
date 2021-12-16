# model library import
from sentence_transformers import SentenceTransformer, util
import numpy as np
import pandas as pd

from numpy import dot
from numpy.linalg import norm

import time
import json


from flask import Flask, request 

# cosine similarity function
def cos_sim(A, B):
       return dot(A, B)/(norm(A)*norm(B)) ## A, B는 array

# extracting top results function
# em_se_embedding : series, em_query : numpy
def extract_top(em_se_embedding, em_query, top_k): 
    cos_sim_query = pd.DataFrame()
    for i in range(em_se_embedding.size):
      cos_sim_query.loc[i, 'cos_sim'] = cos_sim(np.array(json.loads(em_se_embedding[i])), em_query)
    cos_sim_query = cos_sim_query.sort_values("cos_sim", ascending=False)
    top_sm = cos_sim_query.index[0:top_k].tolist()
    return top_sm

# 서버 연결
app = Flask(__name__) 

@app.route('/') 
def hello(): 
    return 'access successful!' 

@app.route('/sebert', methods=['POST']) 
def method(): 
    if request.method == 'POST':         
        params = json.loads(request.get_data(), encoding='utf-8')

        # 데이터가 request에 없을 경우
        if len(params) == 0:
            return 'No parameter'

        # 쿼리문 embedding
        query = params["query"]
        query_embedding = embedder.encode(query) 
        # gpu 돌릴 때
        # query_embedding = embedder.encode(query, convert_to_tensor=True)
        # query_embedding = query_embedding.cpu()
        # query_embedding = query_embedding.numpy() 
        query_embedding = query_embedding.astype(float)
        
        # title또는 full(title+summary)인지 확인
        standard = params['standard']
        em_se_embedding = corpus_embedding['embedding_'+standard]

        #sort the top_k results
        top_k=3
        top_results = extract_top(em_se_embedding, query_embedding, top_k)

        # response_top_results = []
        # for idx in top_results[0:top_k]:
        #     result = {}
        #     detail = {}
        #     policy = corpus_embedding.loc[idx,:]
        #     result['welfare_id'] = idx
        #     result['title'] = policy['title']
        #     result['summary'] = policy['summary']
        #     detail['who'] = policy['who']
        #     detail['criteria'] = policy['criteria']
        #     detail['what']=policy['what']
        #     detail['how']=policy['how']
        #     detail['calls']=policy['calls']
        #     detail['sites']=policy['sites']
        #     detail['url']=policy['url']
        #     result['detail'] = detail
        #     response_top_results.append(result)
        
        return json.dumps({"recommend":top_results},ensure_ascii=False)


if __name__ == '__main__': 
    # 모델 불러오기
    model_path = './output/training_sts'
    embedder = SentenceTransformer(model_path, device='cpu')
    # gpu 사용할 때 
    # embedder = SentenceTransformer(model_path)

    # 임베딩된 복지 정책 가져오기
    corpus_embedding = pd.read_csv("corpus_embedding.csv", encoding = "utf-8-sig")
    print("connection success")
    app.run(port=8080, host="0.0.0.0")


