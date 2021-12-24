// Importing Models
const Welfare = require('../models/welfare'); 

// Import sequelize query operator 
const { Op } = require("sequelize");

// Import Modules
let jwt = require("jsonwebtoken"); 
let secretObj = process.env.JWT_SECRET

// Import FCM Info
const admin = require('firebase-admin');
const User = require('../models/user'); 

// Import modules
let jwt = require("jsonwebtoken"); 
let secretObj = require("../config/jwt"); 

exports.getPushAlarm = (req, res, next) => {
    /*
        구현할 로직 : 연관 카테고리에서 찜 갯수 가장 많은것 10개에서 하나 랜덤으로 뽑아서 FCM 푸시알림으로 보내주기
        0. Request로 온 User Token을 복호화해서 유저 정보를 받는다. 
        1. user_interest table에서 해당 유저의 관심 카테고리를 받는다.
        2. 해당 관심 카테고리를 가지는 복지정보를 welfare table에서 검색한다.
        3. 검색된 복지정보를 like 갯수로 정렬해서 상위 10개를 추출한다.
        4. 상위 10개에서 랜덤으로 하나를 정한다.
        5. 정해진 복지정보를 FCM message format에 넣어서 보낸다.
        6. 성공 혹은 실패 Resposne Message를 Client에게 보낸다.  
    */

    // 토큰 복호화 
    const user_info = jwt.verify(req.body.token, secretObj.secret);

    // FCM 메시지 Format 
    let message = 
    {
        notification : {
            title: '모두를 위한 복지 알림입니다.',
            body: welfareinfo.title,
        },
        token : userinfo.user_mToken
    }

    // FCM으로 메시지 보내는 과정 
    admin
    .messaging()
    .send(message)
    .then(function(response){
        console.log('Successfully sent message:', response)
        return res.status(200).json({success: true})
    })
    .catch(function(err) {
        console.log('Error Sending message!!! : ', err)
        return res.status(400).json({success: false})
    });

}
