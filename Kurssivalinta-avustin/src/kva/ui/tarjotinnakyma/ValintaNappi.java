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
package kva.ui.tarjotinnakyma;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import kva.logiikka.Ryhma;

/**Nappi, joka kuvaa {@code Ryhmaa} palkissaan.
 * <p>
 * {@code ValintaNapin} tekstinä on sen kuvaaman {@link kva.logiikka.Ryhma}n ryhmäkoodi, 
 * ja sen taustavärin määrittää, onko {@code Ryhman} {@link kva.logiikka.Moduuli}n 
 * tyyppi.
 * <p>
 * Jos {@link kva.logiikka.Ryhma}lla on useita sijainteja, kutankin niistä vastaa 
 * oma {@code ValintaNappi}. {@code ValintaNapin} klikkaaminen hiiren vasemmalla näppäimellä 
 * valitsee {@code Ryhman}, jolloin kaikki {@code Ryhmaa} kuvaavat {@code ValintaNapit} 
 * korostetaan. Tämän toteuttamistesta vastaa luokka {@link kva.ui.tarjotinnakyma.PalkkiEsitys}, 
 * joka hallinnoi {@code ValintaNappeja}.
 *
 * @author Väinö Viinikka
 * @see kva.ui.tarjotinnakyma.ValintaNappiLista
 */
public class ValintaNappi extends Button implements Comparable<ValintaNappi> {
    
    private final Ryhma ryhma;

    /**Luo uuden {@code ValintaNapin}.
     * 
     * @param ryhma {@code Ryhma}, jota nappi kuvaa
     */
    public ValintaNappi(Ryhma ryhma) {
        super(ryhma.getKoodi());
        this.ryhma = ryhma;
        
        switch(ryhma.getModuuli().getTyyppi()) {
            case PAKOLLINEN:
                setStyle("-fx-background-color: greenyellow; ");
                break;
            case VALTAKUNNALLINEN_SYVENTAVA:
                setStyle("-fx-background-color: salmon; ");
                break;
            case KOULUKOHTAINEN_SYVENTAVA:
                setStyle("-fx-background-color: moccasin; ");
                break;
            case SOVELTAVA:
                setStyle("-fx-background-color: white; ");
                break;
        }
    }

    /**Kertoo {@code ValintaNapille}, onko se korostettava valittuna.
     * <p>
     * Korostettuna napin teksti on lihavoitu, muulloin normaali.
     * 
     * @param onValittu {@code true}, jos {@code Ryhma} valittiin, {@code false}, 
     *        jos sen valinta poistettiin
     */
    public void setOnValittu(boolean onValittu) {
        if(onValittu) {
            super.setFont(Font.font("System", FontWeight.BOLD, 12));
        } else {
            super.setFont(Font.font("System", FontWeight.NORMAL, 12));
        }
    }
    
    /**Palauttaa {@code Ryhman}, jota {@code ValintaNappi} kuvaa.
     * 
     * @return napin kuvaama {@code Ryhma}
     */
    public Ryhma getRyhma() {
        return ryhma;
    }

    @Override
    public int compareTo(ValintaNappi t) {
        return getText().compareTo(t.getText());
    }
}
