package gr.uoa.di.kr.yagoextension.repositories;

import gr.uoa.di.kr.yagoextension.model.NBDEntity;
import gr.uoa.di.kr.yagoextension.readers.RDFReader;
import gr.uoa.di.kr.yagoextension.vocabulary.RDFVocabulary;
import gr.uoa.di.kr.yagoextension.vocabulary.YAGO2geoVocabulary;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Filter;

import static org.apache.jena.vocabulary.VCARD4.hasSource;

class NBDRepository extends Repository<NBDEntity> implements RDFReader {

  NBDRepository(String path) {
    super(path);
  }

  @Override
  public void read() {
    try {
      this.readSHP();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void readSHP() throws IOException {
      String nameProp = "NAME" ;

      File file = new File(this.inputFile);
      URL url = null;
      try {
          url = file.toURI().toURL();
      } catch (MalformedURLException e) {
          e.printStackTrace();
      }
      Map<String, Object> map = new HashMap<>();
      map.put("url", url);

      DataStore dataStore = DataStoreFinder.getDataStore(map);
      String typeName = dataStore.getTypeNames()[0];

      FeatureSource<SimpleFeatureType, SimpleFeature> source =
              dataStore.getFeatureSource(typeName);

      FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures();
      int id = 0 ;
      try (FeatureIterator<SimpleFeature> features = collection.features()) {
        while (features.hasNext()) {
          SimpleFeature feature = features.next();
          Set<String> labels = new HashSet<>();
          HashMap<String,String> properties = new HashMap<String,String>();

          for(org.opengis.feature.Property prop : feature.getProperties()){
              String col = prop.getName().toString();
              Object value = prop.getValue();
              if (col.equals(nameProp)){
                labels.add(value.toString());
              }else {
                  if (value == null){
                      properties.put(col, "nan");
                  } else {
                      properties.put(col,value.toString());
                  }
              }
          }
          entities.add(new NBDEntity(Integer.toString(id), labels, (Geometry) feature.getDefaultGeometry(), properties));
          id++;
        }
      }
      dataStore.dispose();

  }
  @Override
  public void readRDF
() {

    String base = "http://example.com/ontology#";

    String areaProp = base + "has_AREASQKM";
    String fcodeProp = base + "has_FCODE" ;
    String gnisidProp = base + "has_GNIS_ID" ;
    String sourceProp = base + "has_SOURCE_DAT" ;
    String nameProp = base + "has_GNIS_NAME" ;

    String hasGeometry = RDFVocabulary.HAS_GEOMETRY;
    String asWkt = RDFVocabulary.AS_WKT;

    InputStream inputstream = null;
    try {
      inputstream = new FileInputStream(this.inputFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    WKTReader wktReader = new WKTReader();
    Model nbd = ModelFactory.createDefaultModel();
    //Model nbd = RDFDataMgr.loadModel(this.inputFile, Lang.TTL);
    RDFDataMgr.read(nbd,inputstream,Lang.TTL);
    ResIterator subjects = nbd.listSubjects();
    /* iterate over the subjects of the input rdf file */
    while(subjects.hasNext()) {

      Resource subject = subjects.next();
      String subjectURI = subject.getURI();
      Set<String> labels = new HashSet<>();
      String nbdID = null;
      Double areasqkm = null;
      Integer fcode = null ;
      String gnisID = null ;
      Integer population = null ;
      String name = null ;
      //Integer stateFips = null ;
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
        if(predicate.equals(nameProp)){
          labels.add(object.asLiteral().getString());
          name = object.asLiteral().getString();
        }
        else if(predicate.equals(asWkt))
          wkt = object.asLiteral().getString();
        else if(predicate.equals(areaProp))
          areasqkm = object.asLiteral().getDouble();
        else if(predicate.equals(fcodeProp))
          fcode = object.asLiteral().getInt();
        else if(predicate.equals(sourceProp))
          hasSource = object.asLiteral().getString();
        else if(predicate.equals(gnisidProp))
          gnisID = object.asLiteral().getString();
        else if(predicate.equals(hasGeometry)) {
            try {
                wkt = nbd.listObjectsOfProperty(object.asResource(), ResourceFactory.createProperty(asWkt))
                        .next().asLiteral().getString().replace("<http://www.opengis.net/def/crs/EPSG/0/4326>", "");
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }

      }

      if(labels.size() == 0)
        continue;
      /* create a new entity and add it to the repository */
        //entities.add(new NBDEntity(subjectURI, labels, wktReader.read(wkt), areasqkm, population, name,
         //       hasSource));

    }

  }

  @Override
  public void generate(Map<String, String> matches, OutputStream datasetFile) {
    /*
    Model dataset = ModelFactory.createDefaultModel();
    Set<String> matchedEntities = matches.keySet();
    entities.
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

     */
  }
}
