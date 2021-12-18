// Importing Models
const Welfare = require('../models/welfare'); 
const PushAlarm = require('../models/push_alarm');

// Import sequelize query operator 
const { Op } = require("sequelize");

// Import Modules
let jwt = require("jsonwebtoken"); 
let secretObj = process.env.JWT_SECRET

// Import FCM Info
const admin = require('firebase-admin');
const User = require('../models/user'); 

// In-App 푸시알림 창에 표시할 정보 전송 API 
exports.getInfo = (req, res, next) => {
    // 토큰 복호화 
    const user_info = jwt.verify(req.body.token, secretObj.secret);

    // 현재 날짜
    const curDate = new Date();

    // 현재 사용자가 푸시알림을 킨 복지정보
    let welfare_list_on = []
    // 현재 사용자에게 추천할 복지정보
    let welfare_list = [];

    // welfare_id 배열에 들어있는 복지 정보를 Response로 반환
    PushAlarm.findAll({where: {user_id:user_info.user_id}, raw: true})
    .then(results => {
        results.forEach(element => {
            Welfare.findByPk(element.welfare_id)
            .then(welfareInfo=>{
                welfareInfo.d_day = Math.floor((welfareInfo.start_date.getTime()- curDate.getTime())/(1000*3600*24))
                welfare_list_on.push(welfareInfo);
            })
        });
    })
    .then(()=>{
        Welfare.findAll({where: {start_date : { [Op.gte] : curDate}}, order: [['like_count', 'DESC']], raw: true})
	.then(result=>{
            let count = 5-welfare_list_on.length;
            result.forEach(element => {
                let flag = false
                if(count > 0){
                    if(welfare_list_on != undefined)
                        welfare_list_on.forEach(element2 => {
                            if (element.welfare_id == element2.welfare_id) {
                                flag = true
                            }
                        });
                    if(!flag)
                        element.d_day = Math.floor((element.start_date.getTime()- curDate.getTime())/(1000*3600*24))
                        welfare_list.push(element);                
                        count -= 1;
                }
            });
            // 성공시 성공 json 보내기
            res.send(JSON.stringify({
                "success": true,
                "statusCode" : 201,
                "welfare_list": welfare_list,
                "welfare_list_on":welfare_list_on,
                "token" : req.body.token
            }));
        })
    })

}

// 복지정보를 토글했을 경우의 API
exports.pushToggle = (req, res, next) => {
    // 토큰 복호화 
    const command = req.body.toggle_command;
    const user_info = jwt.verify(req.body.token, secretObj.secret);
    const id2toggle = req.body.welfare_id

    Welfare.findByPk(id2toggle).then(welfare => {
        if (command === 'On') {
            // 데이터 생성    
            PushAlarm.create({
                user_id:user_info.user_id,
                welfare_id:id2toggle,
                d_day: welfare.start_date
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
    // 현재 시간
    const curr = new Date();
    const utc = curr.getTime() + (curr.getTimezoneOffset() * 60 * 1000);
    const KR_TIME_DIFF = 9 * 60 * 60 * 1000;
    const kr_curr = new Date(utc + (KR_TIME_DIFF));

    // PushAlarm DB에서 시작한 복지 정보를 가진 사용자에게 알람을 보낸다.
    PushAlarm.findAll({where: {d_day : { [Op.lte] : kr_curr}}, raw: true})
    .then(alarms => {
        alarms.forEach(pushinfo => {

            // 1. 파이어베이스 토큰값을 가져온다. 
            User.findByPk(pushinfo.user_id)
            .then(userinfo => {
                // 2. Welfare 정보를 가져온다.
                Welfare.findByPk(pushinfo.welfare_id)
                .then(welfareinfo => {
                    // 3. 푸시알림을 보낸다.
                    let message = 
                    {
                        notification : {
                            title: '모두를 위한 복지 알림입니다.',
                            body: welfareinfo.title,
                        },
                        token : userinfo.user_mToken
                    }

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
                })
            })

        });
      })

}
