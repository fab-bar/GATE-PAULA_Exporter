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



// one token
/* <mark id="{Token.id}" xlink:href="#tok_id"/><!-- {Token.Text} --> */
// multiple tokens
/* <mark id="{Token.id}" xlink:href="#xpointer(id('tok_id')/range-to(id('tok_id')))"/><!-- {Token.Text} --> */
// discontinuous span
/* <mark id="{Token.id}" xlink:href="#xpointer(id('tok_id')/range-to(id('tok_id'))),#tok_id"/><!-- {Token.Text} --> */

// TODO: set as parameter
temporaryAnnotationSet = "PAULA"

doc.getNamedAnnotationSets().each{annotationSet ->
  if (annotationSet.getKey() != temporaryAnnotationSet) {
    annotationSet.getValue().getAllTypes().each{annotation ->


      basename = corpus.getName() + "." +
	doc.features.Place.toString().replace('ä', 'ae').replace('ü', 'ue').replace('ö', 'oe') +
	doc.features.Year.toString()
      anno_name = annotation.replace('_', '')
      anno_basename = annotationSet.getKey() + "." + basename + "." + anno_name

      seg_filename = anno_basename + "_seg.xml"
      feat_filename = anno_basename + "_multifeat.xml"

      new File(scriptParams.outputFolder + seg_filename).withWriter("UTF-8"){ out ->

	// Header
	out.writeLine('<?xml version="1.0" standalone="no"?>')
	out.writeLine('')
	out.writeLine('<!DOCTYPE paula SYSTEM "paula_mark.dtd">')
	out.writeLine('<paula version="1.1">')
	out.writeLine('')
	out.writeLine(/<header paula_id="${anno_basename}_seg"\/>/)
	out.writeLine('')
	out.writeLine(/<markList xmlns:xlink="http:\/\/www.w3.org\/1999\/xlink" type="${ annotation }" xml:base="${ basename }.tok.xml">/)

	AnnotationSet token = annotationSet.getValue().get(annotation)
	// Sort the annotations
	List<Annotation> list = new ArrayList<Annotation>(token);
	Collections.sort(list, new gate.util.OffsetComparator());
		
	i = 0
	list.groupBy([{ it.getFeatures().get("id", it.id).toString() }]).each { key, anno_list ->
	  xlink_strs = []
	  comment_str = []
	  anno_list.each { anno ->
	    AnnotationSet paula_toks_tmp = gate.Utils.getContainedAnnotations(doc.getAnnotations(temporaryAnnotationSet), anno, "tok");
	    List<Annotation> paula_toks = new ArrayList<Annotation>(paula_toks_tmp);
	    Collections.sort(paula_toks, new gate.util.OffsetComparator());
	    if(paula_toks.size() == 1) {
	      xlink_strs << /#tok_${paula_toks.toArray()[0].id}/
	      comment_str << gate.Utils.stringFor(doc, anno)
	    }
	    else if(paula_toks.size() > 1) {
	      xlink_strs << /#xpointer(id('tok_${paula_toks.first().id}')\/range-to(id('tok_${paula_toks.last().id}')))/
	      comment_str << gate.Utils.stringFor(doc, anno)
	    }
	    else {
	      // TODO: may not occur - show a warning
	    }
	  }
				   	
	  if (xlink_strs.size() > 0) {
	    xlink_str = xlink_strs.join(',')
	    if (xlink_strs.size() > 1) {
	      xlink_str = '(' + xlink_str + ')'
	    }
	    out.writeLine(/	<mark id="${anno_name}_${anno_list[0].id}" xlink:href="${xlink_str}"\/><!-- ${comment_str.join(' ')} -->/)
	  }
	}
						
	// Footer
	out.writeLine('</markList>')
	out.writeLine('</paula>')
							
      }

      new File(scriptParams.outputFolder + feat_filename).withWriter("UTF-8"){ out ->

	// Header
	out.writeLine('<?xml version="1.0" standalone="no"?>')
	out.writeLine('<!DOCTYPE paula SYSTEM "paula_multiFeat.dtd">')
	out.writeLine('')
	out.writeLine('<paula version="1.1">')
	out.writeLine(/<header paula_id="${anno_basename}_multifeat"\/>/)
	out.writeLine('')
	out.writeLine(/<multiFeatList xmlns:xlink="http:\/\/www.w3.org\/1999\/xlink" type="multiFeat" xml:base="${ seg_filename }">/)

	// TODO: Annotation sets and types as parameter
	AnnotationSet token = annotationSet.getValue().get(annotation)
	// Sort the annotations
	List<Annotation> list = new ArrayList<Annotation>(token);
	Collections.sort(list, new gate.util.OffsetComparator());
	list.groupBy([{ it.getFeatures().get("id", it.id).toString() }]).each { key, anno_list ->
	  anno = anno_list[0] // get the features from the first annotation
	  AnnotationSet paula_toks_tmp = gate.Utils.getContainedAnnotations(doc.getAnnotations(temporaryAnnotationSet), anno, "tok");
	  List<Annotation> paula_toks = new ArrayList<Annotation>(paula_toks_tmp);
	  Collections.sort(paula_toks, new gate.util.OffsetComparator());
	  if(paula_toks.size() >= 1) {
	    out.writeLine(/	<multiFeat xlink:href="#${anno_name}_${anno.id}"><!-- ${ gate.Utils.stringFor(doc, anno)} -->/)
	    features = anno.getFeatures()
	    if (features.isEmpty()) {
	      // empyt Feature Map: add the annotation type as feature
	      out.writeLine(/		<feat name="${anno_name}" value="${anno_name.replace('<', '&lt;').replace('/', '').replace('(', '').replace(')', '').replace(':', '').replace('&', '&amp;')}"\/>/)
	    }
	    else {
	      // add the features to a PAULA file
	      features.keySet().each { feature ->
		// keine leeren values - TODO: hierdurch kann auch eine leere FeatureMap entstehen, muss abgefangen werden
		if (features.get(feature).toString().replace('<', '&lt;').replace('/', '').replace('(', '').replace(')', '').replace(':', '').replace('&', '&amp;')) {
		  out.writeLine(/		<feat name="${anno_name}_${feature}" value="${features.get(feature).toString().replace('<', '&lt;').replace('/', '').replace('(', '').replace(')', '').replace(':', '').replace('&', '&amp;')}"\/>/)
		}
	      }
	    }
	    out.writeLine('	</multiFeat>')
	  }
	}
	
	// Footer
	out.writeLine('</multiFeatList>')
	out.writeLine('')
	out.writeLine('</paula>')
	
      }

    }}}
