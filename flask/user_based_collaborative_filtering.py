import numpy as np
import pandas as pd
from pandas import DataFrame
from itertools import product


def get_cosine_similarity(u, v):
    uvdot = (u * v).sum()

    norm1 = (u ** 2).sum()
    norm2 = (v ** 2).sum()

    score = uvdot / np.sqrt(norm1 * norm2)

    return score


def get_cosine_similarity_user(data):
    index_combinations = list(product(data.index, repeat=2))
    similarity_list = []

    for uname, vname in index_combinations:
        u, v = data.loc[uname], data.loc[vname]
        score = get_cosine_similarity(u, v)

        similarity = {
            'u': uname,
            'v': vname,
            'score': score
        }

        similarity_list.append(similarity)
    similarity_list = DataFrame(similarity_list)
    similarity_table = pd.pivot_table(similarity_list, index='u', columns='v', values='score')

    return similarity_table
