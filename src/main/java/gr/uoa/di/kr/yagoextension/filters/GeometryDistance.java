package gr.uoa.di.kr.yagoextension.filters;

import java.util.List;
import java.util.Set;

import gr.uoa.di.kr.yagoextension.model.LabelMatches;
import org.locationtech.jts.geom.Geometry;

import gr.uoa.di.kr.yagoextension.model.Entity;
import gr.uoa.di.kr.yagoextension.model.GeometryMatches;

/*
 *  Input: Matches produced by label similarity filter.
 *  Output: Matches between entities that are near to each other (threshold)
 *  Every entity can be matched with at most one other entity
 */

public class GeometryDistance {

	// original 0.2
	// incorps 5 w 9753 matches
	// incorps 20 w 11548
	public static double threshold = 0.2;
	
	public static GeometryMatches filter(LabelMatches labelMatches) {
		
		GeometryMatches geomMatches = new GeometryMatches();
		
		Set<Entity> lmKeys = labelMatches.getKeys();
		
		for(Entity yagoEntity : lmKeys) {
			Geometry yagoGeom = yagoEntity.getGeometry();
			List<Entity> datasourceEntities = labelMatches.getValueByKey(yagoEntity);
			double bestDist = threshold+1;
			Entity best = null;
			
			for(Entity datasourceEntity : datasourceEntities) {
				/*
				if (yagoGeom.within(datasourceEntity.getGeometry())){
					best = datasourceEntity ;
					break;
				}
				*/


				double curDist = yagoGeom.distance(datasourceEntity.getGeometry());
				if(curDist < bestDist) {
					bestDist = curDist;
					best = datasourceEntity;
				}
			}
			if (best != null) {
				geomMatches.addMatch(best, yagoEntity, bestDist);
			} else System.out.println("Didnt find a match for yago entity " + yagoEntity.getLabels().iterator().next() + " at " +
					yagoEntity.getGeometry().toText());
			/*
			if(bestDist <= threshold) {
				geomMatches.addMatch(best, yagoEntity, bestDist);
			}
			*/
			
		}
		return geomMatches;
	}

}
