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

import kva.logiikka.PeriodinTunniste;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import kva.logiikka.Moduuli;
import kva.logiikka.Moduuli.Tyyppi;
import kva.logiikka.PalkinTunniste;

/**{@code KurssitarjottimenLataaja}, joka lataa tiedot tekstitiedostosta.
 * <p>
 * Luokka hakee kurssitarjottimen tiedot tekstitiedostosta, jonka sijainti annetaan 
 * metodin {@link #muodostaYhteys(java.lang.String)} parametrina. Luokka on tarkoitettu 
 * ohjelman testaamiseen silloin, kun tietoja ei haluta tai voida hakea Wilmasta. 
 * Lopullinen Kurssivalinta-avustimen julkaisu ei sisällä viitteitä tähän luokkaan, 
 * vaan siinä käytetään toista {@code KurssitarjottimenLataajaa}, joka hakee tiedot 
 * suoraan Wilmasta.
 * <p>
 * Luokka ei tue metodin {@code valmisteleLataus()} kutsumista toisen kerran, ja siten kurssitarjottimen 
 * lataamista uudestaan samalla {@code muodostaYhteys()}-metodin kutsulla. Tämän yrittäminen 
 * aiheuttaa {@code IllegalStateException}-poikkeuksen.
 * <p>
 * Luokka sisältää luokan {@link kva.logiikka.lataus.TestiLataaja.TestiLataajaPoikkeus}, 
 * jonka luokan metodit heittävät, mikäli annetun tiedoston lukeminen ei onnistu jostakin 
 * syystä.
 * 
 * @author Väinö Viinikka
 * @see kva.logiikka.lataus.KurssitarjottimenLataaja
 */
public class TestiLataaja implements KurssitarjottimenLataaja {
    
    /**Lukee {@code URL}:n osoittamaa tiedostoa rivi kerrallaan.*/
    private Scanner lukija;
    /**Sen tiedoston sijainti, jonka perusteella kurssitarjotin luodaan.*/
    private String URL;
    /**Kertoo, kuinka mones tutkittavan tiedoston rivi ladattiin viimeksi, alkaen 
     * nollasta.
     */
    private int rivi = 0;
    /**Sisältää viimeisimmän tiedostosta ladatun rivin.*/
    private String viimeisinRivi;
    /**Sisältää moduulit, jotka ladataan tiedostosta.*/
    private HashMap<String, Moduuli> moduulit;
    private HashSet<PeriodinTunniste> ladattavatPeriodit;
    
    private boolean latausKaynnissa = false;
    private ArrayList<String> kaytavaPalkki = new ArrayList<>();
    private String nykyinenOppilaitos;
    private String nykyinenPeriodi;
    private String nykyinenPalkki;
    

    @Override
    public void muodostaYhteys(String URL) throws Exception {
        Scanner uusi = new Scanner(new FileInputStream(URL));
        if((!uusi.hasNext()) || (!uusi.nextLine().equals("PERIODIEN_TUNNISTEET"))) {
            throw new TestiLataajaPoikkeus("Tiedosto \"" + URL + "\" ei ala rivillä \"PERIODIEN_TUNNISTEET\".");
        }
        
        rivi = 1;
        viimeisinRivi = "PERIODIEN_TUNNISTEET";
        lukija = uusi;
        this.URL = URL;
    }

    @Override
    public List<PeriodinTunniste> haePeriodienNimet() throws Exception {
        if(!"PERIODIEN_TUNNISTEET".equals(viimeisinRivi)) {
            throw new IllegalStateException("Periodien nimiä ei voi hakea ennen yhteyden muodostamista.");
        }
        
        ArrayList<PeriodinTunniste> tunnisteet = new ArrayList<>();
        String oppilaitos = "";
        
        while(true) {
            if(!lukija.hasNext()) {
                throw new TestiLataajaPoikkeus("Tiedosto \"" + URL + "\" sisältää vain periodien tunnistetietoja.");
            }
            String syote = seuraavaRivi();
            if(syote.equals("MODUULIEN_TIEDOT")) {
                break;
            } else if(syote.startsWith("#")) {
                oppilaitos = syote.substring(1);
            } else {
                tunnisteet.add(new PeriodinTunniste(oppilaitos, syote));
            }
        }
        
        return tunnisteet;
    }

    @Override
    public void valmisteleLataus(List<PeriodinTunniste> periodit) throws Exception {
        if(!"MODUULIEN_TIEDOT".equals(viimeisinRivi)) {
            throw new IllegalStateException("Latausta ei voida valmistella tässä vaiheessa.");
        }
        
        moduulit = new HashMap<>();
        
        while(true) {
            String syote = seuraavaRivi();
            if(syote.equals("KURSSITARJOTTIMET")) {
                break;
            }
            String[] osat = syote.split("/");
            if(osat.length != 2) {
                throw new TestiLataajaPoikkeus("Virheellinen moduulin kuvaus tiedoston \"" 
                        + URL + "\" rivillä " + rivi + ".");
            }
            Tyyppi tyyppi;
            switch(osat[1]) {
                case "pakollinen": tyyppi = Tyyppi.PAKOLLINEN;
                    break;
                case "valtakunnallinen syventava": tyyppi = Tyyppi.VALTAKUNNALLINEN_SYVENTAVA;
                    break;
                case "koulukohtainen syventava": tyyppi = Tyyppi.KOULUKOHTAINEN_SYVENTAVA;
                    break;
                case "koulukohtainen soveltava": tyyppi = Tyyppi.SOVELTAVA;
                    break;
                default:
                    throw new TestiLataajaPoikkeus("Virheellinen moduulin kuvaus tiedoston \"" 
                            + URL + "\" rivillä " + rivi + ".");
            }
            Moduuli uusiModuuli = new Moduuli(osat[0], tyyppi);
            moduulit.put(osat[0], uusiModuuli);
        }
        
        latausKaynnissa = true;
        ladattavatPeriodit = new HashSet<>(periodit);
    }

    @Override
    public boolean onSeuraavaaRyhmaa() throws Exception {
        if(!latausKaynnissa) {
            throw new IllegalStateException("Seuraavan ryhmän olemassaoloa ei voi kysyä ennen latauksen valmistelua.");
        }
        if(!kaytavaPalkki.isEmpty()) {
            return true;
        }
        
        String syote;
        do {
            if(!lukija.hasNext()) {
                return false;
            }
            syote = seuraavaRivi();
            if(syote.startsWith("#")) {
                nykyinenOppilaitos = syote.substring(1);
                if(!lukija.hasNext()) {
                    return false;
                }
                syote = seuraavaRivi();
            }
            if(syote.startsWith("&")) {
                nykyinenPeriodi = syote.substring(1);
                if(!lukija.hasNext()) {
                    return false;
                }
                syote = seuraavaRivi();
            }
        } while(!onHaluttuPeriodi());
        
        kaytavaPalkki.addAll(Arrays.asList(syote.split("/")));
        if(kaytavaPalkki.size() < 2) {
            throw new TestiLataajaPoikkeus("Virheellinen palkin kuvaus tiedoston \"" 
                        + URL + "\" rivillä " + rivi + ".");
        }
        nykyinenPalkki = kaytavaPalkki.remove(0);
        
        return true;
    }

    @Override
    public String seuraavanRyhmanKoodi() throws Exception {
        if(!latausKaynnissa) {
            throw new IllegalStateException("Seuraavan ryhmän koodia ei voi kysyä ennen latauksen valmistelua.");
        }
        if(!onSeuraavaaRyhmaa()) {
            return null;
        }
        return kaytavaPalkki.remove(0);
    }

    @Override
    public PalkinTunniste getNykyinenSijainti() throws Exception {
        return new PalkinTunniste(nykyinenOppilaitos, nykyinenPeriodi, nykyinenPalkki);
    }

    @Override
    public LuotavaRyhma haeRyhmanTiedot(String ryhmakoodi) throws Exception {
        return new LuotavaRyhma(ryhmakoodi);
    }

    @Override
    public Moduuli haeModuulinTiedot(String kurssikoodi) throws Exception {
        Moduuli palautus = moduulit.get(kurssikoodi);
        if(palautus == null) {
            throw new TestiLataajaPoikkeus("Moduulista" + kurssikoodi + " ei löydy tietoa.");
        }
        return palautus;
    }
    
    /**Palauttaa {@code lukijan} seuraavan rivin
     * <p>
     * Tätä metodia tulisi käyttää aina kutsun {@code lukija.nextLine()} sijasta, ellei 
     * muuhun ole painavaa syytä. Näin muuttujien {@code rivi} ja {@code viimeisinRivi} 
     * arvot pysyvät oikeina.
     * 
     * @return kutsun {@code lukija.nextLine()} palauttama merkkijono
     */
    private String seuraavaRivi() {
        rivi++;
        viimeisinRivi = lukija.nextLine();
        return viimeisinRivi;
    }
    
    /**Kertoo, onko tarkastelussa tällä hetkellä periodi, jonka kurssit tulisi ladata.
     * <p>
     * Metodi palauttaa arvon {@code true}, mikäli {@code ladattavatPeriodit} sisältää 
     * {@code PeriodinTunnisteen}, jonka oppilaitos on {@code nykyinenOppilaitos} 
     * ja periodi {@code nykyinenPeriodi}. Jos joko {@code nykyinenOppilaitos} tai 
     * {@code nykyinenPeriodi} on {@code null}, palautetaan {@code false}.
     * 
     * @return {@code true}, mikäli {@code nykyisestaOppilaitoksesta} ja {@code nykyisestaPeriodista} 
     *         muodostuu {@code PeriodinTunniste}, joka kuuluu {@code ladattaviinPeriodeihin}
     */
    private boolean onHaluttuPeriodi() {
        if(nykyinenOppilaitos == null || nykyinenPeriodi == null) {
            return false;
        }
        PeriodinTunniste nykyinen = new PeriodinTunniste(nykyinenOppilaitos, nykyinenPeriodi);
        
        return ladattavatPeriodit.contains(nykyinen);
    }
    
    /**Poikkeus, joka syntyy, kun kurssitarjottimen latauksessa {@code TestiLataajalla} 
     * tapahtuu virhe.
     * 
     * @author Väinö Viinikka
     * @see kva.logiikka.lataus.TestiLataaja
     */
    public class TestiLataajaPoikkeus extends IOException {
        
        /**Luo uuden {@code TestiLataajaPoikkeuksen}.
         * 
         * @param viesti poikkeukseen liittyvä viesti, joka voidaan palauttaa metodilla 
         *        {@link java.lang.Throwable#getMessage()}
         */
        public TestiLataajaPoikkeus(String viesti) {
            super(viesti);
        }
    }
}
