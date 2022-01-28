const PushAlarm = require('../models/push_alarm');
const Welfare = require('../models/welfare'); // 복지정보 모델
const Welfare_category = require('../models/welfare_category'); 
const User_interest = require('../models/user_interest'); 
const User = require('../models/user'); // User 모델 
const schedule = require('node-schedule');
const admin = require('firebase-admin');
const { Op } = require("sequelize");

module.exports = {
  check: () => {
    schedule.scheduleJob('0 0 8 * * *', async()=>{
    // schedule.scheduleJob('5 * * * * *', async()=>{
        
        // 현재 시간
        const curr = new Date();
        const utc = curr.getTime() + (curr.getTimezoneOffset() * 60 * 1000);
        const KR_TIME_DIFF = 9 * 60 * 60 * 1000;
        const kr_curr = new Date(utc + (KR_TIME_DIFF));

        // console.log(kr_curr);
        PushAlarm.findAll({raw: true})
        .then(users=>{
            users.forEach(user => {
                User.findOne({where: {user_id:user.user_id}, raw: true})
                .then(userinfo=>{
                    const fcm_token = userinfo.user_mToken;
                    // console.log(userinfo);
                    User_interest.findAll({where: {user_id:userinfo.user_id}, raw: true})
                    .then(interests=>{
                        let category_list = []
                        interests.forEach(category => {
                            category_list.push(category.category_id)
                        });
                        return category_list
                    })
                    .then(category_list=>{
                        Welfare_category.findAll({where: {category_id: category_list}, raw: true})
                        .then(welfare_category=>{
                            let welfare_list = [];
                            // console.log(welfare_category);
                            welfare_category.forEach(welfare => {
                                if(!welfare_list.includes(welfare.welfare_id)){
                                    welfare_list.push(welfare.welfare_id)
                                } 
                            });
                            
                            return welfare_list;
                        })
                        .then(welfare_list=>{
                            Welfare.findAll({where: {welfare_id:welfare_list}, order: [['like_count', 'DESC']], raw:true, limit :10})
                            .then(result=>{
                                let rand_0_9 = Math.floor(Math.random() * 10);
                                // console.log(fcm_token, result[rand_0_9].title);
                                let message = 
                                {
                                    notification : {
                                        title: '모두를 위한 복지 알림입니다.',
                                        body: result[rand_0_9].title,
                                    },
                                    token: fcm_token
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
                            })
                        })
                    })
                })

            })
        })
    })

    }
}
