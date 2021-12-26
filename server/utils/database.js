// DB 연결
let dbInfo = require("../config/dbInfo");  
const Sequelize = require('sequelize');
const sequelize = new Sequelize("welfare-for-everyone", 'root', dbInfo.password, {
  dialect: 'mysql',
  host: dbInfo.ip
});

module.exports = sequelize;
