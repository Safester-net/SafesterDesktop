# Safester 

# Mobile 2FA 

## v1.0 - 19/05/19

[TOC]

# Introduction

This documents describes how to handle 2FA activation (set with Dekstop version)

### Login 2FA behavior

See Safester-iPhone_v1.2_specs_v1.pdf for details.

When /login API is called, server may return an `error_invalid_2facode` that means that 2FA is required.

In this case, recall login adding HTTP `2faCode` parameter value.

##  2FA  Input Screen

Do same as on Desktop image below. Do not try to force or format numbers input to user<u>. It is mandatory to allow easy paste of 6 digits code from 2FA software</u>.

Desktop 2FA Input Screen may be simplified and text content reduced...

If use hits OK, `/login` call is replayed with `2faCode` value. (code must first be checked to be 6 digits long with numbers only.)

![1558292280566](C:\Users\Nicolas de Pomereu\AppData\Roaming\Typora\typora-user-images\1558292280566.png)

If user hits Cancel, go to first login screen.

## 2FA  Menu (Android only)

<u>On Android only</u>, add a "2FA Activation" on left menu.

It must display a "2FA Activation Status" switch/button that is On or Off. (Like the Signature Switch in Settings)

The status can not be updated: if user tries to change status with the Switch button, a pop-up is displayed as for Change Passphrase:

**Download Safester Desktop Edition in order to configure 2FA Activation**

**(www.safester.net).**

### Testing values for 2FA on Mobile

Use this account : ndepomereu@gmail.com 

passphrase: safester123

validation code 543543.



_____________________________





