// 찜 모델 생성 

const Sequelize = require('sequelize');
const sequelize = require('../utils/database');

const User_dibs = sequelize.define('user_dib', {
    like_id : {
        primaryKey : true,
        autoIncrement: true,
        allowNull : false,
        type : Sequelize.INTEGER,
    }, 
    user_id : {
        allowNull : false,
        type : Sequelize.STRING,
    }, 
    welfare_id : {
        allowNull : false,
        type : Sequelize.STRING
    }
}, {
    charset: "utf8", // char format 설정
    collate: "utf8_general_ci", // 한국어 설정 
    timestamps: false, // filestamps 비활성화
    tableName: "user_like" // 연결할 table 이름 설정 
});
module.exports = User_dibs;
