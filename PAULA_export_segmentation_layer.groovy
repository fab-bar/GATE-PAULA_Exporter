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



// TODO: test if the layers are valid segmentations

// TODO: change to parameter
def seg_layers = [['Tokenannotation', 'graph_token'], ['Tokenannotation','syn_token']]
temporaryAnnotationSet = "PAULA"

seg_layers.each { seg_layer_det ->

	anno_set_name = seg_layer_det[0]
	annotation = seg_layer_det[1]
	annotationSet = doc.getAnnotations(anno_set_name)

	basename = corpus.getName() + "." + doc.features.Place.toString().replace('ä', 'ae').replace('ü', 'ue').replace('ö', 'oe') + doc.features.Year.toString()
	anno_name = annotation.replace('_', '')
	anno_basename = anno_set_name + "." + basename + "." + anno_name

	seg_filename = anno_basename + "_seg.xml"
	feat_filename = anno_basename + "_" + anno_name + ".xml"

	new File(scriptParams.outputFolder + feat_filename).withWriter("UTF-8"){ out ->

		// Header
		out.writeLine('<?xml version="1.0" standalone="no"?>')
		out.writeLine('<!DOCTYPE paula SYSTEM "paula_feat.dtd">')
		out.writeLine('')
		out.writeLine('<paula version="1.1">')
		out.writeLine(/<header paula_id="${anno_basename}_${anno_name}"\/>/)
		out.writeLine('')
		out.writeLine(/<featList xmlns:xlink="http:\/\/www.w3.org\/1999\/xlink" type="${anno_name}" xml:base="${ seg_filename }">/)

		AnnotationSet token = annotationSet.get(annotation)
		// Sort the annotations
		List<Annotation> list = new ArrayList<Annotation>(token);
		Collections.sort(list, new gate.util.OffsetComparator());
		// TODO: take the further segments in discontinuous segmentations into account. 
		list.groupBy([{ it.getFeatures().get("id", it.id).toString() }]).each { key, anno_list ->
			anno = anno_list[0] // get the first annotation
			AnnotationSet paula_toks_tmp = gate.Utils.getContainedAnnotations(doc.getAnnotations(temporaryAnnotationSet), anno, "tok");
			List<Annotation> paula_toks = new ArrayList<Annotation>(paula_toks_tmp);
			Collections.sort(paula_toks, new gate.util.OffsetComparator());
			if(paula_toks.size() >= 1) {
				out.writeLine(/	<feat xlink:href="#${anno_name}_${anno.id}" value="${ gate.Utils.stringFor(doc, anno).replace('&', '&amp;')}"\/>/)
			
		}
	
		// Footer
		out.writeLine('</featList>')
		out.writeLine('')
		out.writeLine('</paula>')
	
	}

}
