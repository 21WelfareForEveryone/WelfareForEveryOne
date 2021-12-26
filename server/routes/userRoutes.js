const express = require('express');
const userController = require('../controllers/userController');
const router = express.Router();

router.post('/rest/user/session', userController.userSession);
router.post('/rest/user/login', userController.userLogin);
router.post('/rest/user/register', userController.userRegister);
router.post('/rest/user/read', userController.userRead);
router.put('/rest/user/update', userController.userUpdate);
router.delete('/rest/user/delete', userController.userDelete);
router.get('/', (req, res, next)=>{
    res.status(404);
    res.end();
});


module.exports = router;