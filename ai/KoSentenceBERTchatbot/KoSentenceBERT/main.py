from sentence_transformers import SentenceTransformer
import pandas as pd
import json

from utils import extract_top

from flask import Flask, request 


# 서버 연결
app = Flask(__name__) 
model_path = './output/training_sts'
embedder = SentenceTransformer(model_path, device='cpu')
# gpu 사용할 때 
# embedder = SentenceTransformer(model_path)
corpus_embedding = pd.read_csv("corpus_embedding.csv", encoding = "utf-8-sig")

@app.route('/sebert', methods=['POST']) 
def method(): 
    if request.method == 'POST':         
        params = json.loads(request.get_data(), encoding='utf-8')

        if len(params) == 0:
            return 'No parameter'

        query = params["query"]
        query_embedding = embedder.encode(query) 
        # gpu 돌릴 때
        # query_embedding = embedder.encode(query, convert_to_tensor=True)
        # query_embedding = query_embedding.cpu()
        # query_embedding = query_embedding.numpy() 
        query_embedding = query_embedding.astype(float)
        
        standard = params['standard']
        em_se_embedding = corpus_embedding['embedding_'+standard]

        top_k=3
        top_results = extract_top(em_se_embedding, query_embedding, top_k)
        
        return json.dumps({"recommend":top_results},ensure_ascii=False)

