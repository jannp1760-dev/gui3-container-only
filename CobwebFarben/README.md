# Cobweb Farben

Clientseitige Fabric-Mod für **Minecraft Java 1.21.11**. Sie färbt die Vanilla-Spinnwebtextur um und zeichnet eine konfigurierbare Außenlinie. Die Welt und andere Spieler sehen weiterhin die normale Spinnwebtextur.

## Farben im Spiel ändern

Im Chat kannst du diese Befehle eingeben:

- `/webfarben` zeigt die aktuelle Einstellung.
- `/webfarben haupt FF55FF` setzt die Hauptfarbe.
- `/webfarben linie 202020` setzt die Farbe der Außenlinie.
- `/webfarben linie aus` schaltet die Linie aus.
- `/webfarben breite 1` setzt die Linienbreite (1 bis 3 Pixel).
- `/webfarben reset` stellt die Standardfarben wieder her.

Du kannst Farben mit oder ohne `#` angeben, zum Beispiel `#55CCFF`. Nach jeder Änderung lädt Minecraft die Texturen neu. Die Konfiguration wird in `config/cobwebfarben.properties` gespeichert.

## Bauen

Das Projekt braucht Java 21, Gradle und Fabric Loom. Öffne den Projektordner in IntelliJ IDEA oder führe `gradle build` im Ordner aus. Die Mod-JAR liegt anschließend unter `build/libs/`. Installiere sie zusammen mit Fabric API in den `mods`-Ordner deines Fabric-Profils.

Die Mod ist nur für den Client gedacht und auf Minecraft 1.21.11 festgelegt.

