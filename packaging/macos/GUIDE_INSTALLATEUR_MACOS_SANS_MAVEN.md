# Guide de creation de l'installeur macOS

Flow complet pour generer un DMG macOS de Safester avec des chemins absolus, sans Maven sur le Mac.

## Objectif

Ce guide permet de :

- preparer le payload `jpackage` sur Windows ;
- copier les fichiers necessaires sur le Mac ;
- construire un DMG macOS non signe depuis le Mac ;
- tester l'application et le DMG generes.

## Chemins utilises

### Windows

```text
I:\Safester
C:\MacOsX\SafesterMacPayload
```

### macOS

```text
/Users/nicolasdepomereu/Downloads
/Users/nicolasdepomereu/Safester
/Users/nicolasdepomereu/SafesterMacPayload
/Users/nicolasdepomereu/SafesterBuild
/Library/Java/JavaVirtualMachines/jdk-16.0.2.jdk/Contents/Home
```

## 1. Installer OpenJDK 16.0.2 sur le Mac

Dans le Terminal macOS :

```bash
cd /Users/nicolasdepomereu/Downloads

curl -L -o openjdk-16.0.2_osx-x64_bin.tar.gz \
  https://download.java.net/java/GA/jdk16.0.2/d4a915d82b4c4fbb9bde534da945d746/7/GPL/openjdk-16.0.2_osx-x64_bin.tar.gz
```

Installer le JDK :

```bash
sudo mkdir -p /Library/Java/JavaVirtualMachines

sudo tar -xzf /Users/nicolasdepomereu/Downloads/openjdk-16.0.2_osx-x64_bin.tar.gz \
  -C /Library/Java/JavaVirtualMachines

sudo xattr -dr com.apple.quarantine \
  /Library/Java/JavaVirtualMachines/jdk-16.0.2.jdk
```

Verifier l'installation :

```bash
/Library/Java/JavaVirtualMachines/jdk-16.0.2.jdk/Contents/Home/bin/java -version

/Library/Java/JavaVirtualMachines/jdk-16.0.2.jdk/Contents/Home/bin/jpackage --version
```

Resultat attendu :

```text
openjdk version "16.0.2"
16.0.2
```

## 2. Preparer le payload sur Windows

Dans Windows PowerShell :

```powershell
powershell -ExecutionPolicy Bypass -File I:\Safester\packaging\macos\prepare-jpackage-input.ps1
```

Le script cree le dossier :

```text
C:\MacOsX\SafesterMacPayload
```

## 3. Copier les dossiers Windows vers le Mac

Copier ce dossier :

```text
C:\MacOsX\SafesterMacPayload
```

vers :

```text
/Users/nicolasdepomereu/SafesterMacPayload
```

Copier aussi le repository :

```text
I:\Safester
```

vers :

```text
/Users/nicolasdepomereu/Safester
```

## 4. Verifier les fichiers sur le Mac

Dans le Terminal macOS :

```bash
ls -l /Users/nicolasdepomereu/Safester/packaging/macos/build-unsigned-dmg-from-prepared-input.sh

ls -l /Users/nicolasdepomereu/SafesterMacPayload/jpackage-input/Safester.jar

ls -l /Users/nicolasdepomereu/SafesterMacPayload/resources/safester-icon-80.png
```

L'idee est de confirmer que :

- le script de build est bien present ;
- le `Safester.jar` a bien ete copie dans `jpackage-input` ;
- l'icone `safester-icon-80.png` est disponible pour le lanceur macOS.

## 5. Construire le DMG sur le Mac

Toujours dans le Terminal macOS :

```bash
cd /Users/nicolasdepomereu/Safester
```

Puis lancer :

```bash
bash /Users/nicolasdepomereu/Safester/packaging/macos/build-unsigned-dmg-from-prepared-input.sh \
  --jdk-home /Library/Java/JavaVirtualMachines/jdk-16.0.2.jdk/Contents/Home \
  --app-version 6.10 \
  --launcher-icon-path /Users/nicolasdepomereu/SafesterMacPayload/resources/safester-icon-80.png
```

## 6. Resultat attendu

Le DMG doit etre genere ici :

```text
/Users/nicolasdepomereu/SafesterBuild/Safester-6.10.dmg
```

L'application macOS doit etre generee ici :

```text
/Users/nicolasdepomereu/SafesterBuild/installer/Safester.app
```

## 7. Tester l'application et le DMG

Ouvrir d'abord l'application :

```bash
open /Users/nicolasdepomereu/SafesterBuild/installer/Safester.app
```

Puis ouvrir le DMG :

```bash
open /Users/nicolasdepomereu/SafesterBuild/Safester-6.10.dmg
```

Si macOS bloque l'application :

1. faire un clic droit sur `Safester.app` ;
2. choisir `Open`.

## Notes utiles

- Le build macOS doit etre lance depuis un Mac.
- Ce flow ne necessite pas Maven sur le Mac.
- Le DMG produit est non signe et non notarise.
- Les chemins de ce guide sont absolus et correspondent au poste de travail cible.
