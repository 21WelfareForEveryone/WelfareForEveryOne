import re

## summary, who, what, criteria 등 모두 해당하기
def preprocess1(text):
    text=re.sub(r'\t', '', text)
    text=re.sub(r'^(\n)+|(\n)+$','', text)
    text=re.sub(r'<BR />', '\n', text)
    return text


## call 과 site의 경우
def preprocess2(text):
    text=re.sub('\t', '', text)
    text=re.sub(r'^(\n)+|(\n)+$','', text)
    text=re.sub(r'\n', ' ', text)
    text=re.sub(r'☎', '', text)
    return text