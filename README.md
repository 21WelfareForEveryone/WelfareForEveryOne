# 모두의 복지 앱
![](https://i.imgur.com/LkDevQK.png)  
복지 사각지대에 계신 분들이 앱 하나로 자신에게 **최적화된 복지 혜택**을 받아 볼 수 있습니다.  
**최적의 맞춤형 정보제공 챗봇과 실시간 푸시 알림, 주변 복지시설 안내, 관심복지 저장**의 기능을 통해 **복지 사각지대**의 사람들에게 편의를 제공합니다.  
([2021 프로보노 공모전](https://www.hanium.or.kr/portal/subscription/info.do?trackSeq=2) 대상 과학기술정보통신부장관상 수상작입니다.)

## 기술 스택

| Frontend | Backend | DB | DATA |
| :--------: | :--------: | :--------: | :--------: |
| <img src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat-square&logo=Android Studio&logoColor=white"/> <img src="https://img.shields.io/badge/Java-007396?style=flat-square&logo=Java&logoColor=white"/> <img src="https://img.shields.io/badge/Volley-3DDC84?style=flat-square&logoColor=white"/> <img src="https://img.shields.io/badge/Google Maps-4285F4?style=flat-square&logo=Google Maps&logoColor=white"/> <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=white"/>| <img src="https://img.shields.io/badge/NodeJs-339933?style=flat-square&logo=Node.js&logoColor=white"/> <img src="https://img.shields.io/badge/Express-000000?style=flat-square&logo=Express&logoColor=white"/> <img src="https://img.shields.io/badge/PM2-2B037A?style=flat-square&logo=PM2&logoColor=white"/> <img src="https://img.shields.io/badge/Flask-000000?style=flat-square&logo=Flask&logoColor=white"/> <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white"/> <img src="https://img.shields.io/badge/GCP-4285F4?style=flat-square&logo=Google Cloud&logoColor=white"/> <img src="https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=Amazon AWS&logoColor=white"/> <img src="https://img.shields.io/badge/Sequelize-52B0E7?style=flat-square&logo=Sequelize&logoColor=white"/> <img src="https://img.shields.io/badge/JWT-000000?style=flat-square&logo=Json Web Tokens&logoColor=white"/> <img src="https://img.shields.io/badge/Postman-FF6C37?style=flat-square&logo=postman&logoColor=white"/> |<img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white"/>| <img src="https://img.shields.io/badge/Python-3776AB?style=flat-square&logo=python&logoColor=white"/> <img src="https://img.shields.io/badge/PyTorch-EE4C2C?style=flat-square&logo=pytorch&logoColor=white"/> |
- Front-end Framework and Structure : [Frontend](https://welfareforeveryone.notion.site/Front-end-Framework-and-Structure-2e8b6b629ea844fd87a32a198ee49fff)
- Back-end Framework and Structure : [Backend](https://welfareforeveryone.notion.site/Back-end-Framework-and-Structure-7ec49ee09c3a4c9b9a1f500b15aead1c) 
- 서버 구축까지의 시행착오 : [Server](https://welfareforeveryone.notion.site/Performance-Engineering-1a92fce2b23b4175bc4ce7cba45e141a)
- KcELECTRA 동작 구조가 궁금하다면? : [KcELECTRA](https://velog.io/@kmg2933/KcELECTRA-%EC%B1%97%EB%B4%87-%EC%84%A4%EA%B3%84%EB%8F%84)
- KoSBERT에 대해 알고 싶다면? : [KoSBERT](https://www.notion.so/KoSBERT-f17c66df70b8455fb9ecc3441c887857)

## 실행 스크립트

### 앱 실행 스크립트
```java
Execute Android Eumulator
Build Project
Run
```
### 서버 실행 스크립트

server/.env를 참고해서 환경변수 파일을 만들어주세요.
server/config/ 폴더에 chatbot.json파일을 [여기서](https://www.notion.so/welfareforeveryone/391ccf431eaa449db00c9a36658ee6e8) 다운받아서 넣어주세요.
server/config/ 폴더에 firebase 인증 키 정보를 넣어주고 추가해주세요. 

```NodeJs
cd (backend folder)
npm install
npm run prod (production version)
npm start (development version)
```
### AI 챗봇 실행 스크립트
docker image 다운로드: kmg2933/welfare-for-everyone:0916
- 파일

model.pt 파일은 해당 [링크](https://welfareforeveryone.notion.site/391ccf431eaa449db00c9a36658ee6e8)에서 받아가세요.
```python
config = {
    'port':'number'
}
```

- 실행 스크립트
```script
sudo cp model.pt WelfareForEveryOne/ai/KcELECTRAchatbot/flask/model.pt
sudo cp config.py WelfareForEveryOne/ai/KcELECTRAchatbot/flask/config.py

cd WelfareForEveryOne/ai/KcELECTRAchatbot/flask

// image build
docker build --no-cache -t kmg2933/welfare-for-everyone:prod .

// cpu
docker run --rm -it -p [host port]:[container port] kmg2933/welfare-for-everyone:prod

```
### KoSBERT 실행 스크립트
docker image 다운로드:bookbug/kosbert_image:latest  
(참고 : CPU로 실행되도록 설정되어 있습니다. GPU로 실행하고 싶으시다면 [GPU 실행](https://github.com/BM-K/KoSentenceBERT-SKT/issues/8)을 참고해주세요.)
- 파일
result.pt 파일은 해당 [링크](https://drive.google.com/drive/folders/1fLYRi7W6J3rxt-KdGALBXMUS2W4Re7II)의 sts/result.pt 파일을output/training_sts/0_Transformer에 넣어주세요.
corpus_embedding.csv는 해당 [링크](https://www.notion.so/welfareforeveryone/391ccf431eaa449db00c9a36658ee6e8)에서 가져가시고 [출처](https://www.data.go.kr/data/15090532/openapi.do)는 공공데이터포털입니다.
```python
config = {
    'port':'number'
}
```
- 실행 스크립트
```script
// file copy
sudo cp result.pt WelfareForEveryOne/ai/KoSentenceBERTchatbot/KoSentenceBERT/output/training_sts/0_Transformer/result.pt
sudo cp corpus_embedding.csv WelfareForEveryOne/ai/KoSentenceBERTchatbot/KoSentenceBERT/corpus_embedding.csv
sudo cp config.py WelfareForEveryOne/ai/KoSentenceBERTchatbot/KoSentenceBERT/config.py

cd WelfareForEveryOne/ai/KoSentenceBERTchatbot

// image build
docker build --no-cache -t kosbert_image:prod .

// cpu
docker run --rm -it -p [host port]:[container port] kosbert_image:prod

// gpu
docker run --gpus all --rm -it -p [host port]:[container port] kosbert_image:prod

```

## 어플리케이션 시연

전체 시연 영상은 다음 [데모 영상](https://www.youtube.com/watch?v=YdwjrgnP7SM)을 참고해 주세요. 

|추천 복지 열람|챗봇|복지시설 |푸시알림|관심복지 추가|
|:---:|:---:|:---:|:---:|:---:|
|<img src = "https://github.com/ZINZINBIN/WelfareForEveryoneGIF/blob/main/detail_info.gif?raw=true" width = 120vw height = 250vh>|<img src = "https://github.com/ZINZINBIN/WelfareForEveryoneGIF/blob/main/chatbot.gif?raw=true" width = 120vw height = 250vh>|<img src = "https://github.com/ZINZINBIN/WelfareForEveryoneGIF/blob/main/map.gif?raw=true" width = 120vw height = 250vh>|<img src = "https://github.com/ZINZINBIN/WelfareForEveryoneGIF/blob/main/push_notification.gif?raw=true" width = 120vw height = 250vh>|<img src = "https://github.com/ZINZINBIN/WelfareForEveryoneGIF/blob/main/recommend_info.gif?raw=true" width = 120vw height = 250vh>|

## 멤버 소개

| <img src = "https://avatars.githubusercontent.com/u/46372624?v=4" width = "100px">| <img src="https://avatars.githubusercontent.com/u/61974170?v=4" width="100px"> | <img src="https://user-images.githubusercontent.com/33998183/147872777-84707c11-f5f2-49f5-8bb1-5d9d1c837491.png" width="100px"> | <img src="https://avatars.githubusercontent.com/u/68273065?v=4" width="100px"> | <img src="https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F0b7ddd17-0bca-4c31-811c-97951980da37%2FKakaoTalk_20210205_212818264.jpg?table=block&id=3b214631-fe5a-4d79-8a85-9c0d695ed5ba&spaceId=6f0402d5-807a-4a27-a6e0-ca357f56d338&width=250&userId=f7b06525-8aba-4d21-b98f-69034e959047&cache=v2" width="100px"> |
| :-----------------------------------------------------------------------: | :-----------------------------------------------------------------------: | :-----------------------------------------------------------------------: | :-----------------------------------------------------------------------: | :-----------------------------------------------------------------------: |
|[김진수](https://github.com/zinzinbin) | [박해미](https://github.com/parkhaemi) | [권태훈](https://github.com/Oxymoron957) | [김민규](https://github.com/MingyuKim-2933) |[문혜현](https://github.com/hyehyeonmoon) |
| App Frontend | App Frontend | App Backend | Chatbot AI |Chatbot AI |


## Reference
- [KcELECTRA by Beomi](https://https://github.com/Beomi/KcELECTRA)
- [Pytorch Docker Image](https://hub.docker.com/r/pytorch/pytorch)
- [Ko-SentenceBERT-SKTBERT](https://github.com/BM-K/KoSentenceBERT_SKT#sentence-transformers-multilingual-sentence-embeddings-using-bert--roberta--xlm-roberta--co-with-pytorch)
- [KorNLU Datasets](https://github.com/kakaobrain/KorNLUDatasets)
- [Sentence Transformers: Multilingual Sentence Embeddings using BERT / RoBERTa / XLM-RoBERTa & Co. with PyTorch](https://github.com/UKPLab/sentence-transformers)

#### * 본 프로젝트는 Google의 GCP Credit 지원을 받고 있습니다. (Google supported this work by providing Google Cloud credit)
