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

/**Sisältää luokat, joita käytetään {@code Kurssitarjottimen} lataamiseen ulkoisen 
 * tiedon perusteella.
 * <p>
 * Lataukseen tarvittavat komennot määritellään rajapinnassa {@link kva.logiikka.lataus.KurssitarjottimenLataaja}. 
 * Rajapinnan toteutukset vastaavat tietojen hakemisesta kukin omalla tavallaan. Toistaiseksi 
 * valmiina on ainoastaan {@link kva.logiikka.lataus.TestiLataaja}, joka hakee tiedot 
 * tekstitiedostosta. Internetiä hyödyntävä lataaja tullaa toteuttamaan lopulliseen 
 * versioon.
 * 
 * @author Väinö Viinikka
 */
package kva.logiikka.lataus;
