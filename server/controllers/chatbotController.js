// Import Models and Infos
const User_dibs = require('../models/dibs'); 
const Welfare = require('../models/welfare');
const chatbotInfo = require("../config/chatbot.json");  
const Welfare_category = require('../models/welfare_category'); 

// Import Modules
let jwt = require("jsonwebtoken"); 
let secretObj = process.env.JWT_SECRET
let request = require('request');

// Counseling Dummy API

// exports.counseling = (req, res, next) => {
//     res.send(JSON.stringify({
//         "success": true,
//         "statusCode" : 200,
//         "message_content":  "인공지능 채팅 서비스는 5월 중 업데이트 예정입니다."
//     }));
// }

exports.counseling = (req, res, next) => {
    // 토큰 복호화 
    const user_info = jwt.verify(req.body.token, secretObj);
    
    // Request로부터 user message 추출 
    let user_message = req.body.chat_message;

    // send Request to kmg2933
    const options = {
        uri: process.env.KcELEC,
        method: 'POST',
        json: {
            "message" : user_message,
            "id" : user_info.user_id
        }
    };

    // Chatbot의 Response를 저장할 변수
    let kmgResponse = {};

    // Request를 Chatbot 서버에 요청한다.
    request(options, function (error, response, body) {    
        if (!error && response.statusCode == 200) {
	    	kmgResponse = body;
            
            // Chatbot 서버에서 보낸 응답이 일반 메시지라면 
            let message = "";
            // 메시지를 attribute에 추가하고 App으로 Response를 보낸다.
            chatbotInfo.forEach(element => {
                if (element.id == kmgResponse.message) {
                    message = element.message
                }
            });
            res.send(JSON.stringify({
                "success": true,
                "statusCode" : 200,
                "message_content":  message
            }));
        }	
    })
}

// Recommend Dummy API 
// exports.get_wel_rcmd = (req, res, next) => {
//     let dummy_wel_list = [1, 2, 3]
//     let category_list = []

//     Welfare_category.findAll({where:{welfare_id:dummy_wel_list}})
//     .then(result => {
//         console.log(result);
//         result.forEach(element => {
//             category_list.push(element.category_id);
//         })
//     })
//     .then(() => {
//         Welfare.findAll({where: {welfare_id: dummy_wel_list}, raw: true})
//         .then(result => {
//             result.forEach(element => {
//                 element.isLiked = false;
//             });
//             result.forEach(element => {
//                 element.category = category_list.pop();
//             });
//             return result;
//         })
//         .then(result => {
//             res.send(JSON.stringify({
//                 "success": true,
//                 "statusCode" : 200,
//                 "welfare_info": result
//             }));
//         })    
//     })
// }

exports.get_wel_rcmd = (req, res, next) => {
    // 토큰 복호화 
    const user_info = jwt.verify(req.body.token, secretObj);
    let category_list = []
    
    // Request로부터 user message 추출 
    let user_message = req.body.chat_message;

    // 해당 유저가 찜을 표시한 정보를 User_dibs 모델에서 가져와 저장한다.
    let likedWelfareIds = [];
    User_dibs.findAll({where: {user_id: user_info.user_id}, raw: true})
    .then(results => {
        results.forEach(element => {
            likedWelfareIds.push(element.welfare_id)
        });
    })

    // send Request to kobert
    const optionsKobert = {
        uri: process.env.KsBERT,
        method: 'POST',
        json: {
            "query" : user_message,
            "standard":"full"
        }
    };

    // kobert 서버로 Request 요청을 보내고 Data를 받는다.
    let kobertResponse = {};
    request(optionsKobert, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            // kobert 서버가 보낸 welfare id의 정보를 Welfare Model에서 얻는다.
            kobertResponse = body;
            const welfareIdsKobert = kobertResponse.recommend;

            Welfare_category.findAll({where:{welfare_id:welfareIdsKobert}})
            .then(result => {
                result.forEach(element => {
                    category_list.push(element.category_id);
                })
            })
            .then(() => {
                Welfare.findAll({where: { welfare_id: welfareIdsKobert }, raw: true})
                .then(result=>{
                    // 해당 welfare 정보가 user가 찜을 한 것인지 검사하고 isLiked attribute 추가 
                    result.forEach(element => {
                        if(likedWelfareIds.includes(element.welfare_id)){
                            element.isLiked = true;
                        }
                        else {
                            element.isLiked = false;
                        }
                    });
                    idx = 0;
                    result.forEach(element => {
                        element.category = category_list[idx];
                        idx += 1;
                    });
                    return result;
                })
                .then(result => {
                    // App으로 Response를 보낸다. 
                    res.send(JSON.stringify({
                        "success": true,
                        "statusCode" : 200,
                        "welfare_info": result
                    }));
                })
                .catch(err => {
                    console.log(err);
                })

            });
        }
    });
}


/*
Chatbot Server API 

통신 url : /chatbot
통신 method : POST

Request 
    {
        "message" : "사용자의 메시지"
        "id" : "사용자 id"
    }

Response
    {
        "type" : "welfare or message",
        "message" : "label number",
        "welfare" : ["welfareid1","welfareid2","welfareid3"]    
    }
*/

// exports.getResponse = (req, res, next) =>{
//     /*
//     Logic
//     1. kmg2933 서버에 REQUEST 요청
//     2. RESPONSE에 따라 다르게 처리한다. 
//         2-1. "type" : "welfare" 일 경우 -> Response로 복지 정보를 전달한다.
//         2-2. "type" : "message"이고 message가 특정 label일 경우 -> kosbert_image 서버에 Request를 요청한다
//             2-2-1. Response로 온 세개의 복지정보를 Response로 전달한다.
//         2-3. "type" : "message" 일 경우 -> Response로 답변 정보를 전달한다.
//     */

//     // 토큰 복호화 
//     const user_info = jwt.verify(req.body.token, secretObj);
    
//     // Request로부터 user message 추출 
//     let user_message = req.body.chat_message;

//     // 해당 유저가 찜을 표시한 정보를 User_dibs 모델에서 가져와 저장한다.
//     let likedWelfareIds = [];
//     User_dibs.findAll({where: {user_id: user_info.user_id}, raw: true})
//     .then(results => {
//         results.forEach(element => {
//             likedWelfareIds.push(element.welfare_id)
//         });
//     })

//     // send Request to kmg2933
//     const options = {
//         uri: 'http://localhost:8000/chatbot',
//         method: 'POST',
//         json: {
//             "message" : user_message,
//             "id" : user_info.user_id
//         }
//     };

//     // Chatbot의 Response를 저장할 변수
//     let kmgResponse = {};

//     // Request를 Chatbot 서버에 요청한다.
//     request(options, function (error, response, body) {    
//     	if (!error && response.statusCode == 200) {
// 	    	kmgResponse = body;
            
//             /*
//             kmgResponse = 
//                 {
//                     "message": "350",
//                     "type": "welfare",
//                     "welfare": undefined
//                 }
//             */
            
//             // case 1. "type" == "welfare"일 경우 
//             if (kmgResponse.type == 'welfare') {
//                 // welfare id를 받아서 Welfare 모델로부터 전체 정보를 받는다.
//                 const welfareIds = kmgResponse.welfare;
//                 Welfare.findAll({where: { welfare_id: welfareIds }, raw: true})
//                 .then(result=>{
//                     result.forEach(element => {
//                         // 해당 welfare 정보가 user가 찜을 한 것인지 검사하고 isLiked attribute 추가 
//                         if(likedWelfareIds.includes(element.welfare_id)){
//                             element.isLiked = true;
//                         }
//                         else {
//                             element.isLiked = false;
//                         }
//                     });
//                     return result;
//                 })
//                 .then(result => {
//                     // App으로 Response를 보낸다. 
//                     res.send(JSON.stringify({
//                         "success": true,
//                         "statusCode" : 200,
//                         "message_type": 1,
//                         "message_content": null,
//                         "welfare_info": result
//                     }));
//                 })
//                 .catch(err => {
//                     console.log(err);
//                 })        
//             }
//             // case 2. "type" == "message"일 경우
//             else {

//                 if (kmgResponse.type == 'message' && kmgResponse.message == '360') {
//                     // send Request to kobert
//                     const optionsKobert = {
//                         uri: 'http://localhost:8080/sebert',
//                         method: 'POST',
//                         json: {
//                             "query" : user_message,
// 				            "standard":"full"
//                         }
//                     };

//                     // kobert 서버로 Request 요청을 보내고 Data를 받는다.
//                     let kobertResponse = {};
//                     request(optionsKobert, function (error, response, body) {
//                         if (!error && response.statusCode == 200) {
//                             // kobert 서버가 보낸 welfare id의 정보를 Welfare Model에서 얻는다.
//                             kobertResponse = body;
//                             const welfareIdsKobert = kobertResponse.recommend;
                
//                             Welfare.findAll({where: { welfare_id: welfareIdsKobert }, raw: true})
//                             .then(result=>{
//                                 // 해당 welfare 정보가 user가 찜을 한 것인지 검사하고 isLiked attribute 추가 
//                                 result.forEach(element => {
//                                     if(likedWelfareIds.includes(element.welfare_id)){
//                                         element.isLiked = true;
//                                     }
//                                     else {
//                                         element.isLiked = false;
//                                     }
//                                 });
//                                 return result;
//                             })
//                             .then(result => {
//                                 // App으로 Response를 보낸다. 
//                                 res.send(JSON.stringify({
//                                     "success": true,
//                                     "statusCode" : 200,
//                                     "message_type": 1,
//                                     "message_content": null,
//                                     "welfare_info": result
//                                 }));
//                             })
//                             .catch(err => {
//                                 console.log(err);
//                             })
//                         }
//                     });
//                 }
//                 else {
//                     // Chatbot 서버에서 보낸 응답이 일반 메시지라면 
//                     let message = "";
//                     // 메시지를 attribute에 추가하고 App으로 Response를 보낸다.
//                     chatbotInfo.forEach(element => {
//                         if (element.id == kmgResponse.message) {
//                             message = element.message
//                         }
//                     });
//                     res.send(JSON.stringify({
//                         "success": true,
//                         "statusCode" : 200,
//                         "message_type": 0,
//                         "message_content":  message,
//                         "welfare_info": null
//                     }));
//                 }
//             }
//         }
//     });

// }

