// Importing Modules
const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const app = express();
require("dotenv").config();

const userRoutes = require('./routes/userRoutes');
const dibsRoutes = require('./routes/dibsRoutes');
const welfareRoutes = require('./routes/welfareRoutes');
const pushRoutes = require('./routes/pushRoutes');
const chatbotRoutes = require('./routes/chatbotRoutes');

// FCM 푸시알림 코드입니다.
const admin = require('firebase-admin')
let serviceAccount = require('./config/hazel-cedar-312311-firebase-adminsdk-75xw8-efe9c58e9f')
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});


// Parsing middleware 
app.use(express.json());
// app.use(bodyParser.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, 'public')));
app.use(bodyParser.json())

// Routers 
app.use(pushRoutes);
app.use(userRoutes);
app.use(welfareRoutes);
app.use(dibsRoutes);
app.use(chatbotRoutes);

app.listen(80);
