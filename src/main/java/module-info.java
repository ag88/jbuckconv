module org.jbuckconv {
	requires org.jfree.jfreechart;
	requires commons.math3;
	requires transitive java.desktop;
	requires org.apache.logging.log4j.core;
	requires org.apache.logging.log4j;
	requires java.prefs;
	exports org.jbuckconv;
	exports org.jbuckconv.model;
}