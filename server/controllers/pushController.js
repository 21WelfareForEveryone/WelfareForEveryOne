const schedule = require('node-schedule');

// Importing Models
const Welfare = require('../models/welfare'); 
const PushAlarm = require('../models/push_alarm');
const User = require('../models/user');  

// Import sequelize query operator 
const { Op } = require("sequelize");

// Import Modules
let jwt = require("jsonwebtoken"); 
let secretObj = process.env.JWT_SECRET

// Import FCM Info
const admin = require('firebase-admin');

// 복지정보를 토글했을 경우의 API
exports.pushToggle = (req, res, next) => {
    // 토큰 복호화 
    const command = req.body.toggle_command;
    const user_info = jwt.verify(req.body.token, secretObj);

    if (command === 'On') {
        // 데이터 생성    
        PushAlarm.create({
            user_id:user_info.user_id
        })
        .then(result => {
            // 성공시 성공 json 보내기
            res.send(JSON.stringify({
                "success": true,
                "statusCode" : 200,
                "token" : req.body.token
            }));
        }).catch(err => { 
            console.log(err);
            // 실패 json 보내기
            res.send(JSON.stringify({
                "success": false,
                "statusCode" : 400,
                "token" : req.body.token
            }));
        })        
    } 
    else {
        PushAlarm.destroy({where: { user_id : user_info.user_id }})
        .then(result => {
            res.send(JSON.stringify({
                "success": true,
                "statusCode" : 200,
                "token" : req.body.token
            }));
        })
        .catch(err => { 
            console.log(err);
            // 실패 json 보내기
            res.send(JSON.stringify({
                "success": false,
                "statusCode" : 400,
                "token" : req.body.token
            }));
        })  
    }   
    

}

// 복지정보를 토글했을 경우의 API
exports.pushToggle = (req, res, next) => {
    // 토큰 복호화 
    const command = req.body.toggle_command;
    const user_info = jwt.verify(req.body.token, secretObj);
    const id2toggle = req.body.welfare_id

    Welfare.findByPk(id2toggle).then(welfare => {
        if (command === 'On') {
            // 데이터 생성    
            PushAlarm.create({
                user_id:user_info.user_id
            })
            .then(result => {
                // 성공시 성공 json 보내기
                res.send(JSON.stringify({
                    "success": true,
                    "statusCode" : 200,
                    "token" : req.body.token
                }));
            }).catch(err => { console.log(err);})        
        } else {
            PushAlarm.destroy({where: { welfare_id: id2toggle }})
            .then(result => {
                res.send(JSON.stringify({
                    "success": true,
                    "statusCode" : 200,
                    "token" : req.body.token
                }));
            })
            .catch(err=>{console.log(err);})
        }   
    })

}

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

    Welfare.findAll({where: {d_day : { [Op.lte] : kr_curr}}, raw: true})

    // FCM 메시지 Format 
    let message = 
    {
        notification : {
            title: '모두를 위한 복지 알림입니다.',
            body: welfareinfo.title,
        },
        token : userinfo.user_mToken
    }

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

const j = schedule.scheduleJob('42 * * * *', function(){
    console.log('The answer to life, the universe, and everything!');
  });