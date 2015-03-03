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



basename = corpus.getName() + "." + doc.features.Place.toString().replace('ä', 'ae').replace('ü', 'ue').replace('ö', 'oe') + doc.features.Year.toString()
anno_basename = basename + "." + "text"

new File(scriptParams.outputFolder + anno_basename + ".xml").withWriter("UTF-8"){ out ->

	// Header
	out.writeLine('<?xml version="1.0" standalone="no"?>')
	out.writeLine('')
	out.writeLine('<!DOCTYPE paula SYSTEM "paula_text.dtd">')
	out.writeLine('')
	out.writeLine('<paula version="1.1">')
	out.writeLine(/<header paula_id="${ anno_basename }" type="text"\/>/)
	out.writeLine('')
	out.write('<body>')

	out.write(doc.getContent().toString().replaceAll("\t", " ").replaceAll('&', '&amp;'))

	
	// Footer
	out.writeLine('</body>')
	out.writeLine('')
	out.writeLine('</paula>')
	
}

// exporting Metadata
new File(scriptParams.outputFolder + basename + ".meta_multifeat.xml").withWriter("UTF-8"){ out ->
	// Header
	out.writeLine('<?xml version="1.0" standalone="no"?>')
	out.writeLine('<!DOCTYPE paula SYSTEM "paula_multiFeat.dtd">')
	out.writeLine('')
	out.writeLine('<paula version="1.1">')
	out.writeLine(/<header paula_id="${basename}.meta_multifeat"\/>/)
	out.writeLine('')

	out.writeLine(/<multiFeatList xmlns:xlink="http:\/\/www.w3.org\/1999\/xlink" type="multiFeat" xml:base="${basename}.anno.xml">/)
	out.writeLine(/	<multiFeat xlink:href="#anno_1">/)
	documentFeatures = doc.getFeatures()
	documentFeatures.keySet().each { feature ->
		out.writeLine(/		<feat name="${feature}" value="${documentFeatures.get(feature).toString()}"\/>/)
		}
	
	// Footer
	out.writeLine('	</multiFeat>')
	out.writeLine('</multiFeatList>')
	out.writeLine('')
	out.writeLine('</paula>')
}
