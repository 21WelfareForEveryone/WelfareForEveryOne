// Import Models
const User_dibs = require('../models/dibs'); 
const Welfare = require('../models/welfare'); 
const Welfare_category = require('../models/welfare_category');

// Import modules
let jwt = require("jsonwebtoken"); 
let secretObj = process.env.JWT_SECRET

// 찜 Create Request 처리
exports.createDibs = (req, res, next) => {
    // 토큰 복호화 
    const user_info = jwt.verify(req.body.token, secretObj.secret);

    // 사용자가 찜에 추가하고 싶어하는 welfare id를 likedWelfareIds에 저장
    let likedWelfareIds = [];
    likedWelfareIds.push(req.body.welfare_id);

    // 현재 사용자가 찜한 welfare id를 likedWelfareIds에 저장 
    User_dibs.findAll({where: {user_id: user_info.user_id}, raw: true})
    .then(results => {
        results.forEach(element => {
            likedWelfareIds.push(element.welfare_id)
        });
    })

    // Model에 Instance 생성
    User_dibs.create({
        user_id: user_info.user_id,
        welfare_id: req.body.welfare_id
    })
    .then(()=>{
        // 새로 찜을 받은 Welfare id의 Like count를 하나 증가 
        Welfare.findOne({ where: { welfare_id: req.body.welfare_id } })
        .then(welfare=>{
            Welfare.update({
                like_count: welfare.like_count + 1
            }, { where: { welfare_id: req.body.welfare_id } })
        })
    })
    .then(()=>{
        // 해당 유저가 찜한 모든 Welfare id 가져오기
        User_dibs.findAll({where: { user_id: user_info.user_id }, raw: true})
        .then(dibs_info=>{
            const welfare_ids = []
            dibs_info.forEach(element => {
                if(welfare_ids.indexOf(element.welfare_id) == -1){
                    welfare_ids.push(element.welfare_id)
                }
            });

            // 찜한 Welfare id를 통해 해당 id의 모든 정보를 가져오고 category_id와 isLiked attribute 추가하기
            let welfare_list = []
            Welfare.findAll({where: {welfare_id: welfare_ids}, raw: true})
            .then(result => {
                welfare_list = result;
                Welfare_category.findAll({where:{welfare_id:welfare_ids}, raw: true})
                .then(result=>{
                    result.forEach(element1 => {
                        welfare_list.forEach(element2 => {
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
                    });
                })
                .then(()=>{
                    // App으로 Response 보내기
                    res.send(JSON.stringify({
                        "success" : true,
                        "statusCode": 200,
                        "recommend_welfare_list" :welfare_list,
                        "token" : req.body.token
                    }));
                })  
            })    
        })
    })
    .catch(err=>{
        console.log(err);
        // 오류시 실패 json 보내기
        res.send(JSON.stringify({
            "success": false,
            "statusCode" : 406,
            "dibs_welfare_list": "DENIED",
            "token" : "DENIED"
        }));
    });
}

// 찜 Read Request 처리
exports.readDibs = (req, res, next) =>  {
    // 토큰 복호화 
    const user_info = jwt.verify(req.body.token, secretObj.secret);

    // 현재 사용자가 찜한 welfare id를 likedWelfareIds에 저장 
    let likedWelfareIds = [];
    User_dibs.findAll({where: {user_id: user_info.user_id}, raw: true})
    .then(results => {
        results.forEach(element => {
            likedWelfareIds.push(element.welfare_id)
        });
    })

    // 해당 유저가 찜한 모든 Welfare id 가져오기
    User_dibs.findAll({where: { user_id: user_info.user_id }, raw: true})
    .then(dibs_info=>{
        const welfare_ids = []
        dibs_info.forEach(element => {
            if(welfare_ids.indexOf(element.welfare_id) == -1){
                welfare_ids.push(element.welfare_id)
            }
        });

        // 찜한 Welfare id를 통해 해당 id의 모든 정보를 가져오고 category_id와 isLiked attribute 추가하기
        let welfare_list = []
        Welfare.findAll({where: {welfare_id: welfare_ids}, raw: true})
        .then(result => {
            welfare_list = result;
            Welfare_category.findAll({where:{welfare_id:welfare_ids}, raw: true})
            .then(result=>{
                result.forEach(element1 => {
                    welfare_list.forEach(element2 => {
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
                });
            })
            .then(()=>{
                // App으로 Response 보내기
                res.send(JSON.stringify({
                    "success" : true,
                    "statusCode": 200,
                    "recommend_welfare_list" :welfare_list,
                    "token" : req.body.token
                }));
            })
        })
    })
    .catch(err=>{
        console.log(err);
        // 오류시 실패 json 보내기
        res.send(JSON.stringify({
            "success": false,
            "statusCode" : 406,
            "dibs_welfare_list": "DENIED",
            "token" : "DENIED"
        }));
    });
}

// 찜 데이터 삭제
exports.deleteDibs = (req, res, next) => {
    // 토큰 복호화 
    const user_info = jwt.verify(req.body.token, secretObj.secret);
    let likedWelfareIds = [];
    // User_dibs 모델에서 해당 유저 id와 welfare_id를 가진 인스턴스 삭제
    User_dibs.destroy({
        where: { user_id: user_info.user_id, welfare_id: req.body.welfare_id },
        raw: true
    })
    .then(()=>{
        // 해당 user가 찜한 welfare id 가져오기
        User_dibs.findAll({where: {user_id: user_info.user_id}, raw: true})
        .then(results => {
            results.forEach(element => {
                likedWelfareIds.push(element.welfare_id)
            });
        })
    })
    .then(()=>{
        // 해당 Welfare 정보의 like_count 1 감소
        Welfare.findOne({ where: { welfare_id: req.body.welfare_id } })
        .then(welfare=>{
            Welfare.update({
                like_count: welfare.like_count - 1
            }, { where: { welfare_id: req.body.welfare_id } })
        })
    })
    .then(()=>{
        // user가 찜한 모든 welfare id 가져오기
        User_dibs.findAll({where: { user_id: user_info.user_id }, raw: true})
        .then(dibs_info=>{
            const welfare_ids = []
            dibs_info.forEach(element => {
                if(welfare_ids.indexOf(element.welfare_id) == -1){
                    welfare_ids.push(element.welfare_id)
                }
            });

            // 찜한 Welfare id를 통해 해당 id의 모든 정보를 가져오고 category_id와 isLiked attribute 추가하기
            let welfare_list = []
            Welfare.findAll({where: {welfare_id: welfare_ids}, raw: true})
            .then(result => {
                welfare_list = result;
                Welfare_category.findAll({where:{welfare_id:welfare_ids}, raw: true})
                .then(result=>{
                    result.forEach(element1 => {
                        welfare_list.forEach(element2 => {
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
                    });
                })
                .then(()=>{
                    // App으로 Response 보내기
                    res.send(JSON.stringify({
                        "success" : true,
                        "statusCode": 200,
                        "recommend_welfare_list" :welfare_list,
                        "token" : req.body.token
                    }));
                })
            })
        })
    })
    .catch(err=>{
        console.log(err);
        // 오류시 실패 json 보내기
        res.send(JSON.stringify({
            "success": false,
            "statusCode" : 406,
            "dibs_welfare_list": "DENIED",
            "token" : "DENIED"
        }));
    });
}
