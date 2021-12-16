package com.example.tave0915;

public class User {
    private String id, pwd, username, address, mToken;
    private int lifeCycle, income, interest;
    private int  gender, isMultiCultural, familyState, isDisabled;
    private String pushToken;

    public User(String username, String id, String pwd, String address, String mToken, int lifeCycle, int income, int interest,
                int gender, int isMultiCultural, int familyState, int isDisabled, String pushToken){
        this.username = username;
        this.id = id;
        this.pwd = pwd;
        this.address = address;
        this.mToken = mToken;
        this.lifeCycle = lifeCycle;
        this.income = income;
        this.gender = gender; // int 0,1
        this.isMultiCultural = isMultiCultural;
        this.isDisabled = isDisabled;
        this.familyState = familyState;
        this.pushToken = pushToken;
        this.interest = interest;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd){
        this.pwd = pwd;
    }

    public int  getGender(){return gender;}
    public void setGender(int gender){this.gender = gender;}
    public String getmToken(){return mToken;}
    public void setmToken(String mToken){this.mToken = mToken;}
    public int getLifeCycle(){return lifeCycle;}
    public void setLifeCycle(int lifeCycle){this.lifeCycle = lifeCycle;}
    public String getAddress(){return address;}
    public void setAddress(String address){this.address = address;}
    public int getIncome(){return income;}
    public void setIncome(int income){this.income = income;}

    public int  getIsMultiCultural(){return isMultiCultural;}
    public void setIsMultiCultural(int isMultiCultural){this.isMultiCultural = isMultiCultural;}
    public int getIsDisabled(){return isDisabled;}
    public void  setIsDisabled(int isDisabled){this.isDisabled = isDisabled;}

    public int getFamilyState(){return familyState;}
    public void setFamilyState(int familyState){this.familyState = familyState;}

    public String getPushToken(){return pushToken;}
    public void setPushToken(String pushToken){this.pushToken = pushToken;}

    public int getInterest(){return interest;}
    public void setInterest(int interest){this.interest = interest;}

}
