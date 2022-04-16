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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import kva.logiikka.PeriodinTunniste;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import kva.logiikka.Kurssitarjotin;
import kva.logiikka.Moduuli;
import kva.logiikka.PalkinTunniste;

/**Yläluokka luokille, jotka lataavat kurssitarjottimen koostamiseen tarvittavia tietoja.
 * <p>
 * Luokan toteutus mahdollistaa sen, että alaluokat siirtävät lataamisen suoritettavaksi 
 * taustasäikeessä. Alaluokat antavat lataamansa tiedot {@code KurssitarjottimenLataajalle}, 
 * minkä jälkeen ne käskevät tulosten tai virheen lähettämisen tietojen kyselijälle 
 * luokan tarjoamilla metodeilla.
 * 
 * @author Väinö Viinikka
 * @see kva.logiikka.Sovelluslogiikka
 * @see kva.logiikka.Kurssitarjotin
 * @see kva.logiikka.Ryhma
 */
public abstract class KurssitarjottimenLataaja {
    
    private List<PeriodinTunniste> tunnisteet;
    private HashMap<String, LuotavaRyhma> ryhmat;
    private HashMap<String, Moduuli> moduulit;
    
    private Consumer<List<PeriodinTunniste>> periodinTunnisteidenKasittely;
    private Consumer<Kurssitarjotin> kurssitarjottimenKasittely;
    private Consumer<Throwable> virheenKasittely;
    
    private boolean tunnisteidenLatausKaynnissa = false;
    private boolean tarjottimenLatausKaynnissa = false;
    
    /**Lataa {@code PeriodinTunnisteet}, jotka kuvaavat saatavilla olevia periodeja.
     * <p>
     * Lataus voidaan ja kannattaa siirtää alaluokassa taustasäikeen tehtäväksi, jolloin
     * metodi palauttaa välittömästi. Latauksen aikana alaluokka lisää {@link kva.logiikka.PeriodinTunniste}et 
     * metodilla {@link #lisaaPeriodinTunniste(kva.logiikka.PeriodinTunniste)} ja 
     * lopulta käskee niiden lähetyksen kyselijälle metodilla {@link #lahetaPeriodinTunnisteet()}. 
     * Jos latauksen aikana syntyy poikkeus, se lähetetään kyselijälle metodilla {@link #lahetaVirhe(java.lang.Throwable)}.
     * <p>
     * Metodia saa kutsua vain luokasta {@code KurssitarjottimenLataaja} JavaFX:n 
     * sovellussäikeessä.
     * 
     * @param data tarvittavat tiedot paikasta, josta kurssitarjottimen tiedot ladataan. 
     *        On alaluokan vastuulla määritellä, mitä tietoja taulukon on sisällettävä.
     * @throws java.lang.IllegalArgumentException jos {@code data} on virheellistä
     * @throws java.lang.ClassCastException jos {@code data} sisältää vääräntyyppisiä 
     *         olioita
     */
    public abstract void lataaPeriodienTunnisteet(Object[] data);
    
    /**Lataa varsinaisen {@code Kurssitarjottimen} sisältämät tiedot.
     * <p>
     * Lataus voidaan ja kannattaa siirtää alaluokassa taustasäikeen tehtäväksi, jolloin 
     * metodi palauttaa välittömästi. Latauksen aikana alaluokka lisää {@code Kurssitarjottimen} 
     * tarvitsevat tiedot metodeilla {@link #lisaaRyhma(kva.logiikka.lataus.LuotavaRyhma)}, 
     * {@link #lisaaSijainti(java.lang.String, kva.logiikka.PalkinTunniste)}ja {@link #lisaaModuuli(kva.logiikka.Moduuli)}. 
     * Kun lataus on valmis, tiedoista koostettu {@code Kurssitarjotin} lähetetään 
     * kyselijälle metodilla {@link #lahetaKurssitarjotin()}. Jos latauksen aikana 
     * syntyy poikkeus, se lähetetään kyselijälle metodilla {@link #lahetaVirhe(java.lang.Throwable)}.
     * <p>
     * Metodia saa kutsua vain luokasta {@code KurssitarjottimenLataaja} JavaFX:n 
     * sovellussäikeessä.
     * 
     * @param periodit niiden periodien tunnisteet, jotka halutaan mukaan {@code Kurssitarjottimeen}, 
     *        samassa järjestyksessä, kuin missä ne lisättiin metodilla {@link #lisaaPeriodinTunniste(kva.logiikka.PeriodinTunniste)}
     */
    public abstract void lataaKurssitarjotin(List<PeriodinTunniste> periodit);
    
    /**Käynnistää mahdollisten periodien tunnisteiden latauksen.
     * <p>
     * Alaluokan toteutuksesta riippuen lataus siirretään taustasäikeelle, jolloin metodi 
     * palauttaa välittömästi. Latauksen tuloksesta saa tiedon parametreinä annettujen 
     * {@code Consumerien} avulla.
     * <p>
     * Metodia on kutsuttava JavaFX:n sovellussäikeessä. Ainoat poikkeukset, jotka metodi 
     * voi heittää, ovat {@code IllegalStateException}, jos metodia kutsutaan lataajan 
     * ladatessa {@code PeriodinTunnisteita} tai kurssitarjotinta, sekä {@code NullPointerException}, 
     * jos {@code tuloksenKasittely} tai {@code virheenKasittely} on arvoltaan {@code null}. 
     * Muut poikkeukset lähetetään {@code virheenKasittelylle}.
     * 
     * @param data tarvittavat tiedot paikasta, josta kurssitarjottimen tiedot ladataan. 
     *        On alaluokan vastuulla määritellä, mitä tietoja taulukon on sisällettävä.
     * @param tuloksenKasittely kun periodien nimet on ladattu, ne ilmoitetaan kutsumalla 
     *        metodia {@code accept()} JavaFX:n sovellussäikeessä.
     * @param virheenKasittely jos lataus keskeytyy poikkeukseen, tämä ilmoitetaan 
     *        kutsumalla metodia {@code accept()} JavaFX:n sovellussäikeessä.
     * @throws java.lang.IllegalStateException jos metodia kutsutaan, kun {@code PeriodinTunnisteiden} 
     *         tai kurssitarjottimen lataus on käynnissä
     * @throws java.lang.NullPointerException jos {@code tuloksenKasittely} tai {@code virheenKasittely} 
     *         on {@code null}
     */
    public final void aloitaPeriodinTunnisteidenLataus(Object[] data, 
            Consumer<List<PeriodinTunniste>> tuloksenKasittely, Consumer<Throwable> virheenKasittely) {
        if(tunnisteidenLatausKaynnissa || tarjottimenLatausKaynnissa) {
            throw new IllegalStateException("Latausta ei voi aloittaa sellaisen ollessa käynnissä.");
        }
        this.periodinTunnisteidenKasittely = Objects.requireNonNull(tuloksenKasittely);
        this.virheenKasittely = Objects.requireNonNull(virheenKasittely);
        this.tunnisteet = new ArrayList<>();
        tunnisteidenLatausKaynnissa = true;
        try {
            lataaPeriodienTunnisteet(data);
        } catch(Throwable t) {
            lahetaVirhe(t);
        }
    }
    
    /**Käynnistää {@code Kurssitarjottimen} latauksen.
     * <p>
     * Alaluokan toteutuksesta riippuen lataus siirretään taustasäikeelle, jolloin metodi 
     * palauttaa välittömästi. Latauksen tuloksesta saa tiedon parametreinä annettujen 
     * {@code Consumerien} avulla.
     * <p>
     * Metodia on kutsuttava JavaFX:n sovellussäikeessä. Ainoat poikkeukset, jotka metodi 
     * voi heittää, ovat {@code IllegalStateException}, jos metodia kutsutaan lataajan 
     * ladatessa {@code PeriodinTunnisteita} tai kurssitarjotinta, sekä {@code NullPointerException}, 
     * jos {@code tuloksenKasittely} tai {@code virheenKasittely} on arvoltaan {@code null}. 
     * Muut poikkeukset lähetetään {@code virheenKasittelylle}.
     * 
     * @param valittavat niiden periodien tunnisteet, joiden kurssit halutaan mukaan 
     *        {@code Kurssitarjottimeen}. Järjestyksellä ei ole väliä.
     * @param tuloksenKasittely kun kurssitarjotin on ladattu, ne ilmoitetaan kutsumalla 
     *        metodia {@code accept()} JavaFX:n sovellussäikeessä.
     * @param virheenKasittely jos lataus keskeytyy poikkeukseen, tämä ilmoitetaan 
     *        kutsumalla metodia {@code accept()} JavaFX:n sovellussäikeessä.
     * @throws java.lang.IllegalStateException jos metodia kutsutaan, kun {@code PeriodinTunnisteiden} 
     *         tai kurssitarjottimen lataus on käynnissä
     * @throws java.lang.NullPointerException jos {@code tuloksenKasittely} tai {@code virheenKasittely} 
     *         on {@code null}
     */
    public final void aloitaKurssitarjottimenLataus(Collection<PeriodinTunniste> valittavat, 
            Consumer<Kurssitarjotin> tuloksenKasittely, Consumer<Throwable> virheenKasittely) {
        if(tunnisteidenLatausKaynnissa || tarjottimenLatausKaynnissa) {
            throw new IllegalStateException("Latausta ei voi aloittaa sellaisen ollessa käynnissä.");
        }
        
        HashSet<PeriodinTunniste> mukaanOtettavat = new HashSet<>(valittavat);
        List<PeriodinTunniste> annettavat = new ArrayList<>(tunnisteet);
        Iterator<PeriodinTunniste> lapikaynti = annettavat.iterator();
        while (lapikaynti.hasNext()) {
            if (!mukaanOtettavat.remove(lapikaynti.next())) {
                lapikaynti.remove();
            }
        }
        if (!mukaanOtettavat.isEmpty()) {
            StringBuilder poikkeusViesti = new StringBuilder("Yksi tai useampi tuntematon PeriodinTunniste:");
            for (PeriodinTunniste ylijaama : mukaanOtettavat) {
                poikkeusViesti = poikkeusViesti.append("\n").append(ylijaama.toString());
            }
            
            lahetaVirhe(new IllegalArgumentException(poikkeusViesti.toString()));
            return;
        }
        
        this.kurssitarjottimenKasittely = Objects.requireNonNull(tuloksenKasittely);
        this.virheenKasittely = Objects.requireNonNull(virheenKasittely);
        
        this.ryhmat = new HashMap<>();
        this.moduulit = new HashMap<>();
        tarjottimenLatausKaynnissa = true;
        try {
            lataaKurssitarjotin(annettavat);
        } catch(Throwable t) {
            lahetaVirhe(t);
        }
    }
    
    /**Lisää uuden {@code PeriodinTunnisteen} listaan, joka lähetetään kyselijälle.
     * <p>
     * Metodia voi kutsua taustasäikeessä.
     * 
     * @param uusi lisättävä {@code PeriodinTunniste}
     * @throws java.lang.IllegalStateException jos metodia kutsutaan, kun {@code PeriodinTunnisteiden} 
     *         lataus ei ole käynnissä
     */
    protected synchronized final void lisaaPeriodinTunniste(PeriodinTunniste uusi) {
        if(tunnisteidenLatausKaynnissa == false) {
            throw new IllegalStateException("PeriodinTunnisteita voi lisätä vain latauksen aikana.");
        }
        tunnisteet.add(uusi);
    }
    
    /**Lähettää lisätyt {@code PeriodinTunnisteet} kyselijälle.
     * <p>
     * Metodia on kutsuttava JavaFX:n sovellussäikeessä.
     * 
     * @see #lisaaPeriodinTunniste(kva.logiikka.PeriodinTunniste) 
     * @throws java.lang.IllegalStateException jos metodia kutsutaan, kun {@code PeriodinTunnisteiden} 
     *         lataus ei ole käynnissä
     */
    protected final void lahetaPeriodinTunnisteet() {
        if(tunnisteidenLatausKaynnissa == false) {
            throw new IllegalStateException("PeriodinTunnisteet voi lähettää vain latauksen aikana.");
        }
        tunnisteidenLatausKaynnissa = false;
        periodinTunnisteidenKasittely.accept(tunnisteet);
    }
    
    /**Lisää uuden sijainnin kurssitarjottimeen lisättävälle ryhmälle.
     * <p>
     * Jos metodilla {@link #lisaaRyhma(kva.logiikka.lataus.LuotavaRyhma)} on lisätty 
     * {@link kva.logiikka.lataus.LuotavaRyhma}, jolla on annettu koodi, annettu sijainti 
     * lisätään ryhmälle, ja metodi palauttaa arvon {@code true}. Jos ehdot täyttävää 
     * {@code LuotavaaRyhmaa} ei löydy, metoid palauttaa arvon {@code false}.
     * <p>
     * Metodia voi kutsua taustasäikeessä. 
     * 
     * @param ryhmakoodi sen {@link kva.logiikka.Ryhma}n koodi, jolle sijainti halutaan 
     *        lisätä
     * @param uusiSijainti lisättävä sijainti
     * @return {@code true}, jos sijainti lisättiin
     * @throws java.lang.IllegalStateException jos metodia kutsutaan, kun {@code Kurssitarjottimen} 
     *         lataus ei ole käynnissä
     * @see #lisaaRyhma(kva.logiikka.lataus.LuotavaRyhma) 
     */
    protected final boolean lisaaSijainti(String ryhmakoodi, PalkinTunniste uusiSijainti) {
        if(tarjottimenLatausKaynnissa == false) {
            throw new IllegalStateException("Sijainteja voi lisätä vain latauksen aikana.");
        }
        
        if(ryhmat.containsKey(ryhmakoodi)) {
            ryhmat.get(ryhmakoodi).lisaaSijainti(uusiSijainti);
            return true;
        } else {
            return false;
        }
    }
    
    /**Lisää uuden {@code LuotavanRyhman Kurssitarjottimeen}.
     * <p>
     * Metodia voi kutsua taustasäikeessä.
     * 
     * @param uusi lisättävä {@code LuotavaRyhma}
     * @throws java.lang.IllegalStateException jos metodia kutsutaan, kun {@code Kurssitarjottimen} 
     *         lataus ei ole käynnissä
     */
    protected synchronized final void lisaaRyhma(LuotavaRyhma uusi) {
        if(tarjottimenLatausKaynnissa == false) {
            throw new IllegalStateException("Ryhmia voi lisätä vain latauksen aikana.");
        }
        ryhmat.put(uusi.getRyhmakoodi(), uusi);
    }
    
    /**Kertoo, onko lataajalle lisätty {@code Moduulia}, jolla on annettu ryhmäkoodi.
     * <p>
     * Metodia voi kutsua taustasäikeestä.
     * 
     * @param ryhmakoodi {@code Moduulin} ryhmakoodi.
     * @return {@code true}, jos ehdot täyttävä {@code Moduuli} löytyi
     * @throws java.lang.IllegalStateException jos metodia kutsutaan, kun {@code Kurssitarjottimen} 
     *         lataus ei ole käynnissä
     * @see #lisaaModuuli(kva.logiikka.Moduuli) 
     * @see kva.logiikka.Moduuli
     */
    protected synchronized final boolean onModuulia(String ryhmakoodi) {
        if(tarjottimenLatausKaynnissa == false) {
            throw new IllegalStateException("Moduuleja voi kysellä vain latauksen aikana.");
        }
        return moduulit.containsKey(ryhmakoodi);
    }
    
    /**Lisää uuden {@code Moduulin Kurssitarjottimeen}.
     * <p>
     * Metodia voi kutsua taustasäikeestä.
     * 
     * @param uusi lisättävä {@code Moduuli}
     * @throws java.lang.IllegalStateException jos metodia kutsutaan, kun {@code Kurssitarjottimen} 
     *         lataus ei ole käynnissä
     */
    protected synchronized final void lisaaModuuli(Moduuli uusi) {
        if(tarjottimenLatausKaynnissa == false) {
            throw new IllegalStateException("Moduuleja voi lisätä vain latauksen aikana.");
        }
        moduulit.put(uusi.getKoodi(), uusi);
    }
    
    /**Koostaa {@code Kurssitarjottimen} annettujen tietojen perusteelle ja lähettää 
     * sen kyselijälle.
     * <p>
     * Metodia on kutsuttava JavaFX:n sovellussäikeestä.
     * 
     * @throws java.lang.IllegalStateException jos metodia kutsutaan, kun {@code Kurssitarjottimen} 
     *         lataus ei ole käynnissä
     */
    protected final void lahetaKurssitarjotin() {
        if(tarjottimenLatausKaynnissa == false) {
            throw new IllegalStateException("Kurssitarjottimen voi lähettää vain latauksen aikana.");
        }
        
        boolean onnistui = true;
        Kurssitarjotin tarjotin = null;
        tarjottimenLatausKaynnissa = false;
        try {
            tarjotin = new Kurssitarjotin(ryhmat.values(), moduulit.values(), tunnisteet);
        } catch(Exception e) {
            onnistui = false;
            lahetaVirhe(e);
        }
        if(onnistui) {
            tarjottimenLatausKaynnissa = false;
            kurssitarjottimenKasittely.accept(tarjotin);
        }
    }
    
    /**Ilmoittaa kyselijälle, että {@code PeriodinTunnisteiden} tai {@code Kurssitarjottimen} 
     * lataus on kaatunut poikkeukseen.
     * 
     * @param virhe syntynyt virhe
     * @throws java.lang.IllegalStateException jos metodia kutsutaan, kun {@code Kurssitarjottimen} 
     *         lataus ei ole käynnissä
     */
    protected final void lahetaVirhe(Throwable virhe) {
        if(!(tarjottimenLatausKaynnissa || tunnisteidenLatausKaynnissa)) {
            throw new IllegalStateException("Poikkeuksen voi lähettää vain latauksen aikana.");
        }
        tunnisteidenLatausKaynnissa = false;
        tarjottimenLatausKaynnissa = false;
        virheenKasittely.accept(virhe);
    }

    /**Kertoo, lataako lataaja parhaillaan {@code PeriodinTunnisteita}.
     * <p>
     * Lataus käynnistyy, kun kyselijä kutsuu metodia {@code aloitaPeriodinTunnisteidenLataus()}, 
     * ja päättyy, kun alaluokasta kutsutaan metodia {@code lahetaPeriodinTunnisteet()} 
     * tai {@code lahetaVirhe()}.
     * <p>
     * Metodia voi kutsua taustasäikeessä.
     * 
     * @return {@code true}, jos {@code PeriodinTunnisteiden} lataus on käynnissä.
     */
    public synchronized final boolean tunnisteidenLatausKaynnissa() {
        return tunnisteidenLatausKaynnissa;
    }

    /**Kertoo, lataako lataaja parhaillaan {@code Kurssitarjotinta}.
     * <p>
     * Lataus käynnistyy, kun kyselijä kutsuu metodia {@code aloitaKurssitarjottimenLataus()}, 
     * ja päättyy, kun alaluokasta kutsutaan metodia {@code lahetaKurssitarjotin()} 
     * tai {@code lahetaVirhe()}.
     * <p>
     * Metodia voi kutsua taustasäikeessä.
     * 
     * @return {@code true}, jos {@code PeriodinTunnisteiden} lataus on käynnissä.
     */
    public synchronized final boolean tarjottimenLatausKaynnissa() {
        return tarjottimenLatausKaynnissa;
    }
    
    /**Poikkeus, joka voidaan heittää, kun kurssitarjottimen latauksessa tapahtuu 
     * virhe.
     * 
     * @author Väinö Viinikka
     * @see kva.logiikka.lataus.KurssitarjottimenLataaja
     */
    public class LataajaPoikkeus extends IOException {
        
        /**Luo uuden {@code TestiLataajaPoikkeuksen}.
         * 
         * @param viesti poikkeukseen liittyvä viesti, joka voidaan palauttaa metodilla 
         *        {@link java.lang.Throwable#getMessage()}
         */
        public LataajaPoikkeus(String viesti) {
            super(viesti);
        }
    }
}
