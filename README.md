# SafesterDesktop

## Safester Client Java Code - v4.2 - June 2019, 28 



<img src="https://www.runsafester.net/img/safester-new-64x64.png" alt="Safester Icon"/>



SafesterDesktop is the Java project of [Safester email encryption service](https://www.safester.net).

It contains all source code of both [Safester for Windows](https://safester.net/install_windows/), [Safester for macOS](https://safester.net/install_macos/) and [Safester for Linux](https://safester.net/install_linux/) editions. 

## Cryptography & OpenPGP

SafesterDesktop text and file encryptions are made with the [OpenPGP](https://www.openpgp.org/)  cryptography protocol. 

The cryptographic calls a are all encapsulated in the dedicated Java `cgeep_api.jar` wrapper library  located in the `/lib` subdirectory.

The underlying OpenPGP crypto library used is [Bouncy Castle](http://www.bouncycastle.org). 

`cgeep_api.jar` cryptography source code is available in the `/lib_src` subdirectory. 

Note that Safester binary libraries are not obfuscated, this allows to verify and check the runtime code at any moment.

## License

Safester.Client is licensed with the [LGPL  2.1](https://github.com/ndepomereu/Safester.Client/blob/master/LICENSE) license.

