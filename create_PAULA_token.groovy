/**************************************
 * Copyright 2015 - Universität Hamburg, SIGS.
 * 
 * This file is part of a collection of scripts that exports GATE-Corpora to PAULA XML.
 *
 * It is free software: 
 * you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * It is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Dies ist Freie Software: 
 * Sie können das Skript unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation, Version 3 der Lizenz oder 
 * (nach Ihrer Wahl) jeder neueren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * Es wird in der Hoffnung, 
 * dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; 
 * sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT
 * oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 * 
 */

/**
 * @author Fabian Barteld
 *
 */

Set tokenNodes = new TreeSet()

doc.getNamedAnnotationSets().each{annotationSet ->
	annotationSet.getValue().each{annotation ->
		tokenNodes.add(annotation.start())
		tokenNodes.add(annotation.end())
	}
}

sortedTokenNodes = tokenNodes.toArray()
for(i = 0; i < sortedTokenNodes.length - 1; i++) {
	// only add non-whitespace token
	if (!doc.getContent().getContent(sortedTokenNodes[i], sortedTokenNodes[i+1]).toString().replaceAll("\\s+","").isEmpty()) {
		outputAS.add(sortedTokenNodes[i], sortedTokenNodes[i+1], "tok", Factory.newFeatureMap())	
	}
}
