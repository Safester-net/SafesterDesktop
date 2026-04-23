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
- utiliser directement le volume partage monte sur le Mac, sans copie manuelle ;
- tester obligatoirement Safester avant la fabrication finale du `dmg` ;
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
/Volumes/MacOsX/SafesterMacPayload
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

Le script exporte aussi les scripts macOS ici :

```text
C:\MacOsX\SafesterMacPayload\scripts\macos
```

## 3. Si le volume MacOsX est monte sur le Mac, ne rien copier

Si le disque partage est deja monte sur le Mac sous :

```text
/Volumes/MacOsX
```

alors tu peux travailler directement dessus, sans recopier manuellement :

```text
/Volumes/MacOsX/SafesterMacPayload
```

Verifier sur le Mac :

```text
/Volumes/MacOsX/SafesterMacPayload/jpackage-input/Safester.jar
/Volumes/MacOsX/SafesterMacPayload/resources/safester-icon-80.png
/Volumes/MacOsX/SafesterMacPayload/scripts/macos/build-from-mounted-volume.sh
```

Dans le Terminal macOS :

```bash
ls -l /Volumes/MacOsX/SafesterMacPayload/jpackage-input/Safester.jar

ls -l /Volumes/MacOsX/SafesterMacPayload/resources/safester-icon-80.png

ls -l /Volumes/MacOsX/SafesterMacPayload/scripts/macos/build-from-mounted-volume.sh
```

## 4. Etape obligatoire : tester Safester.app avant de fabriquer le DMG

Tu peux demander uniquement la creation de l'app macOS, sans generer le `dmg`.

Ne passe pas a l'etape suivante tant que ce test n'est pas valide.

Dans le Terminal macOS :

```bash
bash /Volumes/MacOsX/SafesterMacPayload/scripts/macos/build-from-mounted-volume.sh \
  --package-type app-image \
  --target-dir /Users/nicolasdepomereu/SafesterBuild \
  --jdk-home /Library/Java/JavaVirtualMachines/jdk-16.0.2.jdk/Contents/Home
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

Si ce test n'est pas bon, il ne faut pas fabriquer le `dmg`.

## 5. Construire le DMG depuis le volume monte

Cette etape ne doit etre lancee qu'apres validation complete de `Safester.app`.

```bash
bash /Volumes/MacOsX/SafesterMacPayload/scripts/macos/build-from-mounted-volume.sh \
  --package-type dmg \
  --target-dir /Users/nicolasdepomereu/SafesterBuild \
  --jdk-home /Library/Java/JavaVirtualMachines/jdk-16.0.2.jdk/Contents/Home \
  --open
```

Important :

- ce script relit directement le payload deja prepare sur `/Volumes/MacOsX` ;
- la version est lue automatiquement dans `README-payload.txt` ;
- le Mac ne reconstruit jamais l'application avec Maven ;
- le Mac fabrique seulement `Safester.app`, puis le `dmg`.

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
- Si le volume `MacOsX` est monte, il n'y a rien a copier manuellement sur le Mac.
- Le DMG produit est non signe et non notarise.
- Les chemins de ce guide sont absolus et correspondent au poste de travail cible.
