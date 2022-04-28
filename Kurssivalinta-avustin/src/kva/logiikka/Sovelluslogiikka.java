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
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import kva.logiikka.lataus.KurssitarjottimenLataaja;

/**Ylin säiliöluokka Kurssivalinta-avustimen sovelluslogiikalle, sisältää {@code Kurssitarjottimen}.
 * <p>
 * {@link kva.logiikka.Kurssitarjotin} luodaan komennoilla {@link #lataaPeriodienNimet(java.util.function.Consumer, java.util.function.Consumer, java.lang.Object...)} 
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
    private final ReadOnlyObjectWrapper<LatauksenTila> tila;

    /**Luo uuden {@code Sovelluslogiikan}.
     *
     * @param lataaja {@code KurssitarjottimenLataaja}, joka hakee {@code Kurssitarjottimen} 
     *        luomiseen tarvittavat tiedot eri käskyllä
     */
    public Sovelluslogiikka(KurssitarjottimenLataaja lataaja) {
        this.lataaja = lataaja;
        this.viesti = new ReadOnlyStringWrapper();
        this.tila  = new ReadOnlyObjectWrapper<>(LatauksenTila.LUOTU);
    }

    /**Palauttaa {@code Sovelluslogiikan} sisältämän {@code Kurssitarjottimen}.
     *
     * @return {@code Sovelluslogiikan Kurssitarjotin}, jos {@link #getTila()}
     *         on {@code KURSSITARJOTIN_LADATTU}. Muutoin {@code null}.
     */
    public Kurssitarjotin getTarjotin() {
        return tarjotin;
    }

    /**Lataa mahdollisten periodien tunnisteet.
     * <p>
     * Metodia tulee kutsua JavaFX:n sovellussäikeessä. Metodi aloittaa {@code KurssitarjottimenLataajan} 
     * toteutuksesta riippuen synkronoimattoman latauksen, ja palauttaa välittömästi. 
     * Latauksen tulos ilmoitetaan joko {@code tuloksenKasittelylle} tai {@code virheenKasittelylle}, 
     * mutta sitä voi myös seurata kuuntelemalla {@code Sovelluslogiikan} {@link #tilaProperty()}ä.
     * <p>
     * Sovellussäikeelle lähetettävä lista on {@code KurssitarjottimenLataajan} toteutuksesta 
     * riippuen toivon mukaan järjestetty: saman oppilaitoksen periodit ovat peräkkäin 
     * aikajärjestyksessä.
     *
     * @param tuloksenKasittely Kun periodien tunnisteet on selvitetty, ne lähetetään JavaFX:n 
     *        sovellussäikeelle kutsumalla metodia {@code accept()}.
     * @param virheenKasittely Jos nimien haku kaatuu poikkeukseen, poikkeus lähetetään 
     *        JavaFX:n sovellussäikeelle kutsumalla metodia {@code accept()}.
     * @param data {@code KurssitarjottimenLataajan} vaatimat tiedot paikasta, josta 
     *        kurssitarjotin ladataan. {@code KurssitarjottimenLataajan} toteutus 
     *        määrittää, mitä tietoja taulukon tulee sisältää.
     * @throws java.lang.IllegalStateException jos {@link #getTila()} on {@code LADATAAN_PERIODIEN_NIMIA} 
     *         tai {@code LADATAAN_KURSSITARJOTINTA}
     * @throws java.lang.NullPointerException jos jokin parametreistä on {@code null}
     */
    public void lataaPeriodienNimet(Consumer<List<PeriodinTunniste>> tuloksenKasittely, Consumer<Throwable> virheenKasittely, Object... data) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(tuloksenKasittely);
        Objects.requireNonNull(virheenKasittely);

        if (getTila() == LatauksenTila.LADATAAN_PERIODIEN_NIMIA || getTila() == LatauksenTila.LADATAAN_KURSSITARJOTINTA) {
            throw new IllegalStateException("Periodien nimiä ei voi ladata, kun lataajalla on taustaprosessi käynnissä.");
        }
        tila.setValue(LatauksenTila.LADATAAN_PERIODIEN_NIMIA);

        Consumer<List<PeriodinTunniste>> laajempiTuloksenKasittely = (tulos) -> {
            viesti.setValue("");
            periodinTunnisteet = tulos;
            tuloksenKasittely.accept(tulos);
            tila.setValue(LatauksenTila.PERIODIEN_NIMET_LADATTU);
        };
        Consumer<Throwable> laajempiVirheenKasittely = (virhe) -> {
            viesti.setValue("");
            virheenKasittely.accept(virhe);
            tila.setValue(LatauksenTila.LUOTU);
        };
        viesti.setValue("Ladataan periodien nimiä.");
        lataaja.aloitaPeriodinTunnisteidenLataus(data, laajempiTuloksenKasittely, laajempiVirheenKasittely);
    }

    /**Lataa {@code Kurssitarjottimen}, joka sisältää saatavilla olevat ryhmät ja 
     * moduulit.
     * <p>
     * Metodia tulee kutsua JavaFX:n sovellussäikeessä. Metodi aloittaa {@code KurssitarjottimenLataajan} 
     * toteutuksesta riippuen synkronoimattoman latauksen, ja palauttaa välittömästi. 
     * Latauksen tulos ilmoitetaan joko {@code tuloksenKasittelylle} tai {@code virheenKasittelylle}, 
     * mutta sitä voi myös seurata kuuntelemalla {@code Sovelluslogiikan} {@link #tilaProperty()}ä.
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
    public void lataaKurssitarjotin(Collection<PeriodinTunniste> valittavat, Consumer<Kurssitarjotin> tuloksenKasittely,
            Consumer<Throwable> virheenKasittely) {
        Objects.requireNonNull(valittavat);
        Objects.requireNonNull(tuloksenKasittely);
        Objects.requireNonNull(virheenKasittely);

        if (getTila() == LatauksenTila.LUOTU || getTila() == LatauksenTila.LADATAAN_PERIODIEN_NIMIA) {
            throw new IllegalStateException("Kurssitarjotinta ei voi ladata, kun periodien nimiä ei ole ladattu.");
        } else if (getTila() == LatauksenTila.LADATAAN_KURSSITARJOTINTA) {
            throw new IllegalStateException("Kurssitarjotinten latausta ei voi aloittaa, kun se on jo käynnissä.");
        }
        
        LatauksenTila vanhaTila = getTila();
        tila.setValue(LatauksenTila.LADATAAN_KURSSITARJOTINTA);
        
        Consumer<Kurssitarjotin> laajempiTuloksenKasittely = (tulos) -> {
            viesti.setValue("");
            this.tarjotin = tulos;
            tuloksenKasittely.accept(tarjotin);
            tila.setValue(LatauksenTila.KURSSITARJOTIN_LADATTU);
        };
        Consumer<Throwable> laajempiVirheenKasittely = (virhe) -> {
            viesti.setValue("");
            virheenKasittely.accept(virhe);
            tila.setValue(vanhaTila);
        };
        viesti.setValue("Ladataan kurssitarjotinta.");
        lataaja.aloitaKurssitarjottimenLataus(valittavat, laajempiTuloksenKasittely, laajempiVirheenKasittely);
    }

    /**Palauttaa listan periodeista, jotka on mahdollista ladata osaksi
     * kurssitarjotinta.
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

    /**Palauttaa viestin, joka kertoo, mitä {@code Sovelluslogiikka} tekee
     * kullakin hetkellä.
     * <p>
     * Kun {@link #getTila()} on {@code LADATAAN_PERIODIEN_NIMIA}, viesti on "Ladataan 
     * periodien nimiä.", ja kun tila on {@code LADATAAN_KURSSITARJOTINTA}, viesti 
     * on "Ladataan kurssitarjotinta." Muulloin viesti on tyhjä merkkijono. Lisää 
     * toiminnallisuutta saatetaan toteuttaa tulevaisuudessa.
     *
     * @return lataajan toimintaa kuvaava viesti, joka voidaan näyttää käyttöliittymässä.
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
     */
    public LatauksenTila getTila() {
        return tila.get();
    }

    /**Palauttaa tarkkailtavan {@code Propertyn Sovelluslogiikan} tilasta.
     * 
     * @return latauksen tilaa kuvaava {@code Property}
     * @see #getTila() 
     */
    public ReadOnlyObjectProperty<LatauksenTila> tilaProperty() {
        return tila.getReadOnlyProperty();
    }

    /**Esitys {@code Sovelluslogiikan} kulloisestakin toimintavaiheesta.
     * <p>
     * {@link kva.logiikka.Sovelluslogiikka} on aina aluksi tilassa {@code LUOTU}. 
     * Kun käyttäjä kutsuu metodia {@link #lataaPeriodienNimet(java.util.function.Consumer, java.util.function.Consumer, java.lang.Object...)}, 
     * tilaksi vaihtuu {@code LADATAAN_PERIODIEN_NIMIA}. Kun lataus taustasäikeessä 
     * saadaan valmiiksi, tilaksi vaihdetaan {@code PERIODIEN_NIMET_LADATTU}, paitsi 
     * jos lataus kaatuu poikkeukseen, jolloin {@code Sovelluslogiikka} palaa tilaan 
     * {@code LUOTU}. Kun käyttäjä periodien tunnisteiden lataamisen jälkeen kutsuu 
     * metodia {@link #lataaKurssitarjotin(java.util.Collection, java.util.function.Consumer, java.util.function.Consumer)}, 
     * lataaja siirtyy tilaan {@code LADATAAN_KURSSITARJOTINTA}. Kun lataus on valmis, 
     * lataaja siirtyy tilaan {@code KURSSITARJOTIN_LADATTU}, mutta jos se kaatuu 
     * poikkeukseen, lataaja palaa tilaan {@code PERIODIEN_NIMET_LADATTU}.
     *
     * @author Väinö Viinikka
     * @see kva.logiikka.Sovelluslogiikka
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
        
        /**Kertoo, että periodien nimet on ladattu ja {@code Sovelluslogiikka} on 
         * valmis lataamaan itse kurssitarjottimen.
         * <p>
         * LatauksenTila on tässä arvossa myös, mikäli kurssitarjottimen latausta 
         * on yritetty, mutta siinä ei ole onnistuttu.
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
