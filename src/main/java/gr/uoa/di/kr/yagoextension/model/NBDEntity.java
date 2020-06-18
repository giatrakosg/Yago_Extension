package gr.uoa.di.kr.yagoextension.model;

import org.locationtech.jts.geom.Geometry;

import java.util.Set;

public class NBDEntity extends Entity {
  private String gnisID;
  private double shapeLength;
  private double shapeArea;
  private String datasetDesc;
  private String fCode;

  public NBDEntity(String id, Set<String> labels, Geometry geom, String gnisID, double shapeLength, double shapeArea, String datasetDesc, String fCode) {
    super(id, labels, geom);
    this.gnisID = gnisID;
    this.shapeLength = shapeLength;
    this.shapeArea = shapeArea;
    this.datasetDesc = datasetDesc;
    this.fCode = fCode;
  }

  public String getGnisID() {
    return gnisID;
  }

  public double getShapeLength() {
    return shapeLength;
  }

  public double getShapeArea() {
    return shapeArea;
  }

  public String getDatasetDesc() {
    return datasetDesc;
  }

  public String getfCode() {
    return fCode;
  }
}
