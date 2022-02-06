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
