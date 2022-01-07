const PushAlarm = require('../models/push_alarm');
const Welfare = require('../models/welfare'); // 복지정보 모델
const Welfare_category = require('../models/welfare_category'); 
const User_interest = require('../models/user_interest'); 
const User = require('../models/user'); // User 모델 
const schedule = require('node-schedule');
const admin = require('firebase-admin');
const { Op } = require("sequelize");

// let serviceAccount = require('./config/hazel-cedar-312311-firebase-adminsdk-75xw8-efe9c58e9f.json')
// admin.initializeApp({
//     credential: admin.credential.cert(serviceAccount)
// });

module.exports = {
  check: () => {
    schedule.scheduleJob('0 0 8 * * *', async()=>{
    // schedule.scheduleJob('5 * * * * *', async()=>{
        
        // 현재 시간
        const curr = new Date();
        const utc = curr.getTime() + (curr.getTimezoneOffset() * 60 * 1000);
        const KR_TIME_DIFF = 9 * 60 * 60 * 1000;
        const kr_curr = new Date(utc + (KR_TIME_DIFF));

        console.log(kr_curr);
        
        PushAlarm.findAll({raw: true})
        .then(users => {
            users.forEach(user => {
                console.log(user);
                User.findByPk(user.user_id)
                .then(userinfo=>{
                    const fcm_token = userinfo.user_mToken;
                    Welfare.findAll({order: [['like_count', 'DESC']], raw: true})
                    .then(welfare_info=>{
                        console.log(welfare_info[0].title);
                        let message = 
                        {
                            notification : {
                                title: '모두를 위한 복지 알림입니다.',
                                body: welfare_info[0].title,
                            },
                            token: fcm_token
                        }

                        // admin
                        // .messaging()
                        // .send(message)
                        // .then(function(response){
                        //     console.log('Successfully sent message:', response)
                        //     // return res.status(200).json({success: true})
                        // })
                        // .catch(function(err) {
                        //     console.log('Error Sending message!!! : ', err)
                        //     // return res.status(400).json({success: false})
                        // });
                    })
                })
                });

        })

    })

    }
}



/*
            Welfare.findAll({order: ['like_count', 'DESC'], raw: true})
            .then(welfare_info=>{
                
            })

                        // 3. 푸시알림을 보낸다.
                        let message = 
                        {
                            notification : {
                                title: '모두를 위한 복지 알림입니다.',
                                body: "모두를 위한 복지",
                            },
                            token:"faOKk6bCQ4qzp1hKDDanw3:APA91bHK09p1i941ckEhiYJWP3pB3gMK9zX7c41LIkMI2MOTNJ06d3Bj8YepchK7I3bZMtVuxvB-mP7vuGDVCM9KYbe7_s5gQekb4ERYgPRoW8HAWwDU3W99G22pq0n5JXKWqYY0p1TX"
                        }

                        admin
                        .messaging()
                        .send(message)
                        .then(function(response){
                            console.log('Successfully sent message:', response)
                            // return res.status(200).json({success: true})
                        })
                        .catch(function(err) {
                            console.log('Error Sending message!!! : ', err)
                            // return res.status(400).json({success: false})
                        });
*/