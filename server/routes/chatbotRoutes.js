const express = require('express');
const router = express.Router();
const chatbotController = require('../controllers/chatbotController');

// router.post('/chatbot/getresponse', chatbotController.getResponse);
router.post('/chatbot/counseling', chatbotController.counseling);
router.post('/chatbot/get_wel_rcmd', chatbotController.get_wel_rcmd);

module.exports = router;