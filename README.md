# Kurssivalinta-avustin

Copyright (C) 2022 Väinö Viinikka

Copying and distribution of this file, with or without modification,
are permitted in any medium without royalty provided the copyright
notice and this notice are preserved.  This file is offered as-is,
without any warranty.
## Yleistä
Kurssivalinta-avustin on ohjelma, jonka tarkoituksena on yksinkertaistaa työkalua, jolla Wilmassa tehdään lukion kurssivalinnat. Se lataa kurssitarjottimet suoraan Wilman nettisivuilta, ja esittää ne omassa käyttöliittymässään, joka tarjoaa seuraavia ominaisuuksia valintojen teon helpottamiseksi:  
- kun käyttjäjä valitsee tietyn kurssin, saman kurssin muut ryhmät siirretään erilleen muista. Näin ne ovat edelleen nähtävillä, mutta eivät tiellä.
- jo käytyjen kurssien piilottaminen
- mahdollisuus piilottaa tietyt kurssit
- mahdollisuus piilottaa saman aineen kaikki kurssit
- mahdollisuus piilottaa muut, kuin pakolliset kurssit tietystä aineesta
## Asentaminen
Kurssivalinta-avustin on kirjoitettu Java-ohjelmointikielellä, joten tarvitset koneellesi Java Running Environment (JRE) -kirjaston voidaksesi käyttää sitä. JRE:n voit ladata ilmaiseksi osoitteesta https://java.com/en/download/.

Kun olet asentanut JRE:n, Kurssivalinta-avustimen asentaminen omalle koneelle onnistuu seuraavasti:
- Klikkaa sivun yläosassa linkkiä "Kurssivalinta-avustin.jar".
- Avautuvalla sivulla paina nappia "Download", joka sijaitsee sivun oikeassa laidassa.

Nyt ohjelma latautuu koneellesi JAR-tiedostona, joka käynnistyy tuplaklikkaamalla tavalliseen tapaan.
## Käyttö
### Asetusten asettaminen
Kun käynnistät Kurssivalinta-avustimen, ensimmäisenä näkyviin tulee Asetukset-kaavake, johon pääset täyttämään ainevalintasi (matematiikan ja ruotsin oppimäärät, opiskelemasi vieraat kielet, jne.). Valintasi vaikuttavat siihen, mitä kursseja sinulle näytetään: jos esimerkiksi valitset pitkän matematiikan, et näe ollenkaan lyhyen matematiikan kursseja. Kaavakkeen lopussa on kolme kenttää joihin voit lisätä kurssien ja aineiden koodeja: yksi kursseille, joita et halua nähdä, toinen aineille, joiden kursseja et halua nähdä ollenkaan, ja kolmas aineille, joista haluat nähdä vain pakolliset kurssit.

Piilotettavien aineiden listalla on valmiiksi merkkijonoja. Niistä ei tarvitse välittää, vaan ne ovat seurausta aiemmin tekemistäsi valinnoista. Kurssit, joiden koodit alkavat v-kirjaimella, saa näppärästi piilotettua lisäämällä merkkijonon "v" piilotettaviin aineisiin. Muiden kuin v:llä alkavien kurssien piilottamiseen ei valitettavasti ole yhtä helppoa tapaa.

Asetuksia ei tarvitse erikseen tallentaa, ja voit palata myöhemmin muokkaamaan niitä.
### Kurssitarjottimen lataaminen Wilmasta
Nyt voit siirtyä Kurssitarjotin-välilehdelle. Syötä ensiksi näkymään Wilma-palvelimesi osoite (esim. https://helsinki.inschool.fi) ja klikkaa "Seuraava". Nyt näytölle ilmestyy selainnäkymä, jossa pääset kirjautumaan Wilmaan. Näkymän avautuminen kestää hetken aikaa, ole kärsivällinen. Kun olet kirjautunut Wilmaan, ja etusivu on latautunut, paina "Lataa periodit". Wilma-tunnuksiasi ei tallenneta mihinkään.

Seuraavaksi näytölle ilmestyy kaavake, josta pääset valitsemaan ne periodit, jotka haluat ladattavan. Kun olet saanut valinnat mieleisiksesi, klikkaa "Lataa"-painiketta, joka löytyy kaavakkeen alalaidasta, ja kurssitarjottimet latautuvat tietokoneellesi. Lataus lähtee käyntiin, vaikka aluksi mitään ei näytä tapahtuvan. Älä klikkaa nappia toista kertaa.
### Kurssien valitseminen
Kurssivalintanäkymä on päällisin puolin samanlainen, kuin Wilmassa. Siinä ei kuitenkaan näy kursseja, jotka olet jo käynyt tai jotka olet valinnut piilotettaviksi. Kurssit valitaan klikkaamalla niitä, jolloin kaikki saman kurssin muut ryhmät hyppäävät oikealle erilleen muista. Tällä tavoin voit keskittyä kursseihin, joita et ole vielä valinnut. Näin voit huomata esimerkiksi, että toisen jakson neljännessä palkissa on vain yksi kurssi, jonka aiot käydä. Muualta valitut kurssit ovat kuitenkin näkyvillä siltä varalta, että satut tarvitsemaan niitä.
## Lisenssi- ja tekijänoikeustiedot
Kurssivalinta-avustin on lisensoitu Free Software Foundationin julkaiseman GNU General Public Licensen versiolla 3 tai myöhemmillä versioilla. Pähkinänkuoressa tämä tarkoittaa seuraavaa:
- Saat ladata ohjelman koneellesi ja käyttää sitä ilman minkäänlaisia rajoituksia.
- Saat muokata ja levittää ohjelmaa tietyillä ehdoilla.
- OHJELMALLA EI OLE MINKÄÄNLAISTA TAKUUTA. KÄYTÄT OHJELMAA OMALLA VASTUULLASI.

Lisenssin (englanninkielinen) teksti löytyy tiedostosta [LICENSE.txt](LICENSE.txt).
## Tulevaisuus
Kurssivalinta-avustimeen on suunnitteilla uusia ominaisuuksia, jotka toivon mukaan lisäävät sen käyttökelpoisuutta ja helppokäyttöisyyttä. Suunnitteilla ovat:
- erilliset Ladataan-näkymät kurssitarjottimen latausvälilehteen
- mahdollisuus hakea ryhmiä ryhmäkoodeilla ja niiden osilla
- mahdollisuus piilottaa ryhmä, aine tai aineen ei-pakolliset kurssit klikkaamalla ryhmänappia hiiren oikealla näppäimellä
- kurssin tietojen esittäminen
- ryhmän tietojen (esim. opettajan) esittäminen
- täysien ja lukittujen ryhmien merkitseminen ja mahdollisuus piilottaa ne
- paraikaa käytävien kurssien piilottaminen
- mahdollisesti ilmenevien bugien korjaaminen

Uusia ominaisuuksia tullaan toteuttamaan sen mukaan, miten kirjoittajalla riittää aikaa, kiinnostusta ja jaksamista.

Osa Suomen kunnista luopuu Wilmasta vuosina 2023-24. Kurssivalinta-avustimeen saatetaan toteuttaa yhteensopivuus korvaavan ohjelmiston kanssa, mikäli se osoittautuu mielekkääksi ja mahdolliseksi.
