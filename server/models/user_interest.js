// 유저 찜 목록 model 생성

const Sequelize = require('sequelize');
const sequelize = require('../utils/database');

const User_interest = sequelize.define('user_interest', {
    interest_id : {
        primaryKey : true,
        autoIncrement: true,
        allowNull : false,
        type : Sequelize.INTEGER,
    }, 
    user_id : {
        allowNull : false,
        type : Sequelize.STRING,
    }, 
    category_id : {
        allowNull : false,
        type : Sequelize.INTEGER
    }
}, {
    charset: "utf8", // char format 설정
    collate: "utf8_general_ci", // 한국어 설정 
    timestamps: false, // filestamps 비활성화
    tableName: "user_interest" // 연결할 table 이름 설정 
});
module.exports = User_interest;
