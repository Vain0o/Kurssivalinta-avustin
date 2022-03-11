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
package kva.logiikka.tapahtumat;

import kva.logiikka.Ryhma;

/**Toteuttaa mahdollisuuden kuunnella tiettyyn {@code Ryhmaan} kohdistuvia 
 * valintoja.
 * <p>
 * Rajapinnan toteuttava olio lisätään {@link kva.logiikka.Ryhma}lle {@code lisaaValintaKuuntelija()}-metodilla, 
 * minkä jälkeen {@code ValintaKuuntelijaa} tiedotetaan {@code Ryhmaan} vaikuttavista 
 * valintatoimenpiteistä. Kuuntelijan voi myös poistaa {@code Ryhman poistaValintaKuuntelija()}-metodilla. 
 * Sama {@code ValitaKuuntelija} voidaan lisätä usealle eri {@code Ryhmalle}.
 *
 * @author Väinö Viinikka
 */
public interface ValintaKuuntelija {
    
    /**Metodia kutsutaan, kun {@code Ryhma} valitaan.
     * 
     * @param ryhma {@code Ryhma}, joka valittiin
     */
    public void valittu(Ryhma ryhma);
    
    /**Metodia kutsutaan, kun {@code Ryhman} valinta poistetaan.
     * 
     * @param ryhma {@code Ryhma}, jonka valinta poistettiin
     */
    public void valintaPoistettu(Ryhma ryhma);
    
    /**Metodia kutsutaan, kun samasta {@code Kurssitarjottimesta} valittiin {@code Ryhma}, 
     * jonka {@code Kurssi} on sama, kuin kyseessä olevalla {@code Ryhmalla}, mutta 
     * joka ei ole {@code Ryhma} itse.
     * 
     * @param ryhma kyseessä oleva {@code Ryhma}
     */
    public void valittuMuualta(Ryhma ryhma);
    
    /**Metodia kutsutaan, kun samassa {@code Kurssitarjottimessa} poistetaan sellaisen 
     * {@code Ryhman} valinta, jonka {@code Kurssi} on sama, kuin kyseessä olevalla 
     * {@code Ryhmalla}, mutta joka ei ole {@code Ryhma} itse.
     * 
     * @param ryhma kyseessä oleva {@code Ryhma}
     */
    public void valintaPoistettuMuualta(Ryhma ryhma);
}
