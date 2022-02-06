const express = require('express');
const router = express.Router();
const chatbotController = require('../controllers/chatbotController');

router.post('/chatbot/getresponse', chatbotController.getResponse);

module.exports = router;