const express = require('express');
const router = express.Router();
const dibsController = require('../controllers/dibsController');

router.post('/rest/dibs/create', dibsController.createDibs);
router.post('/rest/dibs/read', dibsController.readDibs);
router.delete('/rest/dibs/delete', dibsController.deleteDibs);

module.exports = router;