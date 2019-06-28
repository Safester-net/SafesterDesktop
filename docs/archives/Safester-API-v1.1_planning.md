# Safester 

# Mobile Web API & C# API Planning

# v1.0 - 09/01/19

## Introduction

The Web APIs require C# Crypto classes for Xamarin integration.

We KawanSoft will first develop the APIs that do not require C# Crypto, aka the mobile will work as final version. The only difference will be that :

- Content will not be controlled (no signature verify).
- The emails `Subject` will be encrypted (BASE6 string).
- The emails `Body` contents will be encrypted (BASE6 string).
- The attachments will not be decrypted.

This will allow mobile team to test login, emails list, email display.

We will then develop the C# Crypto APIs, and when the C# C# Crypto API, we will develop the last Web APIs

There thus will be three Modules for delivery:

1. **Module 1:** Web API  without C# Crypto
2. **Module 2**: C# Crypto classes
3. **Module3 :** Web API with C# Crypto

## Web API 

| Web API Name               | Remarks                                                      |
| -------------------------- | ------------------------------------------------------------ |
| `Login`                    | Mandatory (real connection to Safester).<br>Can be used without  C# Crypto. |
| `ListMessages`             | Can be used without  C# Crypto.                              |
| `getMessage`               | Can be used without C# Crypto.                               |
| `getAttachment`            | Can be used without C # Crypto.                              |
| `getPublicKey`             | No C# Crypto required.                                       |
| `getPrivateKey`            | Requires C# Crypto                                           |
| `getCompletionAddressBook` | Requires C# Crypto                                           |
| `setCompletionEntry`       | Requires C# Crypto                                           |
| `sendMessage`              | Requires C# Crypto                                           |
| `Register`                 | Not mandatory for Mobile dev: `Register` can be done with Desktop version during Mobile development.<br>Requires C# crypto. |

## C# Crypto 

| C# Class    | C# Method                                                    |
| ----------- | ------------------------------------------------------------ |
| `InfoKey`   | Constructor.                                                 |
| `Register`  | `KeyPairHolder GenerateKeyPair(String, InfoKey)`             |
| `Decryptor` | `String Decrypt(String, PrivateKey);`<br>`String Decrypt(Attachmentfile, PrivateKey);` |
| `Encryptor` | `String Encrypt(String, List<PublicKey>, bool sign`)<br>`List<File> Encrypt(File, List<PublicKey>, bool sign);` |

## Planning

### Development Time

| Web API                  | Development Time (in days) |
| ------------------------ | -------------------------- |
| Login                    | 0,5                        |
| ListMessages             | 0,5                        |
| getMessage               | 0,5                        |
| getAttachment            | 0,5                        |
| getPublicKey             | 0,5                        |
| getPrivateKey            | 0,5                        |
| getCompletionAddressBook | 0,5                        |
| setCompletionEntry       | 0,5                        |
| sendMessage              | 1                          |
| Register                 | 1                          |

**Total Web APIs: 6 days.**

| C# CLASS    | Development Time (in days) |
| ----------- | -------------------------- |
| `InfoKey`   | 1                          |
| `Register`  | 1                          |
| `Decryptor` | 0,5                        |
| `Encryptor` | 0,5                        |

**Total C# APIs : 3 days.**

### Objectives 

Week 3 - 2019: 

- Delivery of  Module 1 (Web API without C# Crypto).
- Delivery of Module 2 (C# Crypto classes.).

Week 4 - 2019

- Delivery of Module 3 (Web API without C# Crypto) .


_________________





### 

### 



