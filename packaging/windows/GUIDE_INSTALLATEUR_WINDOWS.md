# Guide de creation de l'installeur Windows

Flow complet pour generer un installeur Windows de Safester avec des chemins absolus.

Le flow principal ci-dessous construit un installeur `exe` non signe. Une variante `msi` est aussi incluse a la fin.

## Objectif

Ce guide permet de :

- verifier les prerequis Windows ;
- construire l'application Safester avec Maven ;
- generer un app-image Windows avec `jpackage` ;
- generer un installeur Windows `exe` ou `msi` ;
- tester l'installeur genere.

## Chemins utilises

### Workspace

```text
I:\Safester
```

### Sortie build Windows

```text
C:\MacOsX\SafesterBuild
```

### JDK Windows

```text
C:\Program Files\Apache NetBeans\jdk
```

## 1. Verifier OpenJDK sur Windows

Dans Windows PowerShell :

```powershell
& "C:\Program Files\Apache NetBeans\jdk\bin\java.exe" -version

& "C:\Program Files\Apache NetBeans\jdk\bin\jpackage.exe" --version
```

Resultat attendu :

```text
une version Java >= 17
une version jpackage disponible
```

Le script Windows attend un OpenJDK 17 ou plus recent, avec `jpackage`.

## 2. Verifier Maven et WiX Toolset

Toujours dans Windows PowerShell :

```powershell
mvn -version

Get-Command candle.exe

Get-Command light.exe
```

L'idee est de confirmer que :

- Maven est disponible dans le `PATH` ;
- WiX Toolset est disponible dans le `PATH` pour construire le package Windows.

## 3. Verifier les fichiers du projet

Toujours dans Windows PowerShell :

```powershell
Test-Path I:\Safester\moyocore_x64.dll

Test-Path I:\Safester\packaging\windows\build-unsigned-exe.ps1

Test-Path I:\Safester\packaging\windows\build-installer.ps1
```

Tu dois obtenir `True` pour ces verifications.

## 4. Construire l'installeur EXE sur Windows

Dans Windows PowerShell :

```powershell
powershell -ExecutionPolicy Bypass -File I:\Safester\packaging\windows\build-unsigned-exe.ps1 `
  -TargetDir "C:\MacOsX\SafesterBuild" `
  -JdkHome "C:\Program Files\Apache NetBeans\jdk" `
  -AppVersion "6.10"
```

Ce script fait automatiquement les actions suivantes :

- build Maven du projet ;
- copie des dependances runtime ;
- creation de l'app-image Windows ;
- copie de `moyocore_x64.dll` dans l'image ;
- generation de l'installeur `exe`.

## 5. Resultat attendu pour l'EXE

Le dossier d'installation intermediaire doit etre ici :

```text
C:\MacOsX\SafesterBuild\installer
```

L'app-image doit etre ici :

```text
C:\MacOsX\SafesterBuild\installer\Safester
```

L'installeur genere doit etre copie dans le dossier racine de sortie :

```text
C:\MacOsX\SafesterBuild\Safester-6.10.exe
```

Le package est genere sous `C:\MacOsX\SafesterBuild\installer`, puis copie vers `C:\MacOsX\SafesterBuild`.

## 6. Tester l'installeur EXE

Dans Windows PowerShell :

```powershell
Start-Process "C:\MacOsX\SafesterBuild\Safester-6.10.exe"
```

Ensuite :

1. suivre l'assistant d'installation ;
2. verifier que Safester se lance correctement ;
3. verifier que le raccourci Windows est bien cree ;
4. verifier que l'entree menu Demarrer Safester est bien presente.

## 7. Variante MSI

Si tu veux produire un `msi` au lieu d'un `exe`, utilise :

```powershell
powershell -ExecutionPolicy Bypass -File I:\Safester\packaging\windows\build-unsigned-msi.ps1 `
  -TargetDir "C:\MacOsX\SafesterBuild" `
  -JdkHome "C:\Program Files\Apache NetBeans\jdk" `
  -AppVersion "6.10"
```

Resultat attendu :

```text
C:\MacOsX\SafesterBuild\Safester-6.10.msi
```

Tu peux le tester avec :

```powershell
Start-Process "C:\MacOsX\SafesterBuild\Safester-6.10.msi"
```

## Notes utiles

- Le build Windows doit etre lance depuis Windows.
- Le script attend un OpenJDK 17+ avec `jpackage`.
- Le script attend Maven dans le `PATH`.
- Le script attend WiX Toolset dans le `PATH` pour `exe` et `msi`.
- `moyocore_x64.dll` est requis et copie automatiquement dans le package.
- L'UUID d'upgrade Windows est fixe dans le script pour conserver le comportement de mise a jour.
- Si besoin, tu peux remplacer `6.10` par une autre version de package.
