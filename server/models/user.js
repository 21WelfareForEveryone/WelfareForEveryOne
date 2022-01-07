// User 모델 생성 

const Sequelize = require('sequelize');
const sequelize = require('../utils/database');

const User = sequelize.define('user', {
    user_id : {
        primaryKey : true, 
        allowNull : false,
        unique : true,
        type : Sequelize.STRING
    }, 
    user_password : {
        allowNull : false,
        type : Sequelize.STRING
    },
    user_name : {
        allowNull : false,
        type : Sequelize.STRING
    },
    user_gender : {
        allowNull : true,
        type : Sequelize.INTEGER
    },
    user_address : {
        allowNull : true,
        unique : false,
        type : Sequelize.STRING
    },
    user_life_cycle : {
        allowNull : true,
        unique : false,
        type : Sequelize.INTEGER
    },
    user_is_multicultural : {
        allowNull : true,
        unique : false,
        type : Sequelize.INTEGER
    },
    user_is_one_parent : {
        allowNull : true,
        unique : false,
        type : Sequelize.INTEGER
    },
    user_income : {
        allowNull : true,
        unique : false,
        type : Sequelize.INTEGER
    },
    user_is_disabled : {
        allowNull : true,
        unique : false,
        type : Sequelize.INTEGER
    },
    user_mToken : {
        allowNull : true,
        unique : false,
        type : Sequelize.TEXT
    },
    last_update : {
        allowNull : true,
        unique : false,
        type : Sequelize.DATE
    },
    img_idx : {
        allowNull : false,
        unique : false,
        type : Sequelize.INTEGER
    }
}, {
    charset: "utf8", // char format 설정
    collate: "utf8_general_ci", // 한국어 설정 
    timestamps: false, // filestamps 비활성화
    tableName: "user" // 연결할 table 이름 설정 
});
module.exports = User;
