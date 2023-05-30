const router = require('express').Router();
const { login, register, profile, home, metadata } = require('../controllers/index');

router.get('/login', login.getLoginPage);
router.post('/login', login.postLoginData);
router.post('/register', register.postRegisterData);
router.get('/profile/:userId', profile.getUserData);

router.get('/home/:page?', home.getBarangData);
router.get('/home/search/:judulbarang', home.getBarangByName);
router.get('/home/bundle', home.getBundle);
router.get('/home/bundle/:idBundle', home.getBundleById);

router.get('/toko/:page?', home.getTokoData);
router.get('/toko/search/:namatoko', home.getTokoByName);

router.get('/metadata', metadata.getMetadata);

module.exports = router;
