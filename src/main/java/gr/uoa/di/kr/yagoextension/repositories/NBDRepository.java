package gr.uoa.di.kr.yagoextension.repositories;

import gr.uoa.di.kr.yagoextension.model.NBDEntity;
import gr.uoa.di.kr.yagoextension.readers.RDFReader;
import gr.uoa.di.kr.yagoextension.vocabulary.RDFVocabulary;
import gr.uoa.di.kr.yagoextension.vocabulary.YAGO2geoVocabulary;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class NBDRepository extends Repository<NBDEntity> implements RDFReader {

  NBDRepository(String path) {
    super(path);
  }

  @Override
  public void read() {
    this.readRDF();
  }

  @Override
  public void readRDF() {

    String base = "http://example.com/ontology#";

    String areaProp = base + "has_AREASQKM";
    String fcodeProp = base + "has_FCODE" ;
    String gnisidProp = base + "has_GNIS_ID" ;
    String sourceProp = base + "has_SOURCE_DAT" ;
    String statenameProp = base + "has_STATE_NAME" ;

    String hasGeometry = RDFVocabulary.HAS_GEOMETRY;
    String asWkt = RDFVocabulary.AS_WKT;

    WKTReader wktReader = new WKTReader();
    Model nbd = RDFDataMgr.loadModel(this.inputFile, Lang.TTL);
    ResIterator subjects = nbd.listSubjects();
    /* iterate over the subjects of the input rdf file */
    while(subjects.hasNext()) {

      Resource subject = subjects.next();
      String subjectURI = subject.getURI();
      Set<String> labels = new HashSet<>();
      String nbdID = null;
      Double areasqkm = null;
      Integer fcode = null ;
      Integer gnisID = null ;
      Integer population = null ;
      String stateName = null ;
      Integer stateFips = null ;
      String hasSource = null ;


      String wkt = null;
      StmtIterator subjectStmts = nbd.listStatements(subject, null, (RDFNode) null);

      Integer id = 0 ;
      /* get the information that is available for the current entity */
      while (subjectStmts.hasNext()) {
        Statement stmt = subjectStmts.next();
        String predicate = stmt.getPredicate().getURI();
        RDFNode object = stmt.getObject();

        id++;
        nbdID = id.toString() ;
        if(predicate.equals(statenameProp))
          labels.add(object.asLiteral().getString());
        else if(predicate.equals(asWkt))
          wkt = object.asLiteral().getString();
        else if(predicate.equals(areaProp))
          areasqkm = object.asLiteral().getDouble();
        else if(predicate.equals(fcodeProp))
          fcode = object.asLiteral().getInt();
        else if(predicate.equals(statenameProp))
          stateName = object.asLiteral().getString();
        else if(predicate.equals(sourceProp))
          hasSource = object.asLiteral().getString();
        else if(predicate.equals(gnisidProp))
          gnisID = object.asLiteral().getInt();
        else if(predicate.equals(hasGeometry)) {
          wkt = nbd.listObjectsOfProperty(object.asResource(), ResourceFactory.createProperty(asWkt))
            .next().asLiteral().getString().replace("<http://www.opengis.net/def/crs/EPSG/0/4326>", "");
        }

      }

      if(labels.size() == 0)
        continue;
      /* create a new entity and add it to the repository */
      try {
        entities.add(new NBDEntity(subjectURI, labels, wktReader.read(wkt), nbdID, areasqkm, fcode, gnisID, population, stateName, stateFips,
                hasSource));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

  }

  @Override
  public void generate(Map<String, String> matches, OutputStream datasetFile) {

    Model dataset = ModelFactory.createDefaultModel();
    Set<String> matchedEntities = matches.keySet();

    Property area = ResourceFactory.createProperty(YAGO2geoVocabulary.ONTOLOGY, "hasOSNI_Area");
    Property areasqkm = ResourceFactory.createProperty(YAGO2geoVocabulary.ONTOLOGY, "hasOSNI_AreaSqKm");
    Property perimeter = ResourceFactory.createProperty(YAGO2geoVocabulary.ONTOLOGY, "hasOSNI_Perimeter");
    Property type = ResourceFactory.createProperty(RDFVocabulary.TYPE);
    Property name = ResourceFactory.createProperty(YAGO2geoVocabulary.ONTOLOGY, "hasONSI_Name");
    Property id = ResourceFactory.createProperty(YAGO2geoVocabulary.ONTOLOGY, "hasOSNI_ID");
    Property hasGeometry = ResourceFactory.createProperty(RDFVocabulary.HAS_GEOMETRY);
    Property asWKT = ResourceFactory.createProperty(RDFVocabulary.AS_WKT);

    for(NBDEntity nbdEntity : this.entities) {
      Resource subject;
      if(matchedEntities.contains(nbdEntity.getURI()))
        subject = ResourceFactory.createResource(matches.get(nbdEntity.getURI()));
      else
        subject = ResourceFactory.createResource(YAGO2geoVocabulary.RESOURCE+"nbdentity"+nbdEntity.getNbdID());
      Resource geometry = ResourceFactory.createResource(YAGO2geoVocabulary.ONTOLOGY+"Geometry_osni_"+nbdEntity.getNbdID());
      if(nbdEntity.getAreasqkm() != null)
        dataset.add(subject, area, ResourceFactory.createTypedLiteral(nbdEntity.getAreasqkm()));
      if(nbdEntity.getAreasqkm() != null)
        dataset.add(subject, areasqkm, ResourceFactory.createTypedLiteral(nbdEntity.getAreasqkm()));
      dataset.add(subject, id, ResourceFactory.createTypedLiteral(nbdEntity.getNbdID()));
      nbdEntity.getLabels().forEach(label -> dataset.add(subject, name, ResourceFactory.createStringLiteral(label)));
      //dataset.add(subject, type, ResourceFactory.createResource(YAGO2geoVocabulary.ONTOLOGY+nbdEntity.getDivision()));
      dataset.add(subject, hasGeometry, geometry);
      dataset.add(geometry, asWKT, ResourceFactory.createPlainLiteral(nbdEntity.getGeometry().toText()));
    }
    RDFDataMgr.write(datasetFile, dataset, RDFFormat.TURTLE_BLOCKS);
  }
}
