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

File base_dir = new File(scriptParams.outputFolder)
File korpus_dir = new File(base_dir, corpus.getName())
korpus_dir.mkdir()
File text_dir = new File(korpus_dir, doc.features.Place.toString().replace('ä', 'ae').replace('ü', 'ue').replace('ö', 'oe') + doc.features.Year.toString())
text_dir.mkdir()

// copy dtds
File dtd_dir = new File(scriptParams.dtdFolder)
new AntBuilder().copy(todir: text_dir.getAbsolutePath()) {
	fileset(dir: dtd_dir.getAbsolutePath())
}

import groovy.io.FileType

base_dir.eachFile (FileType.FILES) { file ->
    if(file.name.contains(basename)) {
     	file.renameTo(new File(text_dir, file.getName().replace('ä', 'ae').replace('ü', 'ue').replace('ö', 'oe')))
    }
}


// create AnnoSet
new File(text_dir, basename.replace('ä', 'ae').replace('ü', 'ue').replace('ö', 'oe') + ".anno.xml").withWriter("UTF-8"){ out ->
	// Header
	out.writeLine('<?xml version="1.0" standalone="no"?>')
	out.writeLine('<!DOCTYPE paula SYSTEM "paula_struct.dtd">')
	out.writeLine('')
	out.writeLine('<paula version="1.1">')
	out.writeLine(/<header paula_id="${ basename }.anno" \/>/)
	out.writeLine('')
	out.writeLine('<structList xmlns:xlink="http://www.w3.org/1999/xlink" type="annoSet">')
	out.writeLine('	<struct id="anno1">')
	
	i = 1
	text_dir.eachFile (FileType.FILES) { file ->
		if(file.name.endsWith('.xml') && !file.name.endsWith('anno.xml')) {
			out.writeLine(/		<rel id="rel_${i}" xlink:href="${file.name.substring(file.name.lastIndexOf("/") + 1).substring(file.name.lastIndexOf("\\") + 1)}" \/>/)
			i += 1
		}	
	}
	
	
	// Footer
	out.writeLine('	</struct>')
	out.writeLine('</structList>')
	out.writeLine('')
	out.writeLine('</paula>')
}
