# 모두의 복지
프로보노 공모전 프론트엔드 개발 프로젝트 by TAVE<br/>
- Email address :
  - App Developer(김진수) : wlstn5376@gmail.com<br/>
  - App Developer(박해미) :
- Demo Video : https://www.youtube.com/watch?v=YdwjrgnP7SM <br />
- 기술 스택 정리 : https://welfareforeveryone.notion.site/Front-end-Framework-and-Structure-2e8b6b629ea844fd87a32a198ee49fff <br/>

## Introduction

모두의 복지는 복지 사각지대에 계신 분들에게 사용자 기반 추천 알고리즘을 적용한 최적화된 복지 혜택을 실시간 제공해주는 어플리케이션입니다.

모두의 복지가 갖는 기능은 다음과 같습니다.

1. 리스트 뷰 형태로 최적의 맞춤형 복지 정보 제공
2. 챗봇을 통한 실시간 복지 추천 정보 제공
3. 사용자 위치에 따른 복지시설 안내(병원, 공공기관, 노인 복지 등)
4. 복지 정보에 대한 푸시 알림
5. 관심 복지 정보에 대한 찜 기능

## Development Environment
- Tool : Android Studio(ver 2020.03.01 patch2)
- Language : Java
- Library to use
  - http networking : volley
  - Push Notification : Firebase

## APIs
- Google Map API : MapActivity google map 시각화
- Google Place API : MapActivity 복지시설 키워드 검색

## Application Version
- minSdkVersion : 24
- targetSdkVersion : 30

## How to execute
```
sync project with gradle files
build project
run app
```

* 본 프로젝트는 Google의 GCP Credit 지원을 받고 있습니다. (Google supported this work by providing Google Cloud credit)