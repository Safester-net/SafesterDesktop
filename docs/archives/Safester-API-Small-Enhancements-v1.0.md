# Safester 

# Mobile Small Enhancements

# v1.0 - 20/02/19

# Introduction

This documents describes  the small enhancements to add to Safester Mobile and that are to be released in v1.0 and v1.01.

## Small Enhancements list 

## v1.0  

To be done asap, in this order. All enhancements are needed for 1.0 first Android and iPhone Beta release:

- register API: returns  error message "error_account_already_exists" if user tries to register with an email address corresponding to an already existing account. (Details will be updated in API doc).
- login API: returns "error_account_pending_validation" if user tries to login, but has not validated his account by clicking URL in confirmation email sent by Safester server. (Details will be updated in API doc).
- login UI, register UI (all UI with passphrase) : an option shall allow to display passphrases in clear text ("Eye symbol", etc.). Option is kind of an on/off switch with dynamic update of UI.
- Settings UI: add a "Mobile Signature" option. Allows user to set the bottom email signature text. Textarea of signature input/display must be on separated UI screen. Textarea mus support copy/paste. See Safester Desktop for implementation.
- read message UI: Add a "Display Message as Stored on Server" hyperlink. See Safester Desktop.
- send message UI: after user has clicked "Send", message is displayed encrypted, and a message box says "Message is encrypted and will be sent!". See Safester Desktop for implementation.
- Settings UI: add an select box "Messages displayed per scroll". See Safester Desktop for implementation. Default value is 25.
- Left Menu : add a "Change Passphrase" sub-menu. See Safester Desktop for implementation.

## v1.01

Less urgent. v1.01 will be released after 1.0 Android & iPhone release, when all is OK:

- Simple Address book: simplified address book, not encrypted. Purpose is to add and display names & email addresses. (Implementation to be defined later).
- I18n : the UI will be automatically display in English, or French only if user locale is  French. Values will be defined in a properties key=value file.

_________________________________





