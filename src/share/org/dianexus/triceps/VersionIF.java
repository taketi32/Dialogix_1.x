package org.dianexus.triceps;

import java.lang.String;

/*public*/ interface VersionIF {
    /*public*/ final static boolean DEBUG = true;
    /*public*/ final static boolean AUTHORABLE = true;
    /*public*/ final static boolean DEPLOYABLE = true;
    /*public*/ final static String VERSION_MAJOR = "2.4";
    /*public*/ final static String VERSION_MINOR = "0a";
    /*public*/ final static String VERSION_TYPE = ((AUTHORABLE && DEPLOYABLE) ? "Development System" : ((AUTHORABLE) ? "Authoring System" : ((DEPLOYABLE) ? "Interviewing System" : "Demo")));
    /*public*/ final static String VERSION_NAME = "Triceps " + VERSION_TYPE + " version " + VERSION_MAJOR + "." + VERSION_MINOR;
}
