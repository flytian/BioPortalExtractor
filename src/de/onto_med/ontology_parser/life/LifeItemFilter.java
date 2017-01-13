package de.onto_med.ontology_parser.life;

import org.apache.commons.lang3.StringUtils;

public class LifeItemFilter {

	public static void main(String[] args) {
		if (
			isUseless(new LifeItem("id", "35.", null))
			&& isUseless(new LifeItem("id", " - 15", null))
			&& isUseless(new LifeItem("id", "", null))
			&& isUseless(new LifeItem("id", " ", null))
			&& !isUseless(new LifeItem("id", "test", null))
		) {
			System.out.println("OK");
		} else {
			System.err.println("FAILED");
		}
	}
	
	public static boolean isUseless(LifeItem item) {
		return StringUtils.isBlank(item.getDescription().replaceAll("\u00A0", ""))
			|| !item.getDescription().matches(".*\\p{L}.*");
	}
}
