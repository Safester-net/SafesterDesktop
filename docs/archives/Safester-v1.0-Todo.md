# Safester Mobile v1.0 - TODO - 07/03/2019

## Introduction

This documents describes the bug fixes and annoyances clearing to do in Safester v1.0 as of March 7, 2019

## TODO LIST

### Register

- C# code used RSA algo that does not work. We had to modify it.  Please reuse as is new code from new ConsoleAppRegister2 C# project. Please reuse as is the 2 classes in Register folder, just modify RegisterApi.cs to call your existing PassphraseUtil.cs :
  - HttpSender.cs
  - RegisterApi.cs 
- When user hits the CREATE button, there must be an immediate display of progress indicator that prevents  that the user user can not again press CREATE button. This is very important.
- Email syntax is checked on server. If email is invalid, error message is "error_invalid_email_address".

### Others

All passphrase fields: for the eyes icon, please use from g_iconex set:

- eye.png
- eye_blind.png

![1551954017554](C:\Users\Nicolas de Pomereu\AppData\Roaming\Typora\typora-user-images\1551954017554.png)

![1551954051050](C:\Users\Nicolas de Pomereu\AppData\Roaming\Typora\typora-user-images\1551954051050.png)

Read Message detail:

- **Bug:** the ".txt" extension is always added to the decrypted file.
- When users clicks on attach, **suppress the "What do you want" question.**
- In version 1.0 **on Android,** don't give the download folder real name,  Just download the file, and then display the always the same message on 3 lines, <br>**File successfully <br>downloaded and decrypted <br>in your Download folder <br>**

- Compose message:
  - When input of to address: the upper part of the text is cut:

![1551954119740](C:\Users\Nicolas de Pomereu\AppData\Roaming\Typora\typora-user-images\1551954119740.png)