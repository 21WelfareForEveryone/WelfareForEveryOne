const express = require('express');
const router = express.Router();
const pushController = require('../controllers/pushController');

router.post('/push/getPushAlarm', pushController.getPushAlarm);

module.exports = router;