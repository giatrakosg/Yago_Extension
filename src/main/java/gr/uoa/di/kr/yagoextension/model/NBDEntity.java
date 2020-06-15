package gr.uoa.di.kr.yagoextension.model;

import org.locationtech.jts.geom.Geometry;

import java.util.Set;

public class NBDEntity extends Entity {

  private String nbdID;
  private String name;
  private Double areasqkm;
  private Integer fcode;
  private String gnisID;
  private Integer population;
  private String stateName ;
  private Integer stateFips;
  private String hasSource ;
  private String fipsCode;

  public NBDEntity(String id, Set<String> labels, Geometry geom, String nbdID, Double areasqkm, Integer fcode, String gnisID,
                   Integer population, String name , String hasSource) {
    super(id, labels, geom);
    this.nbdID = nbdID ;
    this.areasqkm = areasqkm ;
    this.fcode = fcode ;
    this.gnisID = gnisID ;
    this.population = population ;
    this.name = name ;
    this.hasSource = hasSource ;

  }

  public String getNbdID() {
    return nbdID;
  }

  public Double getAreasqkm() {
    return areasqkm;
  }

  public String getName() {
    return name;
  }

  public Integer getFcode() {
    return fcode;
  }

  public String getGnisID() {
    return gnisID;
  }

  public Integer getPopulation() {
    return population;
  }

  public String getStateName() {
    return stateName;
  }

  public String getHasSource() {
    return hasSource;
  }

  public String getFipsCode() {
    return fipsCode;
  }
}
