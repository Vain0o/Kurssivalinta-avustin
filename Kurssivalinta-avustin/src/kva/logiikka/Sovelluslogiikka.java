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
package kva.logiikka;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import kva.logiikka.lataus.KurssitarjottimenLataaja;
import kva.logiikka.lataus.LuotavaRyhma;

/**Ylin säiliöluokka Kurssivalinta-avustimen sovelluslogiikalle, sisältää {@code Kurssitarjottimen}.
 * <p>
 * {@link kva.logiikka.Kurssitarjotin} luodaan komennoilla {@link #lataaPeriodienNimet(java.lang.String, java.util.function.Consumer, java.util.function.Consumer)} 
 * ja {@link #lataaKurssitarjotin(java.util.Collection, java.util.function.Consumer, java.util.function.Consumer)}. 
 * Lataamiseen käytetään konstruktoriparametrina annettua {@link kva.logiikka.lataus.KurssitarjottimenLataaja}a.
 *
 * @author Väinö Viinikka
 */
public class Sovelluslogiikka {
    
    private final KurssitarjottimenLataaja lataaja;
    private List<PeriodinTunniste> periodinTunnisteet;
    private Kurssitarjotin tarjotin;
    private final ReadOnlyStringWrapper viesti;
    private LatauksenTila tila;
    
    /**Luo uuden {@code Sovelluslogiikan}.
     * 
     * @param lataaja {@code KurssitarjottimenLataaja}, joka hakee {@code Kurssitarjottimen} 
     *        luomiseen tarvittavat tiedot eri käskyllä
     */
    public Sovelluslogiikka(KurssitarjottimenLataaja lataaja) {
        this.lataaja = lataaja;
        this.viesti = new ReadOnlyStringWrapper();
    }
    
    /**Palauttaa {@code Sovelluslogiikan} sisältämän {@code Kurssitarjottimen}.
     * 
     * @return {@code Sovelluslogiikan Kurssitarjotin}, jos {@link #getTila()} on 
     *         {@code KURSSITARJOTIN_LADATTU}. Muutoin {@code null}.
     */
    public Kurssitarjotin getTarjotin() {
        return tarjotin;
    }
    
    /**Lataa mahdollisten periodien nimet.
     * <p>
     * Metodia tulee kutsua JavaFX:n sovellussäikeessä. Metodi lähettää nimien lataamisen 
     * suoritettavaksi taustasäikeessä, minkä jälkeen ladatut nimet tai mahdollisesti 
     * syntyvä poikkeus lähetetään takaisin JavaFX:n sovellussäikeelle.
     * <p>
     * Sovellussäikeelle lähetettävä  lista on {@code KurssitarjottimenLataajan} toteutuksesta 
     * riippuen toivon mukaan järjestetty: saman oppilaitoksen periodit ovat peräkkäin 
     * aikajärjestyksessä.
     * 
     * @param URL sen nettisivun, tiedoston tm. osoite, josta tiedot haetaan. {@code KurssitarjottimenLataaja} 
     *        pitää osoitteen muistissa, jotta periodien varsinaiset tiedot voidaan 
     *        hakea myöhemmin samasta paikasta.
     * @param tuloksenKasittely Kun periodien nimet on selvitetty, ne lähetetään JavaFX:n 
     *        sovellussäikeelle kutsumalla metodia {@code accept()}.
     * @param virheenKasittely Jos nimien haku kaatuu poikkeukseen, poikkeus lähetetään 
     *        JavaFX:n sovellussäikeelle kutsumalla metodia {@code accept()}.
     * @throws java.lang.IllegalStateException jos {@link #getTila()} on {@code LADATAAN_PERIODIEN_NIMIA} 
     *         tai {@code LADATAAN_KURSSITARJOTINTA}
     * @throws java.lang.NullPointerException jos jokin parametreistä on {@code null}
     */
    public void lataaPeriodienNimet(String URL, Consumer<List<PeriodinTunniste>> tuloksenKasittely, Consumer<Throwable> virheenKasittely) {
        Objects.requireNonNull(URL);
        Objects.requireNonNull(tuloksenKasittely);
        Objects.requireNonNull(virheenKasittely);
        
        if(tila == LatauksenTila.LADATAAN_PERIODIEN_NIMIA || tila == LatauksenTila.LADATAAN_KURSSITARJOTINTA) {
            throw new IllegalStateException("Periodien nimiä ei voi ladata, kun lataajalla on taustaprosessi käynnissä.");
        }
        tila = LatauksenTila.LADATAAN_PERIODIEN_NIMIA;
        
        Task<List<PeriodinTunniste>> tehtava = new Task<List<PeriodinTunniste>>() {
            
            @Override
            protected List<PeriodinTunniste> call() throws Exception {
                updateMessage("Ladataan periodien nimiä.");
                lataaja.muodostaYhteys(URL);
                if(super.isCancelled()) {
                    return null;
                }
                return lataaja.haePeriodienNimet();
            }
        };
        
        viesti.bind(tehtava.messageProperty());
        tehtava.setOnSucceeded((ev) -> {
            tila = LatauksenTila.PERIODIEN_NIMET_LADATTU;
            periodinTunnisteet = (List<PeriodinTunniste>) ev.getSource().getValue();
            viesti.unbind();
            viesti.setValue("");
            tuloksenKasittely.accept(periodinTunnisteet);
        });
        tehtava.setOnFailed((ev) -> {
            tila = LatauksenTila.LUOTU;
            virheenKasittely.accept(ev.getSource().getException());
        });
        
        Thread th = new Thread(tehtava);
        th.setDaemon(true);
        th.start();
    }
    
    /**Lataa {@code Kurssitarjottimen}, joka sisältää saatavilla olevat ryhmät ja 
     * moduulit.
     * <p>
     * Metodia tulee kutsua JavaFX:n sovellussäikeessä. Metodi antaa kurssitarjottimen 
     * lataamisen taustasäikeen tehtäväksi, minkä jälkeen kurssitarjotin tai mahdollisesti 
     * syntyvä poikkeus lähetetään takaisin JavaFX:n sovellussäikeelle.
     *
     * @param valittavat mukaan otettavien periodien tunnisteet, järjestyksellä ei 
     *        ole väliä
     * @param tuloksenKasittely Kun kurssitarjotin on ladattu, se lähetetään JavaFX:n 
     *        sovellussäikeelle metodilla {@code accept()}.
     * @param virheenKasittely Jos kurssitarjottimen lataaminen kaatuu poikkeukseen, 
     *        poikkeus lähetetään JavaFX:n sovellussäikeelle metodilla {@code accept()}.
     * @throws java.lang.IllegalArgumentException jos {@code valittavat} sisältää 
     *         {@code PeriodinTunnisteita}, joita ei löydy {@link #getPeriodienTunnisteet()}-listalta
     * @throws java.lang.IllegalStateException jos {@link #getTila()} on {@code LUOTU}, 
     *         {@code LADATAAN_PERIODIEN_NIMIA} tai {@code LADATAAN_KURSSITARJOTINTA}
     * @throws java.lang.NullPointerException jos jokin parametreistä on {@code null}
     */
    public final void lataaKurssitarjotin(Collection<PeriodinTunniste> valittavat, Consumer<Kurssitarjotin> tuloksenKasittely, 
            Consumer<Throwable> virheenKasittely) {
        Objects.requireNonNull(valittavat);
        Objects.requireNonNull(tuloksenKasittely);
        Objects.requireNonNull(virheenKasittely);
        
        if(tila == LatauksenTila.LUOTU || tila == LatauksenTila.LADATAAN_PERIODIEN_NIMIA) {
            throw new IllegalStateException("Kurssitarjotinta ei voi ladata, kun periodien nimiä ei ole ladattu.");
        } else if(tila == LatauksenTila.LADATAAN_KURSSITARJOTINTA) {
            throw new IllegalStateException("Kurssitarjotinten latausta ei voi aloittaa, kun se on jo käynnissä.");
        }
        
        HashSet<PeriodinTunniste> mukaanOtettavat = new HashSet<>(valittavat);
        List<PeriodinTunniste> tunnisteet = getPeriodienTunnisteet();
        Iterator<PeriodinTunniste> lapikaynti = tunnisteet.iterator();
        while(lapikaynti.hasNext()) {
            if(!mukaanOtettavat.remove(lapikaynti.next())) {
                lapikaynti.remove();
            }
        }
        if(!mukaanOtettavat.isEmpty()) {
            StringBuilder poikkeusViesti = new StringBuilder("Yksi tai useampi tuntematon PeriodinTunniste:");
            for(PeriodinTunniste ylijaama : mukaanOtettavat) {
                poikkeusViesti = poikkeusViesti.append("\n").append(ylijaama.toString());
            }
            throw new IllegalArgumentException(poikkeusViesti.toString());
        }
        
        LatauksenTila vanhaTila = tila;
        tila = LatauksenTila.LADATAAN_KURSSITARJOTINTA;
        
        Task<Kurssitarjotin> tehtava = new Task<Kurssitarjotin>() {
            
            @Override
            protected Kurssitarjotin call() throws Exception {
                updateMessage("Valmistellaan kurssitarjottimen latausta.");
                int periodienMaara = tunnisteet.size();
                lataaja.valmisteleLataus(tunnisteet);
                if(isCancelled()) {
                   return null; 
                }
                
                HashMap<String, LuotavaRyhma> ryhmat = new HashMap<>();
                HashMap<String, Moduuli> moduulit = new HashMap<>();
                
                PalkinTunniste tarkistettava = null;
                int vuoronumero = 0;
                while(lataaja.onSeuraavaaRyhmaa()) {
                    String koodi = lataaja.seuraavanRyhmanKoodi();
                    PalkinTunniste sijainti = lataaja.getNykyinenSijainti();
                    if(!sijainti.equals(tarkistettava)) {
                        vuoronumero++;
                        updateMessage("Ladataan kurssitarjottimia " + vuoronumero + "/" + periodienMaara + ".");
                        tarkistettava = sijainti;
                    }
                    
                    if(!ryhmat.containsKey(koodi)) {
                        LuotavaRyhma luotava = lataaja.haeRyhmanTiedot(koodi);
                        ryhmat.put(koodi, luotava);
                        String kurssikoodi = luotava.getKurssikoodi();
                        if(!moduulit.containsKey(kurssikoodi)) {
                            moduulit.put(kurssikoodi, lataaja.haeModuulinTiedot(kurssikoodi));
                        }
                    }
                    
                    ryhmat.get(koodi).lisaaSijainti(sijainti);
                    
                    if(isCancelled()) {
                        return null;
                    }
                }
                
                return new Kurssitarjotin(ryhmat.values(), moduulit.values(), tunnisteet);
            }
        };
        
        viesti.bind(tehtava.messageProperty());
        tehtava.setOnSucceeded((ev) -> {
            tila = LatauksenTila.KURSSITARJOTIN_LADATTU;
            tarjotin = (Kurssitarjotin) ev.getSource().getValue();
            viesti.unbind();
            viesti.setValue("");
            tuloksenKasittely.accept(tarjotin);
        });
        tehtava.setOnFailed((ev) -> {
            tila = vanhaTila;
            virheenKasittely.accept(ev.getSource().getException());
        });
        
        Thread th = new Thread(tehtava);
        th.setDaemon(true);
        th.start();
    }
    
    /**Palauttaa listan periodeista, jotka on mahdollista ladata osaksi kurssitarjotinta.
     * 
     * @return lista mahdollisten periodien tunnisteista, tai tyhjä lista, jos {@link #getTila()} 
     *         on {@code LUOTU} tai {@code LADATAAN_PERIODIEN_NIMIÄ}.
     */
    public List<PeriodinTunniste> getPeriodienTunnisteet() {
        if (getTila() == LatauksenTila.LUOTU || getTila() == LatauksenTila.LADATAAN_PERIODIEN_NIMIA) {
            return new ArrayList<>();
        }
        return new ArrayList<>(periodinTunnisteet);
    }
    
    /**Palauttaa viestin, joka kertoo, mitä {@code Sovelluslogiikka} tekee kullakin 
     * hetkellä.
     * 
     * @return lataajan toimintaa kuvaava poikkeusViesti, joka voidaan näyttää käyttöliittymässä.
     */
    public String getViesti() {
        return viesti.get();
    }

    /**Palauttaa {@code Property}-muotoisen viestin, joka kertoo, mitä {@code Sovelluslogiikka} 
     * tekee kullakin hetkellä.
     * <p>
     * {@code Propertya} arvoa ei voi muuttaa.
     * 
     * @return viestiä kuvaava {@code Property}, jota voi tarkkailla JavaFX:n sovellussäikeessä.
     * @see #getViesti() 
     */
    public ReadOnlyStringProperty viestiProperty() {
        return viesti.getReadOnlyProperty();
    }

    /**Kertoo, missä toimintansa vaiheessa {@code Sovelluslogiikka} on.
     * 
     * @return {@code Sovelluslogiikan LatauksenTila}
     * @see kva.logiikka.Sovelluslogiikka.LatauksenTila
     */
    public LatauksenTila getTila() {
        return tila;
    }
    
    /**Esitys {@code Sovelluslogiikan} kulloisestakin toimintavaiheesta.
     * <p>
     * {@link kva.logiikka.Sovelluslogiikka} on aina aluksi tilassa {@code LUOTU}. 
     * Kun käyttäjä kutsuu metodia {@link #lataaPeriodienNimet(java.lang.String, java.util.function.Consumer, java.util.function.Consumer)}, 
     * tilaksi vaihtuu {@code LADATAAN_PERIODIEN_NIMIA}. Kun lataus taustasäikeessä 
     * saadaan valmiiksi, tilaksi vaihdetaan {@code PERIODIEN_NIMET_LADATTU}, paitsi 
     * jos lataus kaatuu poikkeukseen, jolloin lataaja palaa tilaan {@code LUOTU}. 
     * Kun käyttäjä periodien nimien lataamisen jälkeen kutsuu metodia {@link #lataaKurssitarjotin(java.util.Collection, java.util.function.Consumer, java.util.function.Consumer)}, 
     * lataaja siirtyy tilaan {@code LADATAAN_KURSSITARJOTINTA}. Kun lataus on valmis, 
     * lataaja siirtyy tilaan {@code KURSSITARJOTIN_LADATTU}, mutta jos se kaatuu 
     * poikkeukseen, lataaja palaa tilaan {@code PERIODIEN_NIMET_LADATTU}.
     * 
     * @author Väinö Viinikka
     * @see kva.logiikka.lataus.KurssitarjottimenLataaja
     */
    public enum LatauksenTila {
        
        /**Kertoo, että {@code KurssitarjottimenLataajalla} ei ole vielä tehty mitään 
         * tai että periodien nimien haku on epäonnistunut.
         * 
         */
        LUOTU, 
        
        /**Kertoo, että periodien nimien lataus on parhaillaan käynnissä taustasäikeessä.
         * 
         */
        LADATAAN_PERIODIEN_NIMIA, 
        
        /**Kertoo, että periodien nimet on ladattu ja {@code KurssitarjottimenLataaja} 
         * on valmis lataamaan itse kurssitarjottimen.
         * <p>
         * LatauksenTila on tässä arvossa myös, mikäli kurssitarjottimen latausta on yritetty, 
         * mutta siinä ei ole onnistuttu.
         * 
         */
        PERIODIEN_NIMET_LADATTU, 
        
        /**Kertoo, että kurssitarjottimen koostaminen on parhaillaan käynnissä taustasäikeessä.
         * 
         */
        LADATAAN_KURSSITARJOTINTA, 
        
        /**Kertoo, että kurssitarjottimen lataaminen on onnistunut.
         * 
         */
        KURSSITARJOTIN_LADATTU;
    }
}
