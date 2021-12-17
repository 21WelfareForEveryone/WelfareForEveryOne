const express = require('express');
const welfareController = require('../controllers/welfareController');
const router = express.Router();

router.post("/rest/welfare/create", welfareController.createWelfare);
router.post("/rest/welfare/read", welfareController.readWelfare);
router.post("/rest/welfare/search", welfareController.searchWelfare);
router.post("/rest/welfare/recommend", welfareController.recommendedWelfare);
router.delete("/rest/welfare/delete", welfareController.deleteWelfare);

module.exports = router;