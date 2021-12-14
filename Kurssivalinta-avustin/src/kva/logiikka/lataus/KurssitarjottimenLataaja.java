/* Kurssivalinta-avustin – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
 * Copyright (C) 2021 Väinö viinikka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kva.logiikka.lataus;

import java.util.List;
import kva.logiikka.Moduuli;
import kva.logiikka.RyhmanSijainti;

/**Määrittelee komennot, joilla kurssitarjottimen tiedot haetaan.
 * <p>
 * {@link kva.logiikka.Sovelluslogiikka} toteuttaa ne osat ku kurssitarjottimen luomisesta, 
 * jotka ovat samat kaikille: se ottaa vastaan käskyt, kokoaa tiedot periodien nimistä, 
 * kursseista ja ryhmistä, sekä siirtää ne käyttäjälle oikeassa muodossa. Käytännön 
 * toteutus siitä, kuinka ja mistä moduulien ja ryhmien tiedot haetaan, tehdään tämän 
 * rajapinnan toteuttavissa luokissa. Näin lataajia voi vaivattomasti kirjoittaa useita, 
 * esimerkiksi tekstitiedoston perusteella toimivan testilataajan ja oikean internetiä 
 * hyödyntävän lataajan.
 * <p>
 * Aluksi mahdollisten periodien tai periodinpuolikkaiden nimet haetaan rajapinnan 
 * toteuttavilta luokilta kutsumalla {@link #muodostaYhteys(java.lang.String)}- ja 
 * {@link #haePeriodienNimet()}-metodeja, minkä jälkeen kurssitarjottimen varsinainen 
 * lataus alustetaan metodilla {@link #valmisteleLataus(java.util.Collection)}. Samalla 
 * kerrotaan, mistä periodeista kursseja halutaan hakea. Tämän jälkeen kurssitarjotinta 
 * käydään läpi {@link #onSeuraavaaRyhmaa()}- ja {@link #seuraavanRyhmanKoodi()}-metodeilla, 
 * ja ryhmistä ja moduuleista pyydetään tarvittaessa lisätietoa metodeilla {@link #haeRyhmanTiedot(java.lang.String)} 
 * ja {@link #haeModuulinTiedot(java.lang.String) }. Tällä tavoin huolehditaan, että 
 * useassa periodissa esiintyvät ryhmät merkitään samaan {@code Ryhmaan} ja kaikilla 
 * saman moduulin ryhmillä on sama {@code Moduuli}.
 * <p>
 * {@code Sovelluslogiikka} ottaa komentonsa vastaan JavaFX:n sovellussäikeessä, mutta 
 * siirtää toteutuksen taustasäikeelle, jossa se kutsuu tämän rajapinnan metodeja.
 * <p>
 * Määriteltyjen metodien tulisi heittää {@code IllegalStateException}, jos niitä 
 * kutsutaan väärään aikaan. Tätä ei kuitenkaan ole aivan välttämätöntä  toteuttaa, 
 * jos luottaa siihen, ettei metodeja kutsuta muualta, kuin {@code Sovelluslogiikasta}. 
 * Määritellyissä metodeissa tapahtuvat poikkeukset voi heittää ylöspäin {@code KurssitarjottimenLataajan} 
 * huolehdittavaksi.
 *
 * @author Väinö Viinikka
 * @see kva.logiikka.Sovelluslogiikka
 * @see kva.logiikka.Kurssitarjotin
 * @see kva.logiikka.Ryhma
 */
public interface KurssitarjottimenLataaja {
    
    /**Muodostaa yhteyden paikkaan, josta kurssitarjottimen tiedot tuodaan ja valmistelee 
     * periodien nimien haun.
     * <p>
     * Paikka voi toteutuksesta riippuen olla internetsivu, tekstitiedosto tai jotakin 
     * muuta. Metodin pitäisi suorittaa kaikki tarvittava, jonka jälkeen metodin 
     * {@code haePerioidienNimet()} kutsuminen on mahdollista.
     * <p>
     * Toteutuksen tulee muistaa parametrina annettu merkkijono, jos sitä tarvitaan 
     * myöhemmin. On toteutuksen vastuulla määritellä, millainen merkkijonon pitää 
     * olla ja miten sitä tulkitaan.
     * <p>
     * Jos kurssitarjottimen lataus epäonnistuu, sitä voidaan yrittää uudelleen kutsumalla 
     * metodia samalla oliolla uudelleen. Edellisen yrityksen tiedot eivät saa vaikuttaa 
     * uuden yrityksen tuloksiin.
     * <p>
     * {@code Sovelluslogiikka} kutsuu tätä metodia taustasäikeessä. Metodia ei tule 
     * kutsua muualta.
     * 
     * @param URL sen tiedoston, nettisivun tms. sijainti, josta kurssitarjotin ladataan
     * @throws java.lang.Exception jos yhteyden muodostaminen epäonnistuu jollakin 
     *         tavalla
     */
    public abstract void muodostaYhteys(String URL) throws Exception;
    
    /**Palauttaa listan niiden periodien tai perioidinpuolikkaiden nimistä, joiden 
     * valinta on mahdollista.
     * <p>
     * {@link kva.logiikka.lataus.PeriodinTunniste}et tulee palauttaa järjestettyinä: 
     * saman oppilaitoksen periodien on oltava peräkkäin aikajärjestyksessä.
     * <p>
     * {@code Sovelluslogiikka} kutsuu tätä metodia taustasäikeessä. Metodia ei tule 
     * kutsua muualta.
     * 
     * @return lista periodien nimistä
     * @throws java.lang.IllegalStateException jos metodia kutsutaan, ennen kuin metodia
     *         {@code muodostaYhteys()} on kutsuttu
     * @throws java.lang.Exception jos periodien nimien haku epäonnistuu jollakin 
     *         tavalla
     */
    public abstract List<PeriodinTunniste> haePeriodienNimet() throws Exception;
    
    /**Tekee tarvittavat toimet, joiden jälkeen kurssitarjotinten tiedot voidaan ladata.
     * <p>
     * Parametrina annetaan lista periodeista, joiden sisältö tulee ladata. {@code Sovelluslogiikka} 
     * varmistaa automaattisesti, että listalla on vain sellaisia merkkijonoja, jotka 
     * sisältyvät {@link #haePeriodienNimet()}-metodin palauttamaan listaan. Mukana 
     * olevat {@code PeriodinTunnisteet} ovat samassa järjestyksessä, kuin missä ne 
     * olivat tällä listalla. Toteutuksen tulee muistaa lista ryhmien lataamisen ajan.
     * <p>
     * {@code Sovelluslogiikka} kutsuu tätä metodia taustasäikeessä. Metodia ei tule 
     * kutsua muualta.
     * 
     * @param periodit niiden periodien nimet, joissa sijaitsevat kurssit halutaan 
     *                 osaksi {@code Kurssitarjotinta}
     * @throws java.lang.IllegalStateException jos metodeja {@link #muodostaYhteys(java.lang.String)} 
     *         tai {@link #haePeriodienNimet()} ei ole kutsuttu
     * @throws java.lang.Exception jos latauksen valmistelu epäonnistuu jollakin muulla 
     *         tavalla
     */
    public abstract void valmisteleLataus(List<PeriodinTunniste> periodit) throws Exception;
    
    /**Kertoo kurssien latauksen aikana, onko ladattavia ryhmiä jäljellä.
     * <p>
     * {@code Solvelluslogiikka} kutsuu tätä metodia taustasäikeessä. Metodia ei tule 
     * kutsua muualta.
     * 
     * @return {@code false}, jos kaikki kurssit on käyty läpi ja kurssitarjotin on 
     *         siten valmis. Muutoin {@code true}.
     * @throws java.lang.IllegalStateException jos metodia {@link #valmisteleLataus(java.util.Collection)} 
     *         ei ole kutsuttu
     * @throws java.lang.Exception jos tarkistuksessa tapahtuu virhe
     */
    public abstract boolean onSeuraavaaRyhmaa() throws Exception;
    
    /**Palauttaa kurssien latauksen aikana seuraavan läpikäytävän ryhmän ryhmäkoodin.
     * <p>
     * {@code KurssitarjottimenLataaja} kutsuu tätä metodia taustasäikeessä. Metodia 
     * ei tule kutsua muualta.
     * 
     * @return Kurssitarjottimessa seuraavana oleva ryhmäkoodi
     * @throws java.lang.IllegalStateException jos metodia {@link #valmisteleLataus(java.util.Collection)} 
     *         ei ole kutsuttu
     * @throws java.lang.Exception jos ryhmäkoodin tuomisessa tapahtuu virhe
     */
    public abstract String seuraavanRyhmanKoodi() throws Exception;
    
    /**Kertoo kurssien latauksen aikana, mistä jaksosta ja palkista kursseja haetaan 
     * tällä hetkellä.
     * <p>
     * Metodi palauttaa sen ryhmän sijainnin, jonka koodin metodi {@link #seuraavanRyhmanKoodi()}
     * palautti viimeksi. {@code Sovelluslogiikka} kutsuu tätä metodia taustasäikeessä. 
     * Metodia ei tule kutsua muualta.
     * 
     * @return tällä hetkellä läpikäynnin kohteena olevaa jaksoa ja palkkia kuvaileva 
     *         {@link kva.logiikka.RyhmanSijainti}
     * @throws java.lang.IllegalStateException jos metodia {@link #valmisteleLataus(java.util.Collection)} 
     *         ei ole kutsuttu, tai jos metodia {@link #seuraavanRyhmanKoodi()} ei 
     *         ole kutsuttu kertaakaan sen jälkeen
     * @throws java.lang.Exception jos sijaintia lukiessa tapahtuu virhe
     */
    public abstract RyhmanSijainti getNykyinenSijainti() throws Exception;
    
    /**Palauttaa {@code LuotavanRyhman}, joka sisältää {@code seuraavanRyhmanKoodi()}-metodin 
     * viimeksi palauttaman ryhmän tiedot.
     * <p>
     * Kun metodia kutsutaan, se saa parametrina aina saman ryhmäkoodin, kuin minkä 
     * metodi {@link #seuraavanRyhmanKoodi()} palautti viimeksi. Palautettavalla {@link kva.logiikka.lataus.LuotavaRyhma}lla 
     * ei tulisi olla valmiiksi lisättyjä sijainteja, ja sen ryhmäkoodina tulee olla 
     * parametrinä annettava merkkijono.
     * <p>
     * {@code Sovelluslogiikka} kutsuu tätä metodia taustasäikeessä. Metodia ei tule 
     * kutsua muualta.
     * 
     * @param ryhmakoodi sen ryhmän koodi, jonka tietoja haetaan, esim. "KE05.1".
     * @return {@code LuotavaRyhma}, joka kuvaa parhaillaan läpikäytävää ryhmää
     * @throws java.lang.IllegalStateException jos metodia {@link #valmisteleLataus(java.util.Collection)} 
     *         ei ole kutsuttu, tai jos metodia {@link #seuraavanRyhmanKoodi()} ei 
     *         ole kutsuttu kertaakaan sen jälkeen
     * @throws java.lang.Exception jos ryhmän tietoja lukiessa tapahtuu virhe
     */
    public abstract LuotavaRyhma haeRyhmanTiedot(String ryhmakoodi) throws Exception;
    
    /**Palauttaa {@code Moduulin}, joka sisältää {@code seuraavanRyhmanKoodi()}-metodin 
     * viimeksi palauttaman ryhmän moduulin tiedot.
     * <p>
     * Kun metodia kutsutaan, se saa parametrina aina metodin {@link #seuraavanRyhmanKoodi()} 
     * viimeksi palauttaman ryhmäkoodin kurssia kuvaavan osan. Jos esimerkiksi ryhmäkoodi 
     * olisi "KE05.1", tätä metodia kutsuttaisiin parametrilla "KE05". Palautettavalla 
     * {@link kva.logiikka.Moduuli}lla tulee olla parametrinä annettu koodi.
     * <p>
     * {@code Sovelluslogiikka} kutsuu tätä metodia taustasäikeessä. Metodia ei tule 
     * kutsua muualta.
     * 
     * @param kurssikoodi sen moduulin koodi, jonka tietoja halutaan hakea
     * @return {@code Moduuli}, joka kuvaa parhaillaan läpikäytävän ryhmän moduulia
     * @throws java.lang.IllegalStateException jos metodia {@link #valmisteleLataus(java.util.Collection)} 
     *         ei ole kutsuttu, tai jos metodia {@link #seuraavanRyhmanKoodi()} ei 
     *         ole kutsuttu kertaakaan sen jälkeen
     * @throws java.lang.Exception jos moduulin tietoja lukiessa tapahtuu virhe
     */
    public abstract Moduuli haeModuulinTiedot(String kurssikoodi) throws Exception;
    
}
