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

/**Sisältää luokat, jotka toteuttavat Kurssivalinta-avustimen graafisen käyttöliittymän 
 * JavaFX-kirjastoa hyödyntäen.
 * <p>
 * Käyttöliittymän keskus on luokka {@link kva.ui.Kayttoliittyma}. Se hallinnoi {@link kva.ui.Nakyma}-olioita, 
 * jotka luovat käyttöliittymän varsinaisen sisällön, sekä luokkaa {@link kva.ui.Asetukset}, 
 * joka sisältää tiedot siitä, miten kurssit esitetään kurssitarjottimessa.
 * 
 * @author Väinö Viinikka
 * @since Kurssivalinta-avustin 1.0
 */
package kva.ui;
