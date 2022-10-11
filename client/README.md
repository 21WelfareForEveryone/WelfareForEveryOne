# 모두의 복지
개인 맞춤형 복지 추천 앱 모두의 복지 프론트엔드 개발 소개 페이지입니다.   <br/>
- Developer email address :
  - App Developer(김진수) : wlstn5376@gmail.com<br/>
  - App Developer(박해미) : 
- Demo Video : https://www.youtube.com/watch?v=YdwjrgnP7SM <br />
- 기술 스택 정리(notion) : https://welfareforeveryone.notion.site/Front-end-Framework-and-Structure-2e8b6b629ea844fd87a32a198ee49fff <br/>

## Introduction

모두의 복지는 복지 사각지대에 계신 분들에게 사용자 기반 추천 알고리즘을 적용한 최적화된 복지 혜택을 실시간 제공해주는 어플리케이션입니다. <br/>
기본적으로 복지 정보 열람 서비스를 제공하고 있으며 (1) 카테고리별 복지 정보 (2) 키워드 기반 추천 복지 정보 (3) 사용자 관심 복지 정보 (4) 대화형 UI 챗봇을 통한 추천 복지의 형태로 실시간 복지 정보를 제공합니다. <br/>
복지 정보 외에도 복지 시설에 대한 위치 제공 서비스와, 챗봇 UI를 활용한 대화 서비스 또한 제공하며, 더 나아가 푸시 알람 기능을 활용해 사용자가 관심을 가질만한 복지 정보에 대한 마감일 알림 서비스 또한 제공하고 있습니다. 

## UI Implementation
현재까지 프론트엔드에서 구현된 기능은 다음과 같습니다.

1. 리스트 뷰 형태로 최적의 맞춤형 복지 정보 제공
2. 챗봇을 통한 실시간 추천 복지 정보 제공 
3. 챗봇을 통한 실시간 대화 서비스 기능 
4. 사용자 위치에 따른 복지시설 안내(병원, 공공기관, 노인 복지 등)
4. 복지 정보에 대한 푸시 알림
5. 관심 복지 정보에 대한 찜 기능

## Development Environment
- Tool : Android Studio(ver 2020.03.01 patch2)
- Language : Java
- Library to use
  - http networking : Volley
  - Push Notification : Firebase
  - Map UI & Place Searching : Google Map API, Google Place API

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

## Download Application
- 구글 플레이스토어 링크 : https://play.google.com/store/apps/details?id=com.product.welfareapp 

* 본 프로젝트는 Google의 GCP Credit 지원을 받고 있습니다. (Google supported this work by providing Google Cloud credit)