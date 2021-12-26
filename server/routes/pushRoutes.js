const express = require('express');
const router = express.Router();
const admin = require('firebase-admin')
const pushController = require('../controllers/pushController');

router.post('/push/getinfo', pushController.getInfo);
router.post('/push/toggle', pushController.pushToggle);
router.post('/push/getPushAlarm', pushController.getPushAlarm);

module.exports = router;