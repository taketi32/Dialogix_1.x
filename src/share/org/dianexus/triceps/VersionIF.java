package org.dianexus.triceps;

import java.lang.String;

/*public*/ interface VersionIF extends BYSLicenseIF {
    /*public*/ final static boolean DEBUG = true;
    /*public*/ final static boolean AUTHORABLE = true;
    /*public*/ final static boolean DEPLOYABLE = true;
    /*public*/ final static boolean DEMOABLE = (!AUTHORABLE && !DEPLOYABLE);
    /*public*/ final static boolean DEVELOPERABLE = (AUTHORABLE && DEPLOYABLE);
    /*public*/ final static String VERSION_MAJOR = "2.7";
    /*public*/ final static String VERSION_MINOR = "1";
    /*public*/ final static String VERSION_TYPE = ((DEVELOPERABLE) ? "Development System" : ((AUTHORABLE) ? "Authoring System" : ((DEPLOYABLE) ? "Interviewing System" : "Demo")));
    /*public*/ final static String VERSION_NAME = STUDY_ALIAS + " version of Triceps " + VERSION_TYPE + " version " + VERSION_MAJOR + "." + VERSION_MINOR;
	/*public*/ final static String LICENSE_MSG = "This <B>" + VERSION_NAME +
		"</B> is <A HREF='/" + STUDY_ALIAS + "/triceps-license.html'>licensed</A> to " + PRINCIPAL_INVESTIGATOR + 
		" exclusively for the " + STUDY_NAME + 
		" [" + GRANT_NAME + 
		":  " + GRANT_TITLE + 
		"]";   
}
