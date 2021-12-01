package com.example.tave0915;

public class URLs {

    /* http 통신간 사용할 각 기능별 url
    * root_url : 실제 사용할 http server url
    * url_register : 회원가입(RegisterActivity)
    * url_login : 로그인(LoginActivity)
    * url_update : user 정보 업데이트(ProfileActivity)
    * url_delete : user 정보 삭제(...)
    * url_read : user 정보 읽기(MyProfileActivity, 적용 X)
    * url_chatbot : ChatBot getResponse(ChatActivity)
    * url_my_welfare: user의 관심 복지 리스트 호출(MyProfileActivity)
    * url_welfare_search : user의 카테고리별 복지 리스트 호출
    * */

    private static final String root_url = "http://34.64.177.178";
    public static final String url_register = root_url + "/rest/user/register";
    public static final String url_login = root_url + "/rest/user/login";
    public static final String url_update = root_url + "/rest/user/update";
    public static final String url_delete = root_url + "/rest/user/delete";
    public static final String url_read = root_url + "/rest/user/read" ;
    public static final String url_chatbot = root_url + "/chatbot/getresponse";
    public static final String url_my_welfare = root_url + "/rest/dibs/read";
    public static final String url_welfare_search = root_url + "/rest/welfare/search";
}
