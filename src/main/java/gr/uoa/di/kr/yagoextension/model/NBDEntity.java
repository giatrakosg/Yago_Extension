package gr.uoa.di.kr.yagoextension.model;

import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.Set;

public class NBDEntity extends Entity {
  private HashMap<String,String> properties ;

  public NBDEntity(String id, Set<String> labels, Geometry geom, HashMap<String, String> properties) {
    super(id, labels, geom);
    this.properties = properties;
  }

  public HashMap<String, String> getProperties() {
    return properties;
  }
}
