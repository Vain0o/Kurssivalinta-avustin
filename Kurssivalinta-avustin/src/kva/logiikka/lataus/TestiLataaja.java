/* Kurssivalinta-avustin – työkalu lukiolaisille helpottamaan kurssivalintojen tekoa
 * Copyright (C) 2022 Väinö Viinikka
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import javafx.concurrent.Task;
import kva.logiikka.Moduuli;
import kva.logiikka.Moduuli.Tyyppi;
import kva.logiikka.PalkinTunniste;

/**{@code KurssitarjottimenLataaja}, joka lataa tiedot tekstitiedostosta.
 * <p>
 * Luokka hakee kurssitarjottimen tiedot tekstitiedostosta, jonka sijainti annetaan 
 * metodin {@link #lataaPeriodienTunnisteet(java.lang.Object[])} parametrina. Luokka on tarkoitettu 
 * ohjelman testaamiseen silloin, kun tietoja ei haluta tai voida hakea Wilmasta. 
 * Lopullinen Kurssivalinta-avustimen julkaisu ei sisällä viitteitä tähän luokkaan, 
 * vaan siinä käytetään toista {@code KurssitarjottimenLataajaa}, joka hakee tiedot 
 * suoraan Wilmasta.
 * <p>
 * Luokka ei tue metodin {@code lataaKurssitarjotin()} kutsumista toisen kerran, ja 
 * siten kurssitarjottimen lataamista uudestaan samalla {@code lataaPeriodienTunnisteet()}-metodin 
 * kutsulla. Tämän yrittäminen aiheuttaa {@code IllegalStateException}-poikkeuksen.
 *
 * @author Väinö Viinikka
 * @see kva.logiikka.lataus.KurssitarjottimenLataaja
 * @since Kurssivalinta-avustin 1.0
 */
public class TestiLataaja extends KurssitarjottimenLataaja {

    /**Lukee {@code URL}:n osoittamaa tiedostoa rivi kerrallaan.
     */
    private Scanner lukija;
    /**Sen tiedoston sijainti, jonka perusteella kurssitarjotin luodaan.
     */
    private String URL;
    /**Kertoo, kuinka mones tutkittavan tiedoston rivi ladattiin viimeksi,
     * alkaen nollasta.
     */
    private int rivi = 0;
    /**Sisältää viimeisimmän tiedostosta ladatun rivin.
     */
    private String viimeisinRivi;
    private HashSet<PeriodinTunniste> ladattavatPeriodit;
    private ArrayList<String> kaytavaPalkki = new ArrayList<>();
    private String nykyinenOppilaitos;
    private String nykyinenPeriodi;
    private String nykyinenPalkki;
    
    @Override
    public void lataaPeriodienTunnisteet(Object[] data) {
        Task<Void> tehtava = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                String URL = (String) data[0];
                Scanner uusi = new Scanner(new FileInputStream(URL));
                if ((!uusi.hasNext()) || (!uusi.nextLine().equals("PERIODIEN_TUNNISTEET"))) {
                    throw new LataajaPoikkeus("Tiedosto \"" + URL + "\" ei ala rivillä \"PERIODIEN_TUNNISTEET\".");
                }

                rivi = 1;
                viimeisinRivi = "PERIODIEN_TUNNISTEET";
                lukija = uusi;

                String oppilaitos = "";

                while (true) {
                    if (super.isCancelled()) {
                        return null;
                    }
                    if (!lukija.hasNext()) {
                        throw new LataajaPoikkeus("Tiedosto \"" + URL + "\" sisältää vain periodien tunnistetietoja.");
                    }
                    String syote = seuraavaRivi();
                    if (syote.equals("MODUULIEN_TIEDOT")) {
                        return null;
                    } else if (syote.startsWith("#")) {
                        oppilaitos = syote.substring(1);
                    } else {
                        lisaaPeriodinTunniste(new PeriodinTunniste(oppilaitos, syote));
                    }
                }
            }
        };

        tehtava.setOnSucceeded((ev) -> super.lahetaPeriodinTunnisteet());
        tehtava.setOnFailed((ev) -> super.lahetaVirhe(ev.getSource().getException()));
        tehtava.setOnCancelled((ev) -> super.lahetaVirhe(new LataajaPoikkeus("Lataus keskeytettiin.")));

        Thread th = new Thread(tehtava);
        th.setDaemon(true);
        th.start();
    }

    @Override
    public void lataaKurssitarjotin(List<PeriodinTunniste> periodit) {
        Task<Void> tehtava = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                if (!"MODUULIEN_TIEDOT".equals(viimeisinRivi)) {
                    throw new IllegalStateException("Latausta ei voida valmistella tässä vaiheessa.");
                }

                while (true) {
                    if(isCancelled()) {
                        return null;
                    }
                    String syote = seuraavaRivi();
                    if (syote.equals("KURSSITARJOTTIMET")) {
                        break;
                    }
                    String[] osat = syote.split("/");
                    if (osat.length != 2) {
                        throw new LataajaPoikkeus("Virheellinen moduulin kuvaus tiedoston \""
                                + URL + "\" rivillä " + rivi + ".");
                    }
                    Tyyppi tyyppi;
                    switch (osat[1]) {
                        case "pakollinen":
                            tyyppi = Tyyppi.PAKOLLINEN;
                            break;
                        case "valtakunnallinen syventava":
                            tyyppi = Tyyppi.VALTAKUNNALLINEN_SYVENTAVA;
                            break;
                        case "koulukohtainen syventava":
                            tyyppi = Tyyppi.KOULUKOHTAINEN_SYVENTAVA;
                            break;
                        case "koulukohtainen soveltava":
                            tyyppi = Tyyppi.SOVELTAVA;
                            break;
                        default:
                            throw new LataajaPoikkeus("Virheellinen moduulin kuvaus tiedoston \""
                                    + URL + "\" rivillä " + rivi + ".");
                    }
                    lisaaModuuli(new Moduuli(osat[0], tyyppi));
                }
                
                ladattavatPeriodit = new HashSet<>(periodit);

                while (onSeuraavaaRyhmaa()) {
                    String koodi = seuraavanRyhmanKoodi();
                    if(!lisaaSijainti(koodi, getNykyinenSijainti())) {
                        LuotavaRyhma uusi = new LuotavaRyhma(koodi);
                        uusi.lisaaSijainti(getNykyinenSijainti());
                        lisaaRyhma(uusi);
                    }
                }
                
                return null;
            }
        };

        tehtava.setOnSucceeded((ev) -> lahetaKurssitarjotin());
        tehtava.setOnFailed((ev) -> lahetaVirhe(ev.getSource().getException()));
        tehtava.setOnCancelled((ev) -> lahetaVirhe(new LataajaPoikkeus("Kurssitarjottimen lataus keskeytettiin.")));

        Thread th = new Thread(tehtava);
        th.setDaemon(true);
        th.start();
    }
    
    /**Kertoo, onko tekstitiedostossa jäljellä ryhmiä, jotka pitäisi lisätä kurssitarjottimeen.
     * 
     * @return true, jos jäljellä on yksi tai useampi ryhmä
     * @throws LataajaPoikkeus jos tekstitiedoston syntaksi on virheellinen
     */
    private boolean onSeuraavaaRyhmaa() throws LataajaPoikkeus {
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
            throw new LataajaPoikkeus("Virheellinen palkin kuvaus tiedoston \"" 
                        + URL + "\" rivillä " + rivi + ".");
        }
        nykyinenPalkki = kaytavaPalkki.remove(0);
        
        return true;
    }

    /**Kertoo seuraavan kurssitarjottimeen lisättävän ryhmän ryhmäkoodin.
     * 
     * @return ryhmäkoodi
     * @throws LataajaPoikkeus jos tekstitiedoston syntaksi on virheellinen
     */
    private String seuraavanRyhmanKoodi() throws LataajaPoikkeus {
        if(!onSeuraavaaRyhmaa()) {
            return null;
        }
        return kaytavaPalkki.remove(0);
    }

    /**Kertoo, mitä palkkia lataaja käy parhaillaan läpi.
     * 
     * @return läpikäytävän palkin tunniste
     */
    private PalkinTunniste getNykyinenSijainti() {
        return new PalkinTunniste(nykyinenOppilaitos, nykyinenPeriodi, nykyinenPalkki);
    }

    /**Palauttaa {@code lukijan} seuraavan rivin.
     * <p>
     * Tätä metodia tulisi käyttää aina kutsun {@code lukija.nextLine()} sijasta, 
     * ellei muuhun ole painavaa syytä. Näin muuttujien {@code rivi} ja {@code viimeisinRivi} 
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
     * @return {@code true}, mikäli {@code nykyisestaOppilaitoksesta} ja {@code nykyisestaPeriodista} muodostuu {@code PeriodinTunniste}, joka
     *         kuuluu {@code ladattaviinPeriodeihin}
     */
    private boolean onHaluttuPeriodi() {
        if (nykyinenOppilaitos == null || nykyinenPeriodi == null) {
            return false;
        }
        PeriodinTunniste nykyinen = new PeriodinTunniste(nykyinenOppilaitos, nykyinenPeriodi);

        return ladattavatPeriodit.contains(nykyinen);
    }
}
