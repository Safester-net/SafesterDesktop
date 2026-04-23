# Guide de creation de l'installeur macOS

Flow complet pour generer un DMG macOS de Safester avec des chemins absolus, sans Maven sur le Mac.

## Regle principale

Sur le Mac, ce flow ne doit jamais utiliser Maven.

Le principe est le suivant :

- Maven tourne uniquement sur Windows pour preparer `C:\MacOsX\SafesterMacPayload` ;
- sur le Mac, on reutilise directement ce payload prepare ;
- sur le Mac, on lance uniquement `jpackage` via le script `build-installer.sh`.

## Objectif

Ce guide permet de :

- preparer le payload `jpackage` sur Windows ;
- copier uniquement les fichiers necessaires sur le Mac ;
- tester Safester avant la fabrication finale de l'installeur ;
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
/Users/nicolasdepomereu/SafesterMacPayload
/Users/nicolasdepomereu/SafesterPackaging/macos
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

## 3. Copier uniquement le necessaire vers le Mac

Copier ce dossier :

```text
C:\MacOsX\SafesterMacPayload
```

vers :

```text
/Users/nicolasdepomereu/SafesterMacPayload
```

Copier aussi seulement ce dossier du repository Windows :

```text
I:\Safester\packaging\macos
```

vers :

```text
/Users/nicolasdepomereu/SafesterPackaging/macos
```

Pourquoi cela suffit :

- le payload prepare contient deja `Safester.jar` et les dependances ;
- le build sur Mac se fait avec `jpackage`, jamais avec Maven ;
- si tu passes `--app-version` et `--launcher-icon-path`, le script n'a pas besoin du reste du repository.

## 4. Verifier les fichiers sur le Mac

Dans le Terminal macOS :

```bash
ls -l /Users/nicolasdepomereu/SafesterPackaging/macos/build-installer.sh

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
cd /Users/nicolasdepomereu/SafesterPackaging/macos
```

Puis lancer :

```bash
bash /Users/nicolasdepomereu/SafesterPackaging/macos/build-installer.sh \
  --package-type dmg \
  --target-dir /Users/nicolasdepomereu/SafesterBuild \
  --prepared-input-dir /Users/nicolasdepomereu/SafesterMacPayload/jpackage-input \
  --jdk-home /Library/Java/JavaVirtualMachines/jdk-16.0.2.jdk/Contents/Home \
  --app-version 6.10 \
  --launcher-icon-path /Users/nicolasdepomereu/SafesterMacPayload/resources/safester-icon-80.png
```

Important :

- la presence de `--prepared-input-dir` force l'utilisation du payload deja prepare ;
- dans ce mode, le Mac ne reconstruit pas l'application avec Maven ;
- le Mac fabrique seulement `Safester.app`, puis le `dmg`.

## 5 bis. Tester Safester avant de fabriquer le DMG

Si tu veux verifier que l'application fonctionne avant d'aller jusqu'au package final, tu peux demander uniquement la creation de l'app macOS, sans generer le `dmg`.

Dans le Terminal macOS :

```bash
bash /Users/nicolasdepomereu/SafesterPackaging/macos/build-installer.sh \
  --package-type app-image \
  --target-dir /Users/nicolasdepomereu/SafesterBuild \
  --prepared-input-dir /Users/nicolasdepomereu/SafesterMacPayload/jpackage-input \
  --jdk-home /Library/Java/JavaVirtualMachines/jdk-16.0.2.jdk/Contents/Home \
  --app-version 6.10 \
  --launcher-icon-path /Users/nicolasdepomereu/SafesterMacPayload/resources/safester-icon-80.png
```

L'application est alors generee ici :

```text
/Users/nicolasdepomereu/SafesterBuild/installer/Safester.app
```

Tu peux la lancer tout de suite :

```bash
open /Users/nicolasdepomereu/SafesterBuild/installer/Safester.app
```

Ce test permet de valider :

- que l'application se lance correctement ;
- que le packaging de base fonctionne ;
- que l'icone et le bundle macOS sont bien generes ;
- sans attendre la fabrication finale du `dmg`.

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
- Maven ne doit etre utilise que sur Windows pour preparer le payload.
- Le DMG produit est non signe et non notarise.
- Les chemins de ce guide sont absolus et correspondent au poste de travail cible.
