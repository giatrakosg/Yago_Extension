package gr.uoa.di.kr.yagoextension.model;

import org.locationtech.jts.geom.Geometry;

import java.util.Set;

public class NBDEntity extends Entity {

  private String nbdID;
  private String name;
  private Double areasqkm;
  private Integer fcode;
  private Integer gnisID;
  private Integer population;
  private String stateName ;
  private Integer stateFips;
  private String hasSource ;
  private String fipsCode;

  public NBDEntity(String id, Set<String> labels, Geometry geom, String nbdID, Double areasqkm, Integer fcode, Integer gnisID,
                   Integer population, String stateName ,Integer stateFips, String hasSource, String fipsCode) {
    super(id, labels, geom);
    this.nbdID =nbdID ;
    this.areasqkm = areasqkm ;
    this.fcode = fcode ;
    this.gnisID = gnisID ;
    this.population = population ;
    this.stateName = stateName ;
    this.stateFips = stateFips ;
    this.hasSource = hasSource ;
    this.fipsCode = fipsCode ;

  }

  public String getNbdID() {
    return nbdID;
  }

  public String getName() {
    return name;
  }

  public Integer getFcode() {
    return fcode;
  }

  public Integer getGnisID() {
    return gnisID;
  }

  public Integer getPopulation() {
    return population;
  }

  public String getStateName() {
    return stateName;
  }

  public Integer getStateFips() {
    return stateFips;
  }

  public String getHasSource() {
    return hasSource;
  }

  public String getFipsCode() {
    return fipsCode;
  }
}
