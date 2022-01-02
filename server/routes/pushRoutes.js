const express = require('express');
const router = express.Router();
const pushController = require('../controllers/pushController');

router.post('/push/toggle', pushController.pushToggle);

module.exports = router;