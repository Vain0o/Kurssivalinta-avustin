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
package kva.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;

/**Esitys kurssitarjottimeen kuuluvasta ryhmästä
 * <p>
 * Kurssia itseään kuvaavat tiedot, esimerkiksi onko kurssi pakollinen, säilötään 
 * luokassa {@link kva.domain.Kurssi}, ja ne saa selville {@link #getKurssi()}-metodin 
 * kautta. Ryhmää itseään koskevat tiedot, kuten sijainti kurssitarjottimessa säilötään 
 * tähän luokkaan. Tiedot, jotka voivat muuttua sovelluksen käytön aikana, kuten se, 
 * onko kurssi valittu, säilötään {@link javafx.beans.property.Property}inä.
 *
 * @author Väinö Viinikka
 */
public class Ryhma {
    
    private Kurssi kurssi;
    private String ryhmanimi;
    private String sijainti;
    private final BooleanProperty onValittu = new SimpleBooleanProperty();
    private final ReadOnlyBooleanWrapper valittuMuualta = new ReadOnlyBooleanWrapper();

    /**Luo uuden {@Ryhman} annetuilla ryhmäkoodilla ja sijainnilla.
     * 
     * @param ryhmakoodi Ryhmän kurssitarjottimesta löytyvä koodi, esim. FY05.2
     * @param sijainti Merkkijono, joka määrittelee, missä jaksossa ja palkissa 
     *                 kurssi sijaitsee.
     */
    public Ryhma(String ryhmakoodi, String sijainti) {
        this.sijainti = sijainti;
        
        String[] osat = ryhmakoodi.split(".");
        kurssi = new Kurssi(osat[0]);
        if (osat.length > 1) {
            ryhmanimi = osat[1];
        } else {
            ryhmanimi = "";  
        }
    }

    /**Palauttaa {@code Kurssin}, joka sisältää tiedot ryhmän kuvaamasta opintokokonaisuudesta.
     * 
     * @return {@Ryhman Kurssi} 
     */
    public Kurssi getKurssi() {
        return kurssi;
    }
    
    /**
     * 
     * @return 
     */
    public String getKoodi() {
        return new StringBuilder().append(kurssi.getKurssikoodi())
                .append(".")
                .append(ryhmanimi)
                .toString();
    }

    /**Palauttaa samaa kurssia koskevia {@Ryhmia} toisistaan erottavan tunnuksen.
     * <p>
     * Esimerkiksi ryhmäkoodin "FY05.2" ryhmätunniste olisi "2". Nimeä ei palauteta 
     * kokonaislukumuodossa, koska ainakin Otaniemen lukiossa on käytössä myös kirjaimia 
     * sisältäviä ryhmänimiä, kuten MAA03.D1.
     * 
     * @return Ryhmäkoodin pistettä seuraava osa
     */
    public String getRyhmatunniste() {
        return ryhmanimi;
    }

    /**
     * 
     * @return 
     */
    public String getSijainti() {
        return sijainti;
    }
    
    /**
     * 
     * @return 
     */
    public boolean OnValittu() {
        return onValittu.get();
    }

    /**
     * 
     * @param value 
     */
    public void setOnValittu(boolean value) {
        onValittu.set(value);
    }

    /**
     * 
     * @return 
     */
    public BooleanProperty onValittuProperty() {
        return onValittu;
    }

    public boolean onValittuMuualta() {
        return valittuMuualta.get();
    }

    public ReadOnlyBooleanProperty valittuMuualtaProperty() {
        return valittuMuualta.getReadOnlyProperty();
    }
}
