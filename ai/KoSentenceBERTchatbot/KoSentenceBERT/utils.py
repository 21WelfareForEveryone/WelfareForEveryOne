import numpy as np
import pandas as pd
from numpy import dot
from numpy.linalg import norm
import json

def cos_sim(A, B):
      return dot(A, B)/(norm(A)*norm(B)) ## A, B는 array

def extract_top(em_se_embedding, em_query, top_k): 
    cos_sim_query = pd.DataFrame()
    for i in range(em_se_embedding.size):
      cos_sim_query.loc[i, 'cos_sim'] = cos_sim(np.array(json.loads(em_se_embedding[i])), em_query)
    cos_sim_query = cos_sim_query.sort_values("cos_sim", ascending=False)
    top_sm = cos_sim_query.index[0:top_k].tolist()
    return top_sm

def extract_welfare_id(corpus_welfare_id, top_results): 
    top_welfare_id = corpus_welfare_id[top_results].tolist()
    return top_welfare_id