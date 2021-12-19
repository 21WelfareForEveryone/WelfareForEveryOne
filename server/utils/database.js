// DB 연결
// let dbInfo = require("../config/dbInfo");  
const Sequelize = require('sequelize');
const sequelize = new Sequelize("welfare-for-everyone", process.env.DB_USER, process.env.DB_PASSWORD, {
  dialect: 'mysql',
  host: process.env.DB_HOST
});

module.exports = sequelize;
