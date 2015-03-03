# PAULA XML-Exporter for GATE

The PAULA XML-Exporter is a GATE-Application that allows exporting a
GATE-Corpus into the PAULA XML-Format.

It was created by Fabian Barteld between 2013-2015 in the context of the DFG-funded project 
"Entwicklung der satzinternen Großschreibung im Deutschen. Eine
korpuslinguistische Studie zum Zusammenspiel kognitiv-semantischer und
syntaktischer Faktoren" (SIGS) headed by 
Renata Szczepaniak (Hamburg) and Klaus-Michael Köpcke (Münster).

## Installation

The exporter does not need to be installed. Just load the included gapp-file 
as an application in GATE by choosing "Restore Application from File..." 
in the Applications context menu.

## Usage

The PAULA XML-Exporter for GATE consists of a couple of Groovy-files
which have to be run sequentially. The included gapp-file sets up a
pipeline with the right order.

The scripts make only few assumptions about the data:
- If two annotations have an "id"-feature
  with an identical value they are exported as one span.
  This is used to allow the creation of discontinuous spans in GATE.
- It expects the documents in the corpus to have the features Place
  and Year (it is used to name the PAULA-Documents).
- The AnnotationSet "PAULA" is created to store temporary annotations
  and removed afterwards.
  Hence, your data should not include an AnnotationSet named PAULA.

To run the exporter successfully open the corpus pipeline and change the
"outputFolder"-Parameter for the scripts "PAULA\_Export Text", "PAULA\_Export Token",
"PAULA\_Export Annotation" and "PAULA\_Export Folder" to an existing destination.
Furthermore, you have to change to parameter "dtdFolder" for the
script "PAULA\_Export Folder" to
a folder containing the set of PAULA-DTD-Files.

The following sections give an overview over the scripts and what they do.

### Phase 1: Preparation

1. create\_PAULA\_token.groovy

As PAULA needs a tokenisation of the text but GATE does not
have the concept of a primary segmentation, one has to be created.
Here tokenisation means a non-overlapping segmentation of the text so that all the
other annotations can be modelled as joints of the token-segments
(i.e. the minimal segmentation according to the existing annotations).

Tokens that would span only white-space are omitted.

The script creates annotations of the type 'tok' in the
"outputAnnotationSet" - make sure, they do not exist. The other scripts
expect this to be named "PAULA". (This should be changed to a
parameter as well).

### Phase 2: Export

The actual export is divided into several steps (i.e. scripts):

1. PAULA\_export\_text.groovy  
   Exports the basic text.
2. PAULA\_export\_token.groovy  
   Exports the tokens created in phase 1.
3. PAULA\_export\_annotations.groovy  
   Exports the actual annotations that exist in GATE.
   AnnotationSets are mapped to Namespaces.
   The annotations themselves are mapped onto span markables, their
   features are mapped onto features.
   If no features exist, a feature is created in PAULA with the
   annotation type as value.
4. PAULA\_export\_segmentation\_layer.groovy  
   This step is done with ANNIS in mind.
   As the tokenisation created in the first step is only a formal
   segmentation that has no meaning, this steps adds features to given annotations that
   allow these annotations to act as searchable segmentation layer. 
   At the moment, the annotations used as segmentation layers are
   included as a list in the script. This should change in the future.
		
### Phase 3: Post-processing

There are two steps in post-processing. One involving the created
PAULA-Files, one the annotations in GATE:

1. PAULA\_create\_folder\_structure.groovy  
   This creates the corpus/ document-structure of folders used by
   PAULA. Furthermore it adds the dtd's to the directories.
2. remove\_PAULA\_token.groovy  
   Deletes the annotations created in the preparation-step.


## License

PAULA-XML-Exporter for GATE is free software: 
you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

PAULA-XML-Exporter for GATE is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see http://www.gnu.org/licenses/.

Der PAULA-XML-Exporter for GATE ist Freie Software: 
Sie können sie unter den Bedingungen der GNU General Public License, 
wie von der Free Software Foundation, Version 3 der Lizenz oder 
(nach Ihrer Wahl) jeder neueren veröffentlichten Version,
weiterverbreiten und/oder modifizieren.
Der PAULA-XML-Exporter for GATE wird in der Hoffnung, 
dass er nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; 
sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT
oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
Siehe die GNU General Public License für weitere Details.

Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses/.

