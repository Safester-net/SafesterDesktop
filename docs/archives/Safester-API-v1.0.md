# Safester 

# Mobile Web API & C# API 

# v1.0 - 14/12/18

[TOC]

# Introduction

This documents describes :

1. The HTTP APIs (Web services) for data exchange with Safester Server.
2. The C# APIs for crypto operations



# HTTP Apis



## General Principles

- Main root URL is https://www.runsafester.net/api

- APIs are SSL only.

- Data download: all data are received from server using *only* JSON format.

- Data upload: separated HTTP parameters. Some values must be HTML encoded before upload.

- All HTTP calls methods are `POST` if not detailed.

------



## Register API

Allows for a new user to register on Safester. The OpenPGP key pair is created on user device with C# OpenPGP crypto functions using Bouncy Castle http://www.bouncycastle.org/fr/csharp/.)

### Format

`https://www.runsafester.net/api/register`

### POST Request Parameters

| Name         | Value                                                      |
| ------------ | ---------------------------------------------------------- |
| emailAddress | user email                                                 |
| name         | user name and firstname  (HTML encoded)                    |
| privKey      | Private OpenPGP key in BASE64 format. See below C# crypto. |
| pubKey       | PublicOpenPGP key in BASE64 format. See below C# crypto.   |

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



## Login API

Allows user to authenticate to remote Safester server. The output `token` value is used in each subsequent calls for authentication.

### Format

`https://www.runsafester.net/api/login`

### POST Request Parameters

| Name       | Value                                                        |
| ---------- | ------------------------------------------------------------ |
| username   | The username/login                                           |
| passphrase | See Java method to be C# translated as is :<br>`PassphraseUtil.computeHashAndSaltedPassphras`<br> Reference: https://www.runsafester.net/api/doc/PassphraseUtil.java |

### Output

If success:

```json
{
    "status":"OK", 
 	"token":"<Hexdecimal token>"
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



## ListMessages API

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
- `recipientName` contains both name & first name (`"John Smith"`) and is HTML encoded.



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
            "recipientName":"string"
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



## C# APIs for Crypto Operations (To be completed)

## Crypto APIs List

ALL C# APis will use Bouncy Castle OpenPGP library (http://www.bouncycastle.org/fr/csharp/).

APIs List:

```C#
// Register class
//
// For new users Register
// InfoKey contains, algo, key size, random etc.
// InfoKey class: to be done
// KeyPairHolder: class to be done
public KeyPairHolder GenerateKeyPair(String, InfoKey);

// Decryptor class 
//
// For inbox messages read: decrypt subject, body & attachements
// Fail if signature KO:
public String Decrypt(String, PrivateKey);
public String Decrypt(Attachmentfile, PrivateKey);

// Encryptor class
//
// For message send: encrypt subject, body a attachements
public String Encrypt(String, List<PublicKey>, bool sign);
public List<File> Encrypt(File, List<PublicKey>, bool sign);

```



## OpenPGP keys Strings

```c#
// PublicKey PrivateKey and are OpenPGP BASE64 formated key blocks
// used as strings:

 -----BEGIN PGP PUBLIC KEY BLOCK-----                             
 Version: BCPG v1.42                                              
                                                               
 mI0ETIuoyQMEALXT7aHjcj5NsZP9OZGxzjSkjLx3l5SI3k/bb1/Dm1xM01DFYBsX 
 6zCQHir9l0lFkVjd66YwahvTx3jD7YYzncykq3e/3VjqbUZ/pYp76SYZDRIgRaJP 
 IyXrvM/ZDgwKPM9YvnyEZ1ThyAOUQWLOwVT2PO0VumMWBMNkL35vuF97ABEBAAG0 
 FmdhZXRhbmdhY2hldEBnbWFpbC5jb22IsAQTAwIAGgUCTIuoyQUJAAAAAAILBwMW 
 AQIDFQIBAoQBAAoJECO6qVvpGxFY17wD/AjNKSYmgnSHcDSr3NvamwaUvfYnzliG 
 vOS3Id6rPVlV9qPYJe9rXCYAjwJlXVG6uecyd x8JUnHcVkznmWE7RxEnOwoiCCT 
 HYcZ TD2Ro8413La/ZKSOLN8LlHBkxGtnqmV0gu  vGpCdn6s2BayiiaX0jqdr 0 
 j9aDTjEHpJMbiJ8EEwMCAAkFAkzmSJEChAEACgkQ2G2yDJ4ff2 SRwP9E5FZmSpZ 
 t6pCiymDgsQ8yFUCG9hSSymkVqOcFZ9VL2OvxQl7HVJ0NpaAteV6SRA aQK2xLir 
 chR9nLtli vIyULrjdh1v0RhnXTBI6Ffosk1KkX9fH3cdNyTGOc1eyIC5fy7DNNu 
 9yygkrnC13q0cJ7RWHjE fOW4RM68y3dwYS4jQRMi6jJAgQAh5Du6J5DK2Hg3hVW 
 69Yra7WYu71hmNsLBICI vVKEPXESzx0CNTb5Qq2HXVR1CIXRbY9J8ZfCGFArMUO 
 Q0KyPNn8dKA2e/fNBQdMptSvNeTZVScUb8txhOjYPralkkt7Ia9WXcW6Qe4n x21 
 i04uvjMmU0ZUfmYEXuzu iKpu48AEQEAAYiwBBgDAgAaBQJMi6jJBQkAAAAAAgsH 
 AxYBAgMVAgEChAEACgkQI7qpW kbEVirnAQAilX5OZyDjTTyq2U56lPbrQrWVp4a 
 9e KduNwXgNg3lP/oCckbt5FGKdgfLCSyDIajbkD 1w4DGQ92wyT0LIDMRN7s1Al 
 htcCslX1BxMITv335JtqkbRUSnZR1b7VLd/sjjpNGOVflXwrJRdUsV9jXPBed0UA 
 gRmq6sYx4Gr52dWInwQYAwIACQUCTOZIkQKEAQAKCRDYbbIMnh9/bwmCA/41oshu 
 Q/Oe3r7czeUDpt2vk25zONQB6rBelHkXPwigFdhjYSHNCb54Toud4XqCc9eOawdW 
 xl JKKQMH2fVBAp8WtlhRdD UUxGOGy2SQCZfq4dKCFU58mi8CeOFZxY9itLVqwW 
 s5PvFBEkJ2eRlsWAkoNJmouQOl8bXTody8pU2w==                         
 =tThj                                                            
 -----END PGP PUBLIC KEY BLOCK-----           

 // Private PGP key is always encrypted with passphrase
 // We provides C# API for decryption:

  -----BEGIN PGP PRIVATE KEY BLOCK-----                            
 Version: BCPG v1.42                                              
                                                             
 lQH0BEyLqMkDBAC10 2h43I TbGT/TmRsc40pIy8d5eUiN5P229fw5tcTNNQxWAb 
 F swkB4q/ZdJRZFY3eumMGob08d4w 2GM53MpKt3v91Y6m1Gf6WKe kmGQ0SIEWi 
 TyMl67zP2Q4MCjzPWL58hGdU4cgDlEFizsFU9jztFbpjFgTDZC9 b7hfewARAQAB 
 /wcDAnCtsyoOLRF8YBP q2Y9ow0mN9S5AdDXLjOLFU75El87h9HRHigmZs7BczgN 
 H1uq5wPjd6ZPpW feXoq6WbTPj5PN61iVOkYmXOBZ 2j7JSuYkiCGLDbWZj0sw2e 
 0uLJE2uwHtBNUAqPBRoqfIPCEYtK8PLxDbEcd9h 9nj2RYhFfGyX1IFrS152ufrs 
 wtZZ24mcWvH50ZmaKsSrcBQXMe5gYQThlUHrcLwruhpd59yF6ads0Dr3/ds5rAf4 
 12lcNh5u3iH2cxeLNKDUx5DK80jC/Khp4yk1c IPdK iLv8B9ZAc2RRTitcUgJ5p 
 SCNDkEBTBosobGeP7ziXZmWSHrnRY18P0YTBORwOakvuLudPWn3CcZbRX j8zMdY 
 tYqblrg 67R860l3GynfWCyHfU3bmpxRw2Y8ULPUnl5cfT5YiFNhRKnlQfXNxl6o 
 85lIc9aQUKSG/uUMZtq1ubuhMDZiX7a0FmdhZXRhbmdhY2hldEBnbWFpbC5jb22I 
 sAQTAwIAGgUCTIuoyQUJAAAAAAILBwMWAQIDFQIBAoQBAAoJECO6qVvpGxFY17wD 
 /AjNKSYmgnSHcDSr3NvamwaUvfYnzliGvOS3Id6rPVlV9qPYJe9rXCYAjwJlXVG6 
 uecyd x8JUnHcVkznmWE7RxEnOwoiCCTHYcZ TD2Ro8413La/ZKSOLN8LlHBkxGt 
 nqmV0gu  vGpCdn6s2BayiiaX0jqdr 0j9aDTjEHpJMbnQH0BEyLqMkCBACHkO7o 
 nkMrYeDeFVbr1itrtZi7vWGY2wsEgIj69UoQ9cRLPHQI1NvlCrYddVHUIhdFtj0n 
 xl8IYUCsxQ5DQrI82fx0oDZ7980FB0ym1K815NlVJxRvy3GE6Ng tqWSS3shr1Zd 
 xbpB7if7HbWLTi6 MyZTRlR ZgRe7O76Iqm7jwARAQAB/wcDAth4GnQejkB7YC6e 
 G7HVbe/rxMEPJO8bOlYP/Bs2 CizuRfuhKnfUA6xapA9W/jjPKMf8XmxaHFFmTZb 
 oOBYRkFobtAJTIvrvgRdSKZHjdlPDnymOyx/8HX8YvVUH49hSShTzUCO1YJ6mu75 
 pZSCaEB7bqHzNZ0hGs44p3/7Vge/4mhn7AjxqItiie6MI3VuWPmNkBpZZRnoV81i 
 jbisjgEgZdQsQUVve4LKlN85YF/pOVb9UfORTestYhQeY5mYV7rD/m3dX3pbBFHx 
 lrEJ8SVLQ8w1Sj4f4Iszx8t Ir2nfjY5dKbSJgPJdAQMOI2Slbe51fRK1Vtui5ww 
 NYisGz7TbAhcwQ5MtnXSt/zCoD0nJueashqOIL9Fhb8qZUiiW7g8kHLvf4149UsK 
 GnUn9YyLGDptSxMZDD6EDnzZUmIyRlwag47Yw9LLrkrpq HpMzbxEpChvoice/dp 
 H001heyBZduIsAQYAwIAGgUCTIuoyQUJAAAAAAILBwMWAQIDFQIBAoQBAAoJECO6 
 qVvpGxFYq5wEAIpV Tmcg4008qtlOepT260K1laeGvXvinbjcF4DYN5T/6AnJG7e 
 RRinYHywksgyGo25A/tcOAxkPdsMk9CyAzETe7NQJYbXArJV9QcTCE799 SbapG0 
 VEp2UdW 1S3f7I46TRjlX5V8KyUXVLFfY1zwXndFAIEZqurGMeBq dnV         
 =rAWm                                                            
 -----END PGP PRIVATE KEY BLOCK-----   

```



____________________













### 

### 



