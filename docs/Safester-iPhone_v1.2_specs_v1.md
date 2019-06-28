# Safester 

# Mobile iOS - Specs for 1.2.2

# v1.0 - 27/04/19

# Introduction

This documents describes :

1. New Download Dialog for iOS.
2. Photos  picking from Photo Library (iOS only).

## Whats' New 

Update for this document:

**v1.0**

- First version.

# New Download Dialog for iOS

## General Principles

The Download Attachment Dialog must be exactly the same same as on Android:

- **Open the file**
- **Download the file**

Open the file is already implemented and must be kept as incurrent version.

We cover now Download the file iOS implementation.

## Download the file Implementation

When user clicks on `Download the file` , file is downloaded in the Safester App sandbox `Documents/safester` subdirectory which must be created.

`Documents` is the value of :

```c#
Environment.GetFolderPath (Environment.SpecialFolder.MyDocuments);
```

`Documents/safester` subdirectory  must thus be set to be accessible to users. 

The howto to for user access is described precisely in section:

https://docs.microsoft.com/xamarin/ios/app-fundamentals/file-system#sharing-with-the-files-app

(from the Doc: <https://docs.microsoft.com/xamarin/ios/app-fundamentals/file-system>)

Then the user can access his files using is File Explorer.

After the download, as on Android, display a message:

**File successfully** 

**downloaded and decrypted** 

**in the On My Phone / Safester folder.**



# Photos  picking from Photo Library (iOS only).

## Technical Principles

When composing email, iOS users must be able to pick photos.

This is described in:

<https://docs.microsoft.com/xamarin/xamarin-forms/app-fundamentals/dependency-service/photo-picker>

## New Add Attachment dialog

When user clicks on the paper-clip, open a new dialog:

- **Browse Photos Library**
- **Browse My Files**

_____________________________





### 



