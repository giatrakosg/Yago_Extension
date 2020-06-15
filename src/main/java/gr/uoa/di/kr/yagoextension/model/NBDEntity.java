package gr.uoa.di.kr.yagoextension.model;

import org.locationtech.jts.geom.Geometry;

import java.util.Set;

public class NBDEntity extends Entity {
  private String name;
  private Double areasqkm;
  private Integer population;
  private String fipsCode;

  public NBDEntity(String id, Set<String> labels, Geometry geom, Double areasqkm, Integer population, String name , String fipsCode) {
    super(id, labels, geom);
    this.areasqkm = areasqkm ;
    this.fipsCode = fipsCode ;
    this.population = population ;
    this.name = name ;

  }


  public Double getAreasqkm() {
    return areasqkm;
  }

  public String getName() {
    return name;
  }

  public Integer getPopulation() {
    return population;
  }

  public String getFipsCode() {
    return fipsCode;
  }

  public String getNbdID() {
    return this.getURI();
  }
}
