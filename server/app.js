// Importing Modules
const express = require('express');
const morgan = require('morgan');
const dotenv = require('dotenv');
const path = require('path');
// require("dotenv").config();

dotenv.config({
  path: path.resolve(process.cwd(), '.env.' + process.env.NODE_ENV),
});

const app = express();

const userRoutes = require('./routes/userRoutes');
const dibsRoutes = require('./routes/dibsRoutes');
const welfareRoutes = require('./routes/welfareRoutes');
const pushRoutes = require('./routes/pushRoutes');
const chatbotRoutes = require('./routes/chatbotRoutes');

// scheduling 코드입니다. 
const update = require('./utils/schedule')
update.check()

// FCM 푸시알림 코드입니다.
const admin = require('firebase-admin')
let serviceAccount = require('./config/hazel-cedar-312311-firebase-adminsdk-75xw8-efe9c58e9f');
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

// Parsing middleware 
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(morgan('dev'));

// Routers 
app.use(pushRoutes);
app.use(userRoutes);
app.use(welfareRoutes);
app.use(dibsRoutes);
app.use(chatbotRoutes);

app.listen(3000,() => {
    console.log(process.env.VERSION + ' version');
    console.log(`listening at http://localhost:3000`)
});
