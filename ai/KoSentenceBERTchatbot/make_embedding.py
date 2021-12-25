from sentence_transformers import SentenceTransformer, util
import numpy as np
import pandas as pd
import time
import json
import copy

model_path = './output/training_sts'

def target_embedding(device, embedder, em_corpus, target, col_name):
    corpus = em_corpus[target].tolist()
    
    corpus_embeddings = embedder.encode(corpus, convert_to_tensor=True)
    
    if device!='cpu':
        corpus_embeddings = corpus_embeddings.cpu()
    em_se_numpy = corpus_embeddings.numpy()
    for i in range(em_se_numpy.shape[0]):
        em_corpus.loc[i,col_name] = json.dumps(em_se_numpy[i,:].tolist())
    return em_corpus


def make_embedding(device = 'cpu'):

    if device!='cpu':
        embedder = SentenceTransformer(model_path)
    else:
        embedder = SentenceTransformer(model_path, device='cpu')
    em_corpus = pd.read_csv("welfare_for_embedding.csv")
    
    # title+summary embedding
    em_corpus = target_embedding(device, embedder, em_corpus, 'full', 'embedding_full')
    #title embedding
    em_corpus = target_embedding(device, embedder, em_corpus, 'title', 'embedding_title')
    
    em_corpus.to_csv("corpus_embedding.csv",encoding = 'utf-8-sig', index=False)

make_embedding()
