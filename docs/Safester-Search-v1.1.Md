# Safester 

# Mobile Search

## v1.1 - 11/06/19

## Introduction

This documents describes how to handle Search on mobile with new API `/api/searchMessageFull`.

## General principles

Search on Mobile will work as on main features of Outlook Search (see below)

User may specify a search string :

- for the content (body or email subject),
- for the recipients or the mail (extract of recipient email address or name) ,
- for the sender of the mail (extract of sender email address or name) .

The API to call is `/api/searchMessageFull`. See API specs.

But there will be only 2 folders Inbox, Outbox (no Draft for the moment) on Mobile.



## Symbolic Search Interface 

```
Find [Body or subject ] SEARCH
From [Sender   ] 
To   [Recipient]
Folder [Inbox/Sent]                              
Sent between: [now - 1 month] and [today]
```

Sender and Recipient can be filled by end user with the buyed Syncfusion Component. If a Syncfusion value is fetched, C# client side must remove the name part before sending the `searchStringRecipient` or `searchStringSender` parameter value.

Example: if the Syncfusion creates a "Bruno Paul [brunopaul88@outlook.com]" string, the parameter value to send is only the SMTP address  "brunopaul88@outlook.com".

Default Search In Folder is automatically set depending on from what screen search is called from. Default is Inbox.

Dates *should* be displayed as on Desktop with default locale, as possible.



## Search API & Search Algorithm

Search result is treated by API.

If the `searchContentString` is not null, then C# client side must do the filtering on `searchContentString`  by testing all subjects and body that matches the `searchContentString` substring.

_____________________________



## Outlook Search Samples (to give an idea)

Outlook interface is much richer than what we want, of course. This samples do not show the dates options. 

![1560264781157](C:\Users\Nicolas de Pomereu\AppData\Roaming\Typora\typora-user-images\1560264781157.png)



Other sample :

![1560264839798](C:\Users\Nicolas de Pomereu\AppData\Roaming\Typora\typora-user-images\1560264839798.png)