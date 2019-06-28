# Safester 

# Mobile Web API & C# API 

# v2.4 - 11/06/19

[TOC]



# Introduction

This documents describes :

1. The HTTP APIs (Web services) for data exchange with Safester Server.
2. The C# APIs for crypto operations



## Whats' New 

Update for this document:

**v2.4**

- Add `searchMessagesFull` API.

**v2.3**

- Add `searchMessages` API.

**v2.2**

- `get2faActivationStatus` API returns `the2faActivationStatus` instead of `2faActivationStatus`

**v2.1**

- `login`API:  add new `error_invalid_2facode` error message and `2faCode` parameter.
- add `get2faActivationStatus` API.

**v2.0**

- `login`API:  add details about error messages.
- `register` API: add details about error messages.

**v1.9**

- Fix `deleteMessage` API display in this doc.

**v1.8**

- Add `deleteMessage` API.

**v1.7**

- Add `getUserSettings` & `setUserSettings` API.

**v1.6**

- Add `setMessageRead` API.

**v1.5**: 

- Fix format problem in doc.

**v1.4** 

- The `recipients` structure  has been updated. It is the same for `listMessages` and `sendMessage` is explained in `listMessages`.

**v1.3:**

- `register` API: add `hashPassphrase` parameter.
- `sendMessage` API: add `recipientType` parameter

**v1.2:** 

- The `login` API returns the `product` which describes the limitations for each product type.
- The `PgpPublicKeyVerifier`has been added to C# Crypto Library v1.0.7. It is now to be used to verify a PGP public key signature before sending an Email with `/sendMessage` Web API.



# HTTP Apis

## General Principles

- Main root URL is https://www.runsafester.net/api

- APIs are SSL only.

- Data download: all data are received from server using *only* JSON format.

- Data upload: separated HTTP parameters. Some values must be HTML encoded before upload.

- All HTTP calls methods are `POST` if not detailed.

------



## register API (To be done at last in development)

Allows for a new user to register on Safester. The OpenPGP key pair is created on user device with C# OpenPGP crypto functions using Bouncy Castle http://www.bouncycastle.org/fr/csharp/.)

### Format

`https://www.runsafester.net/api/register`

### POST Request Parameters

| Name           | Value                                                        |
| -------------- | ------------------------------------------------------------ |
| emailAddress   | user email                                                   |
| name           | user name and firstname  (HTML encoded)                      |
| hashPassphrase | See below for value.                                         |
| privKey        | Private OpenPGP key in BASE64 format. See below in C# crypto. |
| pubKey         | Public OpenPGP key in BASE64 format. See below in C# crypto. |
#### hassPassphrase value

See Java class to be C# translated as is : `PassphraseUtil`. 

Reference: https://www.runsafester.net/api/doc/PassphraseUtil.java

```java
hashPassphrase = PassphraseUtil.computeHashAndSaltedPassphrase(emailAddress, passphrase);
```

### Output

If success:

```json
{
    "status":"OK"
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"<error_message>",
    "exceptionStackTrace":"<exception_stack_trace>"
}
```

### Special error messages

If `errorMessage` value is `error_account_already_exists`, display following error message to user:

```html
This email is already linked to a Safester account.
```



## login API

Allows user to authenticate to remote Safester server. The output `token` value is used in each subsequent calls for authentication.

### Format

`https://www.runsafester.net/api/login`

### POST Request Parameters

| Name           | Value                                                        |
| -------------- | ------------------------------------------------------------ |
| username       | The username/login                                           |
| hassPassphrase | See below for value.                                         |
| 2faCode        | The 2FA code. Not set on first call. To be set when login API replies with `errorMessage` value `error_invalid_2facode.` |
#### hassPassphrase value

See Java class to be C# translated as is : `PassphraseUtil`. 

Reference: https://www.runsafester.net/api/doc/PassphraseUtil.java

```java
hashPassphrase = PassphraseUtil.computeHashAndSaltedPassphrase(emailAddress, passphrase);
```

### Output

If success:

```json
{
    "status":"OK", 
 	"token":"<Hexdecimal token>",
    "product"<int>,
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```



The product defines the capacity of user for sending email:

| Product int value | Product Name |
| ----------------- | ------------ |
| 0                 | FREE         |
| 1                 | SILVER       |
| 2                 | GOLD         |
| 3                 | PLATINUM     |

The capacity of each Product is defined here:  https://www.safester.net/pricing_v3.html

### Special error messages

If `errorMessage` value is `error_account_pending_validation`, display following error message to user:

```html
Please confirm your registration in order to log in.

Check you inbox for ${username} address:
You must click on confirmation link on an incoming email sent by Safester.
```

If `errorMessage` value is `error_invalid_2facode`, this means all login values are OK, but user must enter a 6 digits 2FA code in a new screen. 

Then login is re-verified passing the `2faCode`. If API replies with OK, then access is granted. If API replies with `error_invalid_2facode` error message , then UI error message **"Invalid F2A code. Please retry"** must be displayed and user must input again new `2faCode`.

## get2faActivationStatus API

Returns true if 2FA is activated for the passed username.

### Format

`https://www.runsafester.net/api/get2faActivationStatus`

### POST Request Parameters

| Name     | Value                                            |
| -------- | ------------------------------------------------ |
| username | The username/login                               |
| token    | The authentication token (returned by login API) |

### Output

If success:

```json
{
    "status":"OK", 
 	"the2faActivationStatus":"<boolean>",
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```

## listMessages API

Lists content of a IN, OUT or DRAFT box.

### Format

`https://www.runsafester.net/api/listMessages`

### POST Request Parameters

| Name        | Value                                                        |
| ----------- | ------------------------------------------------------------ |
| username    | The username/login                                           |
| token       | The authentication token (returned by login API)             |
| directoryId | The ID of the box  to list: <br> 1: INBOX<br> 2: OUTBOX<br> 3: DRAFT |
| limit       | `limit` value as in SQL `select * from table limit n offset m;` |
| offset      | `offset` value as in SQL `select * from table limit n offset m;` |

### Output

If success:

```json
{
    "status":"OK", 
    "messages:" [
        "messageId":long,
        "senderEmailAddr":"string",
        "senderName":"string",
        "recipients": [ 
          {
              	"recipientEmailAddr":"string", 
              	"recipientName":"string"
            	"recipientPosition":int,
            	"recipientType":int,
          }, 
          {
             ... 
          }
       ],
       "date":long,
       "size":long,
       "subject":"base64 string",
       "hasAttachs":boolean
       "isRead":boolean
     ],
         
     ... 
     message i,
     ...           
     message n
        
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```

### Notes

- `subject` is an OpenPGP encrypted BASE64 string.
- `date` is as Unix EPOCH (milliseconds > 01/01/1970).
- About `recipients` structure (used also in `sendMessage` API):  
  - `recipientName` contains both name & first name (`"John Smith"`) and is HTML encoded.
  - `recipientPosition`starts at 1 and indicates the position of the recipient in the email when displayed.
  -  `recipientType` is numeric and indicates the precise recipient type to use for the message:
    - `1` for `to`, `2` for `cc` ,`3` for `bcc`.



## SearchMessages API

Search content on IN or OUT box with others search criteria. Works same as Desktop version.

### Format

`https://www.runsafester.net/api/searchMessages`

### POST Request Parameters

| Name         | Value                                                        |
| ------------ | ------------------------------------------------------------ |
| username     | The username/login                                           |
| token        | The authentication token (returned by login API)             |
| searchString | The content of the search asked by the end user. <u>Minimum length is 5 chars</u>.<br><u>String must be HTML encoded.</u> |
| searchType   | The type of the search (see below for values).               |
| dateStart    | long of the start date of the search as timestamp (Unix EPOCH) |
| dateEnd      | long of the end date of the search as timestamp (Unix EPOCH) |
| directoryId  | The ID of the box  to search in: 1: INBOX or 2: OUTBOX       |
### Output

If success, format is exactly same as ouptut of `listMessages` API.

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```

### Notes

- searchType  values:

  SEARCH_ON_SUBJECT: 1 
  SEARCH_ON_CONTENT: 2 
  SEARCH_ON_RECIPIENT: 3 
  SEARCH_ON_SENDER: 4 |

- Search is not allowed on Draft folder in this version.

- If user has selected `SEARCH_ON_CONTENT`, Mobile C# side must do an additional filter to accept only messages where: 

  -  subject match `searchString` **OR **decrypted body match  `searchString` .



## SearchMessagesFull API

Search content on IN or OUT box with others search criteria. (This new version replaces `SearchMessages` that is deprecated and must not be used anymore in mobile code).

### Format

`https://www.runsafester.net/api/searchMessagesFull`

### POST Request Parameters

| Name                  | Value                                                        |
| --------------------- | ------------------------------------------------------------ |
| username              | The username/login                                           |
| token                 | The authentication token (returned by login API)             |
| searchStringContent   | The content of the search asked by the end user for subject or body.<br><u>Minimum length is 5 chars if not null</u>. (Can be null).<br><u>String must be HTML encoded</u>. |
| searchStringRecipient | The content of the search for the recipient email or recipient (to, cc, bcc) name. Can be null.<br><u>String must be HTML encoded if not null</u>. |
| searchStringSender    | The content of the search for the sender email or sender name. Can be null.<br><u>String must be HTML encoded if not null.</u> |
| dateStart             | long of the start date of the search as timestamp (Unix EPOCH) |
| dateEnd               | long of the end date of the search as timestamp (Unix EPOCH) |
| directoryId           | The ID of the box  to search in: 1: INBOX or 2: OUTBOX       |

### Output

If success, format is exactly same as ouptut of `listMessages`  API, with the addition of the encrypted `body`.

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```

### Notes

- Search is not allowed on Draft folder in this version.
- If `searchStringContent` is not null , Mobile C# side must do an additional filter to accept only messages where: 
  - subject match `searchStringContent` **OR **decrypted body match  `searchStringContent` .



## getMessage API

Gets the body of message of a IN, OUT or DRAFT box & get the attachment file names.

### Format

`https://www.runsafester.net/api/getMessage`

### POST Request Parameters

| Name      | Value                                            |
| --------- | ------------------------------------------------ |
| username  | The username/login                               |
| token     | The authentication token (returned by login API) |
| messageId | The Message ID.                                  |

### Output

If success:

```json
{
    "status":"OK", 
    "message_id":long,
    "body":"string",
    "attachments": [ 
          {
              "attachPosition":int,
              "filename":"string", 
              "size":long,
          }, 
          {
             ... 
          }
    ],
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```

### Notes

- `body` is an OpenPGP encrypted BASE64 string.

- `filename` is a file simple name with no directory as with: `myfile.txt`.



##getAttachment API

Allows to get the HTTP  stream of an OpenPGP encrypted attachment file for local save.

### Format

`https://www.runsafester.net/api/getAttachment`  

### POST Request Parameters

| Name           | Value                                            |
| -------------- | ------------------------------------------------ |
| username       | The username/login                               |
| token          | The authentication token (returned by login API) |
| messageId      | The Message ID.                                  |
| attachPosition | The attachment position. Starts at 1.            |

### Output

If success:

```json
<file stream>
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```



## setMessageRead API

Says to server a message has been read by client side (Mobile, ...)

### Format

`https://www.runsafester.net/api/setMessageRead   ` 

### POST Request Parameters

| Name               | Value                                                        |
| ------------------ | ------------------------------------------------------------ |
| username           | The username/login                                           |
| token              | The authentication token (returned by login API)             |
| senderEmailAddress | The email of the user who sent the message                   |
| messageId          | The message ID of the read Message.                          |
### Output

If success:

```json
{
    "status":"OK", 
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```

### Notes

`senderEmailAddress` is used for security check (make sure on server side the correct message is marked).

## deleteMessage API

Says to server a message has been read by client side (Mobile, ...)

### Format

https://www.runsafester.net/api/deleteMessage

### POST Request Parameters

| Name        | Value                                                        |
| ----------- | ------------------------------------------------------------ |
| username    | The username/login                                           |
| token       | The authentication token (returned by login API)             |
| directoryId | The ID of the box  which contains the message to delete: <br/> 1: INBOX
 2: OUTBOX
 3: DRAFT |
| messageId   | The message ID of the Message to delete                                                      \| |
### Output

If success:

```json
{
    "status":"OK", 
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```

### Notes

`directoryId` is required because users may send messages to themselves, so a message may be both in Inbox and Outbox.

## getPublicKey API

Gets the OpenPGP publickey in BASE64 format as a string of any Safester user. 

### Format

`https://www.runsafester.net/api/getPublicKey ` 

### POST Request Parameters

| Name          | Value                                            |
| ------------- | ------------------------------------------------ |
| username      | The username/login                               |
| token         | The authentication token (returned by login API) |
| userEmailAddr | The Safester user email address                  |

### Output

If success:

```json
{
    "status":"OK",
    "publicKey":"<base64 string>"
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```



## getPrivateKey API

Gets the user own OpenPGP private key in BASE64 format as a string. Note that the key is encrypted with the user passphrase.

### Format

`https://www.runsafester.net/api/getPrivateKey` 

### POST Request Parameters

| Name     | Value                                            |
| -------- | ------------------------------------------------ |
| username | The username/login                               |
| token    | The authentication token (returned by login API) |

### Output

If success:

```json
{
    "status":"OK", 
    "privateKey":"<base64 string>"
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```



## getCompletionAddressBook API

Gets the names & email addresses of recipients. The address book must be permanently updated to make sure completion is up to date on devices.

### Format

`https://www.runsafester.net/api/getCompletionAddressBook  ` 

### POST Request Parameters

| Name     | Value                                            |
| -------- | ------------------------------------------------ |
| username | The username/login                               |
| token    | The authentication token (returned by login API) |

### Output

If success:

```json
{
    "status":"OK", 
    "addressBookEntries": [ 
          {
              "emailAddress":"string", 
              "name":"string"
          }, 
          {
             ... 
          }
    ],
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```

### Notes

`name` contains both name & first name ("John Smith") and is HTML encoded.



## setCompletionEntry API

Add an entry on remote address book.

The address book must be permanently updated to make sure completion is up to date on devices.

### Format

`https://www.runsafester.net/api/setCompletionEntry   ` 

### POST Request Parameters

| Name         | Value                                            |
| ------------ | ------------------------------------------------ |
| username     | The username/login                               |
| token        | The authentication token (returned by login API) |
| emailAddress | The entry email adress                           |
| name         | The entry name (HTML encoded)                    |

### Output

If success:

```json
{
    "status":"OK", 
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```

### Notes

`name` contains both name & first name ("John Smith") and is HTML encoded.



## sendMessage API

Sends an email type message to recipients, including attachments.

Subject, body and attachments must be OpenPGP encrypted before send.

### Format

`https://www.runsafester.net/api/sendMessage    ` 

### Multipart POST Request Parameters

| Name                    | Value                                                        |
| ----------------------- | ------------------------------------------------------------ |
| username                | The username/login                                           |
| token                   | The authentication token (returned by login API)             |
| jsonMessageElements     | The message elements :subject, body, recipients list & attachments lists.<br>String values must be HTML encoded. |
| file1, file2, ... filen | `type=file` file parameters                                  |

### jsonMessageElements structure

```json
{ 
    "senderEmailAddr":"string",
    "recipients": [ 
        {
            "recipientEmailAddr":"string", 
            "recipientName":"string",
            "recipientPosition":int,
            "recipientType":int,

        }, 
        {
        ... 
        }
    ],
    "size":long,
    "subject":"base64 string",
    "body":"base64 string",
    "attachments": [ 
          {
            "attachPosition":int,
            "filename":"string", 
             "size":long,
          }, 
          {
             ... 
          }
    ]
}
```

### Output

If success:

```json
{
    "status":"OK", 
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```

### Notes

`recipients` structure is explained in `sendMessage` API.



## getUserSettings API

Gets the user own settings.

### Format

https://www.runsafester.net/api/getUserSettings

### POST Request Parameters

| Name     | Value                                            |
| -------- | ------------------------------------------------ |
| username | The username/login                               |
| token    | The authentication token (returned by login API) |

### Output

If success:

```json
{
    "status":"OK", 
    "name":"string",
    "product":int,
    "cryptographyInfo":"string",
    "mailboxSize":long,
    "notificationOn":bool,
    "notificationEmail":"string"
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```

### Notes

Product type is described in login API.

`name`  contains both name & first name ("John Smith") and is HTML encoded. 

`mailboxSize` indicates  in bytes the storage already used. The maximum storage per product type is described at: https://www.safester.net/pricing_v3.html.

## setUserSettings API

Defines the user own settings to store on server

### Format

https://www.runsafester.net/api/setUserSettings

### POST Request Parameters

| Name              | Value                                            |
| ----------------- | ------------------------------------------------ |
| username          | The username/login                               |
| token             | The authentication token (returned by login API) |
| name              | The first name and last name of the user         |
| notificationOn    | Says if the notification email is active.        |
| notificationEmail | The email address  to use for notifications      |

### Output

If success:

```json
{
    "status":"OK", 
}
```

If failure:

```json
{
    "status":"KO", 
    "errorMessage":"string",
    "exceptionStackTrace":"string"
}
```



# C# APIs for Crypto Operations 

## Safester C# Crypto APIs Library Reference

See https://github.com/kawansoft/Safester.CryptoLibrary for C# APIs Library. 

Includes full documentation & Nuget package.

## How to use C# APIs with Safester

### Subject & Body Decryption

Call after `/login` the Web API `/getPrivateKey` for the current user.

It will return the user private keyring.

Create a `Decryptor` instance and then use it (the same if you want during all session) for all decryptions of the messages subjects and bodies: 

```c#
// It's OK to always use same Decryptor for all decryptions
Decryptor decryptor = new Decryptor(privateKeyring, passphrase);

//Do all decryptions in loop
for(... */all my messages in mailbox */)
{
    string subject = decryptor.Decrypt(base64EncryptedSubject);
    string body = decryptor.Decrypt(base64EncryptedBody);
    ...
}
```

### File Decryption

All encrypted attachments/files downloaded from server are PGP files and have `.pgp` extension. Just remove the `.pgp` to decrypt for user on mobile:

```c#
string encryptedFile = "koala.jpg.pgp";
string decryptedFile = encryptedFile.Substring(0, encryptedFile.LastIndexOf(".")); 
```

```c#
Stream inputStream = File.OpenRead(encryptedFile);
Stream outputStream = File.OpenWrite(decryptedFile); // koala.jpg
decryptor.Decrypt(decryptedFile, encryptedFile);
// Verify integrity. Throw interface error if false! 
bool isOk = decryptor.Verify;
```
### File & Text Encryption

First get all the remote public keys strings with the `/getPublicKey` API.

Then get the PGP `PgpPublicKey` from each key string in order to build the public key `List`:

```c#
PgpPublicKey pgpPublicKey1 = PgpPublicKeyGetter.ReadPublicKey(publicKeyring1);
PgpPublicKey pgpPublicKey2 = PgpPublicKeyGetter.ReadPublicKey(publicKeyring2);
PgpPublicKey pgpPublicKey3 = PgpPublicKeyGetter.ReadPublicKey(publicKeyring3);

// So we will encrypt file for 3 recipients:
List<PgpPublicKey> encKeys = new List<PgpPublicKey>();
encKeys.Add(pgpPublicKey1);
encKeys.Add(pgpPublicKey2);
encKeys.Add(pgpPublicKey3);

// Encrypted file is always same as file + ".pgp" ext:
string clearFile  ="/path/to/koala.jpg"
string encryptedFile  ="/path/to/koala.jpg.pgp"
    
Stream inputStream = File.OpenRead(clearFile);
Stream outputStream = File.OpenWrite(encryptedFile);

bool armor = false; // Always false
bool withIntegrityCheck = true; // Always true

Encryptor encryptor = new Encryptor(armor, withIntegrityCheck);
encryptor.Encrypt(encKeys, inputStream, outputStream);

```
Text encryption is straightforward:

```c#
Encryptor encryptor = new Encryptor(armor, withIntegrityCheck);
string encryptedText = encryptor.Encrypt(encKeys, "hello world!", outputStream);
```

The `encryptedText` is Base64 armored and ready for upload.

### Register (To be done at last in development)

Call `PgpKeyGenerator` with user email email, user passphrase.

Algorithm and key lengths *must* be :

- `PublicKeyAlgorithm.RSA`
- `PublicKeyLength.BITS_2048`

```C#
string identity = "john@smith.com";
char [] passphrase = ""; // <user input

PgpKeyPairGenerator generator = 
    new PgpKeyPairGenerator(identity, passphrase, PublicKeyAlgorithm.RSA, PublicKeyLength.BITS_2048);
PgpKeyPairHolder pgpKeyPairHolder = generator.Generate();
```

Then get the two armored strings after `Generate()` call: 

```c#
String privKey = pgpKeyPairHolder.PrivateKeyRing;
String publicKey = pgpKeyPairHolder.PublicKeyRing;
```

Then call the `/register` API passing `privKey` & `publicKey` and user is registered.

### PGP public key verification before /sendMessage

Each PGP public key in the recipients lists must be verified before sending the email with `sendMessage` Web API.

This is done using the `PgpPublicKeyVerifier` class that allows to verify that the recipient PGP public key is correctly signed with Safester Master key located on the Safester server. 

The Safester Master PGP public key  identity is contact@safelogic.com.

The flow before sending a message to all recipients is thus:

- Download the contact@safelogic.com Master PGP Public key with `/getPublicKey` API.
- Before adding a recipient to the email list, check the signature of the PGP public key.
- If the signature is invalid, warn the Safester user & discard the send: this means that the PGP public key is invalid and forged.

Code sample:

```c#
List<PgpPublicKey> encKeys = new List<PgpPublicKey>();
encKeys = MyCallGetPublicKeys();

// Get the PGP key from server with /getPublicKey 
// and userEmailAddr = "contact@safelogic.com"
String masterPgpPublicKeyring = MyCallGetPublicKey("contact@safelogic.com");

// Send message for all users
List<String> recipientEmails = MyGetRecipients();
List<String> recipientEmailsValidated = new List<String>();

for (int i = 0; i < recipientEmails.Count; i++)
{
    String theRecipientEmail = recipientEmails[i];
    PgpPublicKey theRecipientPgpPublicKey = encKeys[i];

    PgpPublicKeyVerifier verifier = new PgpPublicKeyVerifier();
    bool verify = verifier.VerifySignature(theRecipientPgpPublicKey, 
                                           masterPgpPublicKeyring);
    
    if (!verify)
    {
        Console.WriteLine("ALERT! Recipient PGP Public key is invalid." +
                          " Mail will not be sent for: " + theRecipientEmail);
    }
    else
    {
        // Recipient PGP public is OK and authenticated
        recipientEmailsValidated.Add(theRecipientEmail);
    }
}

// OK, send the email only to validated recipients
MySendMessage(recipientEmailsValidated);

```
____________











### 

### 



