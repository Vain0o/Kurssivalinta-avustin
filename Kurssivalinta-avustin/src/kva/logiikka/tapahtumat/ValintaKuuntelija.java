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

/**Toteuttaa mahdollisuuden kuunnella tiettyyn {@code Ryhmaan} kohdistuvia valintoja.
 * <p>
 * Rajapinnan toteuttava olio lisätään {@link kva.logiikka.Ryhma}lle {@code lisaaValintaKuuntelija()}-metodilla, 
 * minkä jälkeen {@code ValintaKuuntelijaa} tiedotetaan {@code Ryhmaan} vaikuttavista 
 * valintatoimenpiteistä: {@code Ryhman} itsensä valinnasta ja valinnan poistosta, 
 * sekä samamoduulisen {@code Ryhman} valinnasta muualta samassa {@code Kurssitarjottimessa}.
 * Kuuntelijan voi myös poistaa {@code Ryhman poistaValintaKuuntelija()}-metodilla. 
 * Sama {@code ValitaKuuntelija} voidaan lisätä usealle eri {@code Ryhmalle}.
 * <p>
 * Jos {@code Ryhma} valitaan muualta, kun se on jo valittu, {@code Ryhman} valinta 
 * poistetaan automaattisesti. {@code ValintaKuuntelijaa} tiedotetaan tällöin kahdesti: 
 * ensin poistamisesta ja sitten muualta valinnasta.
 *
 * @author Väinö Viinikka
 * @since Kurssivalinta-avustin 1.0
 */
@FunctionalInterface
public interface ValintaKuuntelija {
    
    /**Metodia kutsutaan, kun {@code Ryhman} valintatila muuttuu
     * 
     * @param t tilanmuutosta kuvaava {@code ValintaTapahtuma}
     */
    public void tilaMuuttui(ValintaTapahtuma t);
}
