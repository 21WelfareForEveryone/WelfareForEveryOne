// 복지 카테고리 모델 생성 
const Sequelize = require('sequelize');
const sequelize = require('../utils/database');

const Welfare_category = sequelize.define('welfare_category', {
    welfare_category_id : {
        primaryKey : true,
        autoIncrement: true,
        allowNull : false,
        type : Sequelize.INTEGER,
    }, 
    welfare_id : {
        allowNull : false,
        type : Sequelize.INTEGER,
    }, 
    category_id : {
        allowNull : false,
        type : Sequelize.INTEGER
    }
}, {
    charset: "utf8", // char format 설정
    collate: "utf8_general_ci", // 한국어 설정 
    timestamps: false, // filestamps 비활성화
    tableName: "welfare_category" // 연결할 table 이름 설정 
});
module.exports = Welfare_category;
