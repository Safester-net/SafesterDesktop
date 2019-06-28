# Safester Mobile - Delivery Planning - 04/02/2019

[TOC]

## Introduction

This documents describes the different Alpha and Beta versions for Safester Mobile v1.0 and v1.x, and the associated milestones & dates.

We will distinguish Alpha versions that are for KawanSoft teamnly, and Beta versions that can/will be delivered to other persons.

## Functional Changes 

### Address Book & Autocompletion in v1.0

Because encryption of present address book is done with Java methods, it's no possible to decrypt in C# the address book content when downloaded... This will be fixed later.

Thus the 2 APIs `/GetCompletionAddressBook`  and `/SetCompletionEntry` are *not* be implemented in Safester v1.0.

Autocompletion with filling of (recipient name, recipient email) will be managed on Mobile side only using a client side cache mechanism (persistent after phone is off).

### Register interface in v1.0

Register interface in v1.0 will not allow any choice for Cryptography Settings  for the `PgpKeyPairGenerator` Thus Values will be preset  to **RSA - 2048 bits / AES - 256 bits**  on interface . (I.e. C# API`PublicKeyAlgorithm.RSA` & `PublicKeyLength.BITS_2048` .)

### Drafts in v1.0 

*Drafts are very important for user experience*: user must  be allowed to go back to his composing message later.

For implementation in v1.0, just save recipients, subject, body in local cache (No need to save attachments). Draft is automatically discarded during `/sendMessage`.

## Settings

The interface must contain a Settings menu, that will allow to display the following info (UI & look & feel are free on Mobile to be discussed apart).

It will display :

- Account (email address)
- Subscription type (FREE,  SILVER GOLD, )
- Cryptography settings
- Storage info.
- **Name of user.**
- **Checkbox for Sent notification email to**
- **Sent notification email to.**
- *User interface language : French / English*

All info except last one will be retrieved will a call to `/GetSettings` API.

The 3 **bold** info are updatable on interface and a call to`/PutSettings` API will update from interface.

The *User interface language : French / English is updatable* but locally stored.

![1549223476889](C:\Users\Nicolas de Pomereu\AppData\Roaming\Typora\typora-user-images\1549223476889.png)

## Versions table 

### V1.0x versions

| Number  | Type     | Release Date                       | Content                                                      |
| ------- | -------- | ---------------------------------- | ------------------------------------------------------------ |
| 0.80    | Alpha    | **Asap starting February 5, 2019** | Complete Android only. <br>It includes : <br>- Auto-Completion.<br>- Attachments downloads.<br>- Send email with Attachments.<br>- Register.<br>- Drafts (without Attachments.) |
| 0.81    | Alpha    | 0.80 + 1 day                       | Include Settings get & put.                                  |
| 0.90    | Alpha    | 0.81 + 2 days                      | Complete Android + iPhone.                                   |
| **1.0** | **Beta** | **0.90 + 1 day**                   | **First Beta!**                                              |
| 1.01    | Beta     | 1.0 + 1 day                        | - Beta displays automatically English or French Interface. <br>- Settings allows to change the default value. |
### V1.1x versions 

This is not included in present development contract and will be discussed later. 

Only v1.1 is urgent:

| Number  | Type     | Release Date      | Content                                                      |
| ------- | -------- | ----------------- | ------------------------------------------------------------ |
| **1.1** | **Beta** | **To be defined** | **- Includes Drafts** **with attachments.**<br>**- Allows to choose Cryptography Settings in Register.<br>- Allows user to activate subscription for SILVER, GOLD or PLATINUM, as on PC version.** |
| 1.2     | Beta     | To be defined     | Includes Address Book management.                            |
| 1.3     | Beta     | To be defined     | Includes Folders, as on PC version.<br>Includes more PC version features (Vacation responder, etc...) |

___________

