// Importing Models
const Welfare = require('../models/welfare'); 
const Welfare_category = require('../models/welfare_category'); 
const User_dibs = require('../models/dibs'); 

// Importing Modules
const request = require('request');
let jwt = require("jsonwebtoken");
let secretObj = process.env.JWT_SECRET

// 복지 정보 Create Request 처리 
exports.createWelfare = (req, res, next) => {
    // Welfare Instance 생성
    Welfare.create({
        welfare_id: req.body.welfare_id,
        title: req.body.title,
        summary: req.body.summary,
        who: req.body.who,
        criteria: req.body.criteria,
        what: req.body.what,
        how: req.body.how,
        calls: req.body.calls,
        sites: req.body.sites,
        like_count: 0
    })
    .then(result=>{
        // 성공 json 보내기
        res.send(JSON.stringify({
            "success": true,
            "statusCode" : 201,
            "welfare_id" : req.body.welfare_id
        }));
    })
    .catch(err=>{
        console.log(err);
        // 실패 json 보내기
        res.send(JSON.stringify({
            "success": false,
            "statusCode" : 202,
            "welfare_id" : "DENIED"
        }));
    })
}

// 복지 정보 Read Request 처리 
exports.readWelfare = (req, res, next) => {
    // 토큰 복호화
    const user_info = jwt.verify(req.body.token, secretObj);
    
    // 해당 유저가 찜한 복지정보 User_dibs model에서 가져오기
    let isLiked = false;
    User_dibs.findAll({where: {user_id: user_info.user_id}, raw: true})
    .then(results => {
        // 유저가 요청한 welfare id가 유저가 찜한 welfare id라면 isLiked = true로 변경
        results.forEach(element => {
            if (element.welfare_id == req.body.welfare_id) {
                isLiked = true;
            }
        });
    })

    // 유저가 요청한 welfare id에 대한 정보를 가져온다.
    let category = 0;
    Welfare_category.findOne({where:{welfare_id:req.body.welfare_id}})
    .then(result => {
        category = result.category_id;
        Welfare.findByPk(req.body.welfare_id)
        .then(welfare=>{
            // 성공시 복지 정보 json 보내기
            res.send(JSON.stringify({
                "success": true,
                "statusCode" : 200,
                "welfare_id" : req.body.welfare_id,
                "title" : welfare.title,
                "summary": welfare.summary,
                "who" : welfare.who,
                "criteria": welfare.criteria,
                "what" : welfare.what,
                "how": welfare.how,
                "calls" : welfare.calls,
                "sites": welfare.sites,
                "category" : welfare.category,
                "like_count": welfare.like_count,
                "category": category,
                "isLiked": isLiked        
            })); 
        })
    })
    .catch(err=>{
        console.log(err);
        // 오류시 실패 json 보내기
        res.send(JSON.stringify({
            "success": false,
            "statusCode" : 406,
            "welfare_id" : req.body.welfare_id
        }));
    })

}

// 복지 정보 Search Request
exports.searchWelfare = (req, res, next) => {
    // Request로부터 Category 번호 받기
    const category = req.body.welfare_category;
    let welfare_num = 0;

    // 토큰 복호화
    const user_info = jwt.verify(req.body.token, secretObj);
    
    // 해당 유저가 찜한 복지정보 User_dibs model에서 가져오기
    let likedWelfareIds = [];
    User_dibs.findAll({where: {user_id: user_info.user_id}, raw: true})
    .then(results => {
        // 유저가 찜한 복지 정보의 id를 likedWelfareIds에 저장한다. 
        results.forEach(element => {
            likedWelfareIds.push(element.welfare_id)
        });
    })

    // 해당 Category 번호에 해당하는 복지의 welfare_id 배열로 추출 
    Welfare_category.findAll({where: {category_id: category}, raw: true})
    .then(welfare_info => {
        let welfare_ids = [];
        welfare_info.forEach(element=>{
            // 30개를 추출하도록 설정됨
            if(welfare_num < 30){
                welfare_ids.push(element.welfare_id);
            }
            welfare_num += 1;
        })
        
        // welfare_id 배열에 들어있는 복지 정보를 Response로 반환
        Welfare.findAll({where: { welfare_id: welfare_ids },order: [['like_count', 'DESC']], raw: true})
        .then(result=>{
            // 해당 복지정보가 사용자가 찜한 복지정보라면 isLiked attribute를 true로 추가한다. 아니면 false로 추가
            result.forEach(element => {
                if(likedWelfareIds.includes(element.welfare_id)){
                    element.isLiked = true;
                }
                else {
                    element.isLiked = false;
                }
            });

            return result
        })
        .then(result => {
            // 성공시 성공 json 보내기
            res.send(JSON.stringify({
                "success": true,
                "statusCode" : 201,
                "welfare_list": result,
                "token" : req.body.token
            }));
        })
    })
    .catch(err=>{
        console.log(err);
    })
}

// 추천 복지 정보 GET Request
/*
    // AI 서버 API //

    통신 url : /main
    통신 method : POST   
    
    Request 
    {
        "id" : "사용자 id"
    }

    Response
    {
        "welfare" : ["welfareid1","welfareid2","welfareid3","welfareid4","welfareid5","welfareid6"]    
    }
*/
// exports.recommendedWelfare = (req, res, next) => {
//     let dummy_wel_list = [0, 1, 2, 3, 4, 5]

//     Welfare.findAll({where: {welfare_id: dummy_wel_list}, raw: true})
//     .then(result => {
//         result.forEach(element => {
//             element.isLiked = false;
//         });
//         return result;
//     })
//     .then(result => {
//         res.send(JSON.stringify({
//             "success" : true,
//             "statusCode": 200,
//             "recommend_welfare_list" :result,
//             "token" : req.body.token
//         }));
//     })
// }

exports.recommendedWelfare = (req, res, next) => {
    // 토큰 복호화 
    const user_info = jwt.verify(req.body.token, secretObj);

    // 민규 서버로 Request 보내기 
    // Response로 도착한 추천복지 6개의 id의 전체 정보를 서버로부터 받아서 res로 전달

    // option parameter 지정
    const options = {
        uri: process.env.KcELEC_RECOMMEND,
        method: 'POST',
        json: {
            "id" : user_info.user_id
        }
    };

    // 해당 유저가 찜한 복지정보 User_dibs model에서 가져오기
    let likedWelfareIds = [];
    User_dibs.findAll({where: {user_id: user_info.user_id}, raw: true})
    .then(results => {
        // 유저가 찜한 복지 정보의 id를 likedWelfareIds에 저장한다. 
        results.forEach(element => {
            likedWelfareIds.push(element.welfare_id)
        });
    })

    let category_list = [];

    // Request를 보내서 추천 복지정보 Response로 얻는다.
    request(options, function (error, response, body) {
        let welfare_list = []
        if (!error && response.statusCode == 200) {
            Welfare_category.findAll({where:{welfare_id:body['welfare']}})
            .then(result => {
                result.forEach(element => {
                    category_list.push(element.category_id);
                })
            })
            .then(() => {
                Welfare.findAll({where: {welfare_id:body['welfare']}, raw: true})
                .then(result => {
                    welfare_list = result;
                    Welfare_category.findAll({where:{welfare_id:body['welfare']}, raw: true})
                    .then(result=>{
                        result.forEach(element1 => {
                            welfare_list.forEach(element2 => {
                                // AI로부터 추천받은 복지 정보가 사용자가 찜한 복지정보인지 검사하고 isLiked attribute 추가
                                if(element1.welfare_id == element2.welfare_id){
                                    element2.category = element1.category_id
                                }
                                if(likedWelfareIds.includes(element2.welfare_id)){
                                    element2.isLiked = true;
                                }
                                else {
                                    element2.isLiked = false;
                                }
                            })
                            let idx = 0;
                            welfare_list.forEach(element => {
                                element.category = category_list[idx];
                                idx += 1;
                            });
                        });
                    })
                    .then(()=>{
                        // App에 Repsonse 보내기
                        res.send(JSON.stringify({
                            "success" : true,
                            "statusCode": 200,
                            "recommend_welfare_list" :welfare_list,
                            "token" : req.body.token
                        }));
                    })
                })

            })

        }
    });
}

// 복지 정보 Delete Request 처리 
exports.deleteWelfare = (req, res, next) => {
    // 나중에 필요시 구현예정
}
