// 복지정보 모델 생성 
const Sequelize = require('sequelize');
const sequelize = require('../utils/database');

const Welfare = sequelize.define('welfare', {
    welfare_id : {
        allowNull : false,
        unique : true,
        primaryKey : true,
        type : Sequelize.INTEGER
    }, 
    title : {
        allowNull : false,
        type : Sequelize.STRING
    },
    summary : {
        allowNull : false,
        type : Sequelize.STRING
    },
    who : {
        type : Sequelize.STRING
    },
    criteria : {
        type : Sequelize.STRING
    },
    what : {
        type : Sequelize.STRING
    },
    how : {
        type : Sequelize.STRING
    },
    calls : {
        type : Sequelize.STRING
    },
    sites : {
        type : Sequelize.STRING
    },
    start_date : {
        type : Sequelize.DATE
    },
    end_date : {
        type : Sequelize.DATE
    },
    url : {
        type : Sequelize.STRING
    },
    like_count : {
        type : Sequelize.INTEGER
    }

}, {
    charset: "utf8", // char format 설정
    collate: "utf8_general_ci", // 한국어 설정 
    timestamps: false, // filestamps 비활성화
    tableName: "welfare" // 연결할 table 이름 설정 
});
module.exports = Welfare;
