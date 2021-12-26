const express = require('express');
const router = express.Router();
const chatbotController = require('../controllers/chatbotController');

router.post('/chatbot/getresponse', chatbotController.getResponse);
router.post('/chatbot/getresponse/dummy0', chatbotController.getResponseDummy0);
router.post('/chatbot/getresponse/dummy1', chatbotController.getResponseDummy1);

module.exports = router;