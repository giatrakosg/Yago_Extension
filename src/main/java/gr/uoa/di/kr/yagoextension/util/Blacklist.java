package gr.uoa.di.kr.yagoextension.util;

import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import gr.uoa.di.kr.yagoextension.structures.Entity;

public class Blacklist {
	
	public static void removeMatchedEntities(Map<String, Entity> yago, Map<String, Entity> ds, String matchesFile) {

		Model matches = RDFDataMgr.loadModel(matchesFile);
		StmtIterator stmts = matches.listStatements();
		System.out.println(yago.size()+"|"+ds.size());
		while(stmts.hasNext()) {
			Statement stmt = stmts.next();
			String subj = stmt.getSubject().getURI();
			String objURI = stmt.getObject().asResource().getURI();
			String obj = objURI.split("/")[4].split(">")[0];
			yago.remove("<"+obj+">");
			ds.remove(subj);
		}
		System.out.println(yago.size()+"|"+ds.size());
	}

}