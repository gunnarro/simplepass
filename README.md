[![Android CI](https://github.com/gunnarro/simplepass/actions/workflows/android.yml/badge.svg)](https://github.com/gunnarro/simplepass/actions/workflows/android.yml)
[![Build and Analyze](https://github.com/gunnarro/simplepass/actions/workflows/android-sonarqube.yml/badge.svg)](https://github.com/gunnarro/simplepass/actions/workflows/android-sonarqube.yml)
[![Known Vulnerabilities](https://snyk.io/test/github/gunnarro/simplepass/badge.svg)](https://snyk.io/test/github/gunnarro/simplepass)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=gunnarro_simplepass&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=gunnarro_simplepass)


# saFe-P
saFe-P is an Android App to store username and password on your mobile in a safe and secure way.
Username and password is stored using AES-256 bit encryption with PBKDF2 SHA-256 and salted hashes to keep it secure.
A master password is used in order to store and view saved credentials. The password is fixed and only set once.

This is only a secure way to store username and password on our mobile, this to only help you to not forget any username and password.
This app do not need any access rights and do not have any integration against 3-party systems. Saved credential stays on you mobile.

All data is deleted if uninstalled.


Simple app for storing credentials in a secure way on your mobile phone. You only need one master password in order to access stored credentials. All stored credentials are encrypted by help of the master password.

If you forgot the master password, all data is lost. Because the master password  is never stored and you should be the only one that know it.

Key features:

- Database file is encrypted and only accessible from this app 
- Password is encrypted before stored into the database
- Password strength indicator
- No integration with 3. parties and no kind of data sharing
- Default login whit username and password
  - Possible activate fingerprint login
- Automatic logout after a custom timeout
- All data is deleted when app is uninstalled
- All features are free to use, no advertisement
- 100% based on open source
- Backup to user's google drive is turned off.
- No permission needed


# Resources
 - [flatIcon android](https://www.flaticon.com/free-icons/android)
 - [icons8](https://icons8.com/icons/set/android-logout)
 - [fonts.google.com/icons](https://fonts.google.com/icons)
