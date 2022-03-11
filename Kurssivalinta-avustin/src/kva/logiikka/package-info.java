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

/**Sisältää luokat, jotka toteuttavat Kurssivalinta-avustimen sovelluslogiikan.
 * <p>
 * Sovelluslogiikan sydän on luokka {@link kva.logiikka.Kurssitarjotin}, joka sisältää 
 * saatavilla olevat {@link kva.logiikka.Moduuli}t ja {@link kva.logiikka.Ryhma}t. 
 * Sovelluslogiikan lopullisen perustan muodostaa kuitenkin luokka {@link kva.logiikka.Sovelluslogiikka}, 
 * joka luo {@code Kurssitarjottimen}.
 * <p>
 * {@code Moduuli} edustaa tiettyä opintokokonaisuutta, ja sisältää tiedot sen pakollisuudesta 
 * yms. tiedoista, jotka eivät riipu siitä, mihin kohtaan kurssitarjotinta moduulin 
 * valitsee. {@code Ryhma} puolestaan edustaa kursstarjottimella saatavilla olevaa 
 * ryhmää, ja se sisältää tiedot käytävästä {@code Moduulista}, sekä opetuksen ajankohdista, 
 *  ja muista tiedoista, jotka vaihtelevat ryhmittäin.
 * 
 * @author Väinö Viinikka
 */
package kva.logiikka;