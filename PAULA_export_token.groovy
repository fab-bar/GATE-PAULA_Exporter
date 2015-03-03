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



/* <mark id="{Token.id}" xlink:href="#xpointer(string-range (//body, '',{Token.offset},{Token.length}))"/><!-- {Token.Text} --> */

basename = corpus.getName() + "." + doc.features.Place.toString().replace('ä', 'ae').replace('ü', 'ue').replace('ö', 'oe') + doc.features.Year.toString()
anno_basename = basename + "." + "tok"

new File(scriptParams.outputFolder + anno_basename + ".xml").withWriter("UTF-8"){ out ->

	// Header
	out.writeLine('<?xml version="1.0" standalone="no"?>')
	out.writeLine('')
	out.writeLine('<!DOCTYPE paula SYSTEM "paula_mark.dtd">')
	out.writeLine('<paula version="1.1">')
	out.writeLine('')
	out.writeLine(/<header paula_id="${anno_basename}"\/>/)
	out.writeLine('')
	out.writeLine(/<markList xmlns:xlink="http:\/\/www.w3.org\/1999\/xlink" type="tok" xml:base="${basename}.text.xml">/)

	// Token
	AnnotationSet token = inputAS.get("tok")
	// Sort the annotations
	List<Annotation> list = new ArrayList<Annotation>(token);
	Collections.sort(list, new gate.util.OffsetComparator());
	list.each { anno ->
	  out.writeLine(/	<mark id="tok_${anno.id}" xlink:href="#xpointer(string-range (\/\/body, '',${anno.start()+1},${anno.end() - anno.start()}))"\/><!-- ${gate.Utils.stringFor(doc, anno)} -->/)        
    }
	
	// Footer
	out.writeLine('</markList>')
	out.writeLine('</paula>')
	
}
